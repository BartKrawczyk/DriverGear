package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administration/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

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

}

