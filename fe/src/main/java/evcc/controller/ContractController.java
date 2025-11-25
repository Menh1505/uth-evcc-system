package evcc.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.local.LocalContract;
import evcc.dto.local.VotingSession;
import evcc.exception.ApiException;
import evcc.service.UserLocalService;
import evcc.service.GroupLocalService;
import evcc.service.ContractLocalService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    // Helper class cho việc tính toán ownership
    private static class TempOwnership {

        final UUID userId;
        final String username;
        final BigDecimal contribution;
        final String note;

        TempOwnership(UUID userId, String username, BigDecimal contribution, String note) {
            this.userId = userId;
            this.username = username;
            this.contribution = contribution;
            this.note = note;
        }
    }

    private final UserLocalService userLocalService;
    private final GroupLocalService groupLocalService;
    private final ContractLocalService contractLocalService;

    public ContractController(UserLocalService userLocalService, GroupLocalService groupLocalService, ContractLocalService contractLocalService) {
        this.userLocalService = userLocalService;
        this.groupLocalService = groupLocalService;
        this.contractLocalService = contractLocalService;
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
            UUID currentUserId = UUID.fromString(currentUser.getUserId());
            List<GroupResponseDto> groups = groupLocalService.getMyGroups(currentUserId);

            // Lấy danh sách vehicle có sẵn cho việc tạo contract
            List<LocalContract.LocalVehicle> availableVehicles = contractLocalService.getAvailableVehicles();

            // Lấy demo users để hiển thị trong form
            Map<UUID, String> demoUsers = userLocalService.getAllUsers();

            model.addAttribute("title", "Hợp đồng của tôi - EVCC System");
            model.addAttribute("groups", groups);
            model.addAttribute("availableVehicles", availableVehicles);
            model.addAttribute("demoUsers", demoUsers);
            return "contracts/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lấy dữ liệu: " + e.getMessage());
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
            List<LocalContract> contracts = contractLocalService.getContractsByGroup(groupId);
            model.addAttribute("title", "Hợp đồng của nhóm - EVCC System");
            model.addAttribute("contracts", contracts);
            model.addAttribute("groupId", groupId);
            return "contracts/group-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể lấy danh sách hợp đồng: " + e.getMessage());
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
            LocalContract contract = contractLocalService.getContractById(id);
            VotingSession votingSession = null;
            try {
                votingSession = contractLocalService.getVotingSession(id);
            } catch (ApiException ignored) {
                // Không có voting session
            }

            model.addAttribute("title", "Chi tiết hợp đồng - EVCC System");
            model.addAttribute("contract", contract);
            model.addAttribute("votingSession", votingSession);
            model.addAttribute("currentUsername", currentUser.getUsername());
            model.addAttribute("currentUserId", currentUser.getUserId());
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
            @RequestParam Long vehicleId,
            @RequestParam("agreedPrice") String agreedPriceStr,
            @RequestParam(required = false) String termsAndConditions,
            @RequestParam(required = false) String notes,
            @RequestParam(name = "selectedUsers", required = false) List<String> selectedUsers,
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

            // Build danh sách ownership từ form - chỉ xử lý user được chọn
            List<LocalContract.LocalOwnership> ownerships = new ArrayList<>();
            BigDecimal totalContribution = BigDecimal.ZERO;

            if (selectedUsers != null && ownerUserIds != null) {
                // Bước 1: Thu thập tất cả đóng góp và tính tổng
                List<TempOwnership> tempOwnerships = new ArrayList<>();

                for (int i = 0; i < ownerUserIds.size(); i++) {
                    String uid = ownerUserIds.get(i);

                    // Chỉ xử lý user được chọn
                    if (selectedUsers.contains(uid)) {
                        String contribStr = ownerContributions != null && ownerContributions.size() > i ? ownerContributions.get(i) : null;
                        String note = ownerNotes != null && ownerNotes.size() > i ? ownerNotes.get(i) : null;

                        try {
                            UUID userId = UUID.fromString(uid.trim());
                            String username = userLocalService.getUsernameById(userId);

                            if (contribStr == null || contribStr.isBlank()) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Số tiền đóng góp không được để trống cho thành viên được chọn.");
                                return "redirect:/contracts";
                            }

                            BigDecimal contrib = new BigDecimal(contribStr.trim());
                            if (contrib.compareTo(BigDecimal.ZERO) <= 0) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Số tiền đóng góp phải lớn hơn 0.");
                                return "redirect:/contracts";
                            }

                            totalContribution = totalContribution.add(contrib);
                            tempOwnerships.add(new TempOwnership(userId, username, contrib, note));

                        } catch (IllegalArgumentException ex) {
                            redirectAttributes.addFlashAttribute("errorMessage", "User ID không hợp lệ ở danh sách quyền sở hữu.");
                            return "redirect:/contracts";
                        } catch (Exception parseEx) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu quyền sở hữu không hợp lệ.");
                            return "redirect:/contracts";
                        }
                    }
                }

                // Bước 2: Tính tỉ lệ % và tạo ownership objects
                for (TempOwnership temp : tempOwnerships) {
                    BigDecimal percentage = temp.contribution
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalContribution, 2, java.math.RoundingMode.HALF_UP);

                    LocalContract.LocalOwnership ownership = new LocalContract.LocalOwnership(
                            null, temp.userId, temp.username, percentage, temp.contribution, "PENDING", temp.note);
                    ownerships.add(ownership);
                }
            }

            if (ownerships.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Danh sách quyền sở hữu không được để trống.");
                return "redirect:/contracts";
            }

            // Lấy thông tin group từ local service
            GroupResponseDto group = groupLocalService.getGroupDetail(groupId);

            LocalContract contract = contractLocalService.createContract(
                    title, description, groupId, group.getName(),
                    vehicleId, agreedPrice, termsAndConditions, notes,
                    ownerships, UUID.fromString(currentUser.getUserId()), currentUser.getUsername()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Tạo hợp đồng \"" + contract.getTitle() + "\" thành công. Đang chờ voting từ các thành viên.");
            return "redirect:/contracts/" + contract.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo hợp đồng: " + e.getMessage());
            return "redirect:/contracts";
        }
    }

    @PostMapping("/{id}/vote")
    public String voteContract(@PathVariable Long id,
            @RequestParam String vote,
            @RequestParam(required = false) String reason,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            contractLocalService.voteContract(id, UUID.fromString(currentUser.getUserId()),
                    currentUser.getUsername(), vote, reason);

            String voteText = "APPROVE".equals(vote) ? "chấp nhận" : "từ chối";
            redirectAttributes.addFlashAttribute("successMessage", "Bạn đã " + voteText + " hợp đồng thành công.");
            return "redirect:/contracts/" + id;
        } catch (ApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/contracts/" + id;
        }
    }

    @GetMapping("/voting")
    public String listVotingSessions(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UserLoginResponse currentUser = requireLogin(session, redirectAttributes);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            List<VotingSession> votingSessions = contractLocalService.getVotingSessionsForUser(
                    UUID.fromString(currentUser.getUserId())
            );

            model.addAttribute("title", "Phiếu bầu chờ xử lý - EVCC System");
            model.addAttribute("votingSessions", votingSessions);
            return "contracts/voting-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể lấy danh sách phiếu bầu: " + e.getMessage());
            return "redirect:/contracts";
        }
    }
}
