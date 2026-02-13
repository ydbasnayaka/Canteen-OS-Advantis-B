package com.canteen.controller;

import com.canteen.dto.ApiResponse;
import com.canteen.dto.EmployeeDTO;
import com.canteen.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Employee REST Controller - API endpoints for employee management
 */
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeRestController {
    
    private final EmployeeService employeeService;
    
    @Autowired
    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    /**
     * Create new employee
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO created = employeeService.createEmployee(employeeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Employee created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
            return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get employee by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(ApiResponse.success(employee));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get employee by Employee ID
     */
    @GetMapping("/emp-id/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeByEmployeeId(employeeId);
            return ResponseEntity.ok(ApiResponse.success(employee));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all employees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    /**
     * Get active employees
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getActiveEmployees() {
        List<EmployeeDTO> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    /**
     * Search employees
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> searchEmployees(@RequestParam String query) {
        List<EmployeeDTO> employees = employeeService.searchEmployees(query);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    /**
     * Delete employee (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Check if employee exists
     */
    @GetMapping("/exists/{employeeId}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmployeeExists(@PathVariable String employeeId) {
        boolean exists = employeeService.existsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
