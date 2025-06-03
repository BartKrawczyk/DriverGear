package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.exception.ResourceAlreadyExistsException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.Gender;
import pl.programodawca.drivergear.service.ClothingAllowanceService;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.PositionClothingAllowanceService;
import pl.programodawca.drivergear.service.PositionService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administration/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final ClothingAllowanceService clothingAllowanceService;
    private final ClothingAssignmentService clothingAssignmentService;
    private final ClothingCompensationService clothingCompensationService;
    private final PositionClothingAllowanceService positionClothingAllowanceService;

    @GetMapping
    public String listEmployees(@RequestParam(required = false) Boolean showInactive,
                                @RequestParam(required = false) Long departmentId,
                                Model model) {
        Page<EmployeeDTO> employeesPage;

        if (departmentId != null) {
            employeesPage = employeeService.findAllByDepartment(departmentId, Pageable.unpaged());
        } else if (showInactive != null && showInactive) {
            employeesPage = employeeService.findAll(Pageable.unpaged());
        } else {
            employeesPage = employeeService.findAllByActive(true, Pageable.unpaged());
        }

        model.addAttribute("employees", employeesPage.getContent());
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        model.addAttribute("selectedDepartmentId", departmentId);
        return "administration/employees/employee-list";
    }

    @GetMapping("/new")
    public String showNewEmployeeForm(Model model) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setActive(true);
        employeeDTO.setHireDate(LocalDate.now());

        model.addAttribute("employeeDTO", employeeDTO);
        model.addAttribute("positions", positionService.findActivePositions());
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowego pracownika");

        return "administration/employees/employee-form";
    }

    @PostMapping("/new")
    public String createEmployee(@Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("positions", positionService.findActivePositions());
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowego pracownika");
            return "administration/employees/employee-form";
        }

        try {
            EmployeeDTO createdEmployee = employeeService.create(employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie dodany.");
            return "redirect:/administration/employees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas dodawania pracownika: " + e.getMessage());
            model.addAttribute("positions", positionService.findActivePositions());
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowego pracownika");
            return "administration/employees/employee-form";
        }
    }

    @GetMapping("/positions/by-department/{departmentId}")
    @ResponseBody
    public List<PositionDTO> getPositionsByDepartment(@PathVariable Long departmentId) {
        return positionService.findActivePositionsByDepartment(departmentId);
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            EmployeeDTO employeeDTO = employeeService.findById(id);

            model.addAttribute("employeeDTO", employeeDTO);
            model.addAttribute("positions", positionService.findActivePositions());
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja pracownika");

            return "administration/employees/employee-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/administration/employees";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateEmployee(@PathVariable Long id,
                                @Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("positions", positionService.findActivePositions());
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja pracownika");
            return "administration/employees/employee-form";
        }

        try {
            EmployeeDTO updatedEmployee = employeeService.update(id, employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie zaktualizowany.");
            return "redirect:/administration/employees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji pracownika: " + e.getMessage());
            model.addAttribute("positions", positionService.findActivePositions());
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja pracownika");
            return "administration/employees/employee-form";
        }
    }

    @PostMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pracownik został pomyślnie usunięty.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas usuwania pracownika: " + e.getMessage());
        }
        return "redirect:/administration/employees";
    }

    @PostMapping("/departments/create")
    @ResponseBody
    public ResponseEntity<?> createDepartment(@Valid @RequestBody CreateDepartmentDTO createDepartmentDTO) {
        try {
            DepartmentDTO createdDepartment = departmentService.createDepartment(createDepartmentDTO);
            return ResponseEntity.ok(createdDepartment);
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Wystąpił błąd podczas tworzenia działu: " + e.getMessage()));
        }
    }

    @PostMapping("/positions/create")
    @ResponseBody
    public ResponseEntity<?> createPosition(@Valid @RequestBody CreatePositionDTO createPositionDTO) {
        try {
            PositionDTO createdPosition = positionService.createPosition(createPositionDTO);
            return ResponseEntity.ok(createdPosition);
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Wystąpił błąd podczas tworzenia stanowiska: " + e.getMessage()));
        }
    }

    /**
     * Display the clothing overview for an employee.
     * 
     * @param id The ID of the employee
     * @param model The model to add attributes to
     * @param redirectAttributes For flash messages
     * @return The view name
     */
    @GetMapping("/{id}/clothing-overview")
    public String showEmployeeClothingOverview(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Get employee details
            EmployeeDTO employee = employeeService.findById(id);
            model.addAttribute("employee", employee);

            // Get position-based clothing allowance details
            List<PositionClothingAllowanceDTO> positionAllowances = 
                positionClothingAllowanceService.getPositionAllowancesByPositionId(employee.getPositionId());
            model.addAttribute("positionAllowances", positionAllowances);

            // Get employee clothing allowances
            List<ClothingAllowanceDTO> employeeAllowances = 
                clothingAllowanceService.getAllowancesByEmployeeId(id);
            model.addAttribute("employeeAllowances", employeeAllowances);

            // Get active employee clothing allowances
            List<ClothingAllowanceDTO> activeAllowances = 
                clothingAllowanceService.getAllowancesByEmployeeIdAndStatus(id, AllowanceStatus.ACTIVE);
            model.addAttribute("activeAllowances", activeAllowances);

            // Get issued clothing items
            List<ClothingAssignmentDTO> issuedItems = 
                clothingAssignmentService.getAssignmentsByEmployeeIdAndStatus(id, AssignmentStatus.ISSUED);
            model.addAttribute("issuedItems", issuedItems);

            // Get all assignments for the employee
            List<ClothingAssignmentDTO> allAssignments = 
                clothingAssignmentService.getAssignmentsByEmployeeId(id);
            model.addAttribute("allAssignments", allAssignments);

            // Get compensation history
            List<ClothingCompensationDTO> compensationHistory = 
                clothingCompensationService.getEmployeeCompensationHistory(id);
            model.addAttribute("compensationHistory", compensationHistory);

            // Calculate total compensation paid
            BigDecimal totalCompensationPaid = compensationHistory.stream()
                .filter(comp -> comp.getPaymentDate() != null)
                .map(ClothingCompensationDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("totalCompensationPaid", totalCompensationPaid);

            // Get pending compensations
            List<ClothingCompensationDTO> pendingCompensations = 
                clothingCompensationService.getPendingCompensations(id);
            model.addAttribute("pendingCompensations", pendingCompensations);

            // Calculate total pending compensation
            BigDecimal totalPendingCompensation = 
                clothingCompensationService.calculateTotalPendingCompensations(id);
            model.addAttribute("totalPendingCompensation", totalPendingCompensation);

            // Get most recent compensation date
            LocalDate mostRecentCompensationDate = compensationHistory.stream()
                .filter(comp -> comp.getPaymentDate() != null)
                .map(ClothingCompensationDTO::getPaymentDate)
                .max(LocalDate::compareTo)
                .orElse(null);
            model.addAttribute("mostRecentCompensationDate", mostRecentCompensationDate);

            return "administration/employees/employee-clothing-overview";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/administration/employees";
        }
    }

    // Helper class for error responses
    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
