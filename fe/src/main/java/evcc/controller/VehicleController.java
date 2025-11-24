package evcc.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import evcc.dto.request.CreateVehicleRequest;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.VehicleResponseDto;
import evcc.dto.response.VoteResponseDto;
import evcc.exception.ApiException;
import evcc.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String listVehicles(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem danh sách xe.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            List<VehicleResponseDto> vehicles = vehicleService.getAllVehicles(token);
            List<VehicleResponseDto> availableVehicles = vehicleService.getAvailableVehicles(token);
            List<GroupResponseDto> myGroups = vehicleService.getMyGroups(token);

            model.addAttribute("title", "Đồng sở hữu xe - EVCC System");
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("availableVehicles", availableVehicles);
            model.addAttribute("myGroups", myGroups);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("createVehicleRequest", new CreateVehicleRequest());
            return "vehicles/list";
        } catch (ApiException e) {
            logger.error("Không thể lấy danh sách xe: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/{vehicleId}")
    public String vehicleDetail(@PathVariable Long vehicleId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem chi tiết xe.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            VehicleResponseDto vehicle = vehicleService.getVehicleById(token, vehicleId);
            model.addAttribute("title", "Chi tiết xe - EVCC System");
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("currentUser", currentUser);
            return "vehicles/detail";
        } catch (ApiException e) {
            logger.error("Không thể lấy chi tiết xe: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/vehicles";
        }
    }

    @PostMapping("/create")
    public String createVehicle(@Valid @ModelAttribute CreateVehicleRequest request,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để tạo xe.");
            return "redirect:/auth/login";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            try {
                List<VehicleResponseDto> vehicles = vehicleService.getAllVehicles(token);
                List<VehicleResponseDto> availableVehicles = vehicleService.getAvailableVehicles(token);
                List<GroupResponseDto> myGroups = vehicleService.getMyGroups(token);

                model.addAttribute("title", "Đồng sở hữu xe - EVCC System");
                model.addAttribute("vehicles", vehicles);
                model.addAttribute("availableVehicles", availableVehicles);
                model.addAttribute("myGroups", myGroups);
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("createVehicleRequest", request);
                return "vehicles/list";
            } catch (ApiException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
                return "redirect:/vehicles";
            }
        }

        try {
            // Since Lombok is having issues, we'll use a simpler approach
            // Convert CreateVehicleRequest fields to VehiclePurchaseProposalRequest
            evcc.dto.request.VehiclePurchaseProposalRequest proposalRequest = new evcc.dto.request.VehiclePurchaseProposalRequest();

            // Use bean property access
            try {
                java.lang.reflect.Field nameField = request.getClass().getDeclaredField("name");
                nameField.setAccessible(true);
                proposalRequest.setName((String) nameField.get(request));

                java.lang.reflect.Field licensePlateField = request.getClass().getDeclaredField("licensePlate");
                licensePlateField.setAccessible(true);
                proposalRequest.setLicensePlate((String) licensePlateField.get(request));

                java.lang.reflect.Field makeField = request.getClass().getDeclaredField("make");
                makeField.setAccessible(true);
                proposalRequest.setMake((String) makeField.get(request));

                java.lang.reflect.Field modelField = request.getClass().getDeclaredField("model");
                modelField.setAccessible(true);
                proposalRequest.setModel((String) modelField.get(request));

                java.lang.reflect.Field yearField = request.getClass().getDeclaredField("year");
                yearField.setAccessible(true);
                proposalRequest.setYear((Integer) yearField.get(request));

                java.lang.reflect.Field groupIdField = request.getClass().getDeclaredField("groupId");
                groupIdField.setAccessible(true);
                proposalRequest.setGroupId((Long) groupIdField.get(request));

                java.lang.reflect.Field purchasePriceField = request.getClass().getDeclaredField("purchasePrice");
                purchasePriceField.setAccessible(true);
                proposalRequest.setPurchasePrice((BigDecimal) purchasePriceField.get(request));

                java.lang.reflect.Field purchaseDateField = request.getClass().getDeclaredField("purchaseDate");
                purchaseDateField.setAccessible(true);
                proposalRequest.setPurchaseDate((java.time.LocalDate) purchaseDateField.get(request));

                java.lang.reflect.Field batteryCapacityField = request.getClass().getDeclaredField("batteryCapacity");
                batteryCapacityField.setAccessible(true);
                Integer batteryCapacity = (Integer) batteryCapacityField.get(request);
                proposalRequest.setBatteryCapacity(batteryCapacity != null ? batteryCapacity.doubleValue() : null);

                java.lang.reflect.Field initialOdometerField = request.getClass().getDeclaredField("initialOdometer");
                initialOdometerField.setAccessible(true);
                proposalRequest.setInitialOdometer((Long) initialOdometerField.get(request));

                // Set voting config
                proposalRequest.setProposalDescription("Đề xuất mua xe mới cho nhóm");
                proposalRequest.setRequiredApprovalPercentage(new BigDecimal("75.00"));
                proposalRequest.setAllowMemberContribution(true);
                proposalRequest.setVoteEndTime(java.time.LocalDateTime.now().plusDays(7));

            } catch (Exception e) {
                logger.error("Error accessing request fields: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý dữ liệu form");
                return "redirect:/vehicles";
            }

            VoteResponseDto vote = vehicleService.proposeVehiclePurchase(token, proposalRequest);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã tạo đề xuất mua xe. Thành viên nhóm có thể bỏ phiếu tại trang voting.");
            return "redirect:/votes/" + vote.getId();
        } catch (ApiException e) {
            logger.error("Không thể tạo đề xuất xe: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
            return "redirect:/vehicles";
        }
    }

    @PostMapping("/{vehicleId}/delete")
    public String deleteVehicle(@PathVariable Long vehicleId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object currentUserObj = session.getAttribute("currentUser");
        if (!(currentUserObj instanceof UserLoginResponse currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xóa xe.");
            return "redirect:/auth/login";
        }

        // Kiểm tra quyền admin
        if (currentUser.getRoles() == null || !currentUser.getRoles().contains("ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ admin mới có quyền xóa xe.");
            return "redirect:/vehicles";
        }

        String token = currentUser.getToken();
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại.");
            session.invalidate();
            return "redirect:/auth/login";
        }

        try {
            vehicleService.deleteVehicle(token, vehicleId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa xe thành công.");
        } catch (ApiException e) {
            logger.error("Không thể xóa xe: {}", e.getErrorMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorMessage());
        }
        return "redirect:/vehicles";
    }
}
