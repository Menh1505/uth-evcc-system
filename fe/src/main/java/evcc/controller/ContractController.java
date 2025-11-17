package evcc.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.CreateContractRequestDto;
import evcc.dto.request.CreateContractRequestDto.OwnershipRequestDto;
import evcc.dto.response.ContractResponseDto;
import evcc.dto.response.ContractSummaryResponseDto;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.exception.ApiException;
import evcc.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    private final UserService userService;

    public ContractController(UserService userService) {
        this.userService = userService;
    }

    private UserLoginResponse requireLogin(HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý hợp đồng.");
            return null;
        }
        if (currentUser.getToken() == null || currentUser.getToken().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return null;
        }
        return currentUser;
    }

    @GetMapping
    public String listMyContracts(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // Lấy các nhóm user đang là thành viên
            List<GroupResponseDto> groups = userService.getMyGroups(currentUser.getToken());
            model.addAttribute("title", "Hợp đồng của tôi - EVCC System");
            model.addAttribute("groups", groups);
            return "contracts/list";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/group/{groupId}")
    public String listContractsByGroup(@PathVariable Long groupId,
                                       Model model,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            List<ContractSummaryResponseDto> contracts =
                userService.getContractsByGroup(currentUser.getToken(), groupId);
            model.addAttribute("title", "Hợp đồng của nhóm - EVCC System");
            model.addAttribute("contracts", contracts);
            model.addAttribute("groupId", groupId);
            return "contracts/group-list";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/contracts";
        }
    }

    @GetMapping("/{id}")
    public String contractDetail(@PathVariable Long id,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            ContractResponseDto contract = userService.getContract(currentUser.getToken(), id);
            model.addAttribute("title", "Chi tiết hợp đồng - EVCC System");
            model.addAttribute("contract", contract);
            model.addAttribute("currentUsername", currentUser.getUsername());
            return "contracts/detail";
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/contracts";
        }
    }

    @PostMapping("/create")
    public String createContract(@RequestParam String title,
                                 @RequestParam(required = false) String description,
                                 @RequestParam Long groupId,
                                 @RequestParam("agreedPrice") String agreedPriceStr,
                                 @RequestParam(required = false) String signingDate,
                                 @RequestParam(required = false) String effectiveDate,
                                 @RequestParam(required = false) String expiryDate,
                                 @RequestParam(required = false) String termsAndConditions,
                                 @RequestParam(required = false) String notes,
                                 @RequestParam(name = "ownerUserId", required = false) List<String> ownerUserIds,
                                 @RequestParam(name = "ownerPercentage", required = false) List<String> ownerPercentages,
                                 @RequestParam(name = "ownerContribution", required = false) List<String> ownerContributions,
                                 @RequestParam(name = "ownerNotes", required = false) List<String> ownerNotes,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            if (title == null || title.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tiêu đề hợp đồng không được để trống.");
                return "redirect:/contracts";
            }

            BigDecimal agreedPrice;
            try {
                agreedPrice = new BigDecimal(agreedPriceStr.trim());
                if (agreedPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Giá thỏa thuận phải lớn hơn 0.");
                    return "redirect:/contracts";
                }
            } catch (Exception nfe) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giá thỏa thuận không hợp lệ.");
                return "redirect:/contracts";
            }

            LocalDate signingDateParsed = null;
            LocalDate effectiveDateParsed = null;
            LocalDate expiryDateParsed = null;
            try {
                if (signingDate != null && !signingDate.isBlank()) {
                    signingDateParsed = LocalDate.parse(signingDate);
                }
                if (effectiveDate != null && !effectiveDate.isBlank()) {
                    effectiveDateParsed = LocalDate.parse(effectiveDate);
                }
                if (expiryDate != null && !expiryDate.isBlank()) {
                    expiryDateParsed = LocalDate.parse(expiryDate);
                }
            } catch (Exception dpe) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ngày tháng không hợp lệ.");
                return "redirect:/contracts";
            }

            CreateContractRequestDto request = new CreateContractRequestDto();
            request.setTitle(title);
            request.setDescription(description);
            request.setGroupId(groupId);
            request.setAgreedPrice(agreedPrice);
            request.setSigningDate(signingDateParsed);
            request.setEffectiveDate(effectiveDateParsed);
            request.setExpiryDate(expiryDateParsed);
            request.setTermsAndConditions(termsAndConditions);
            request.setNotes(notes);

            // Build danh sách ownership từ form (tối đa vài dòng)
            List<OwnershipRequestDto> ownerships = new ArrayList<>();
            if (ownerUserIds != null) {
                for (int i = 0; i < ownerUserIds.size(); i++) {
                    String uid = ownerUserIds.get(i);
                    String percStr = ownerPercentages != null && ownerPercentages.size() > i ? ownerPercentages.get(i) : null;
                    String contribStr = ownerContributions != null && ownerContributions.size() > i ? ownerContributions.get(i) : null;
                    String note = ownerNotes != null && ownerNotes.size() > i ? ownerNotes.get(i) : null;

                    if (uid == null || uid.isBlank()) {
                        continue;
                    }

                    try {
                        OwnershipRequestDto owner = new OwnershipRequestDto();
                        owner.setUserId(UUID.fromString(uid.trim()));

                        if (percStr == null || percStr.isBlank()) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Tỉ lệ sở hữu không được để trống cho mỗi thành viên.");
                            return "redirect:/contracts";
                        }
                        BigDecimal perc = new BigDecimal(percStr.trim());
                        if (perc.compareTo(BigDecimal.ZERO) <= 0) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Tỉ lệ sở hữu phải lớn hơn 0.");
                            return "redirect:/contracts";
                        }
                        owner.setOwnershipPercentage(perc);

                        if (contribStr == null || contribStr.isBlank()) {
                            owner.setContributionAmount(BigDecimal.ZERO);
                        } else {
                            owner.setContributionAmount(new BigDecimal(contribStr.trim()));
                        }

                        owner.setNotes(note);
                        ownerships.add(owner);
                    } catch (IllegalArgumentException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "User ID không hợp lệ ở danh sách quyền sở hữu.");
                        return "redirect:/contracts";
                    } catch (Exception parseEx) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu quyền sở hữu không hợp lệ.");
                        return "redirect:/contracts";
                    }
                }
            }

            if (ownerships.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh sách quyền sở hữu không được để trống.");
                return "redirect:/contracts";
            }

            BigDecimal totalPerc = BigDecimal.ZERO;
            for (OwnershipRequestDto o : ownerships) {
                totalPerc = totalPerc.add(o.getOwnershipPercentage());
            }
            if (totalPerc.compareTo(BigDecimal.valueOf(100)) != 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tổng tỉ lệ sở hữu phải bằng 100%. Hiện tại: " + totalPerc);
                return "redirect:/contracts";
            }

            request.setOwnerships(ownerships);

            ContractResponseDto contract = userService.createContract(currentUser.getToken(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo hợp đồng \"" + contract.getTitle() + "\" thành công.");
            return "redirect:/contracts/" + contract.getId();
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/contracts";
        }
    }
}
