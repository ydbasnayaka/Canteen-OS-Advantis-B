package com.canteen.service;

import com.canteen.dto.EmployeeDTO;
import com.canteen.model.Employee;
import com.canteen.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Employee Service - Business logic for employee management
 */
@Service
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    /**
     * Create new employee
     */
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmployeeId(employeeDTO.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }
        if (employeeDTO.getEmail() != null && employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        Employee employee = convertToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }
    
    /**
     * Update existing employee
     */
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        existingEmployee.setFullName(employeeDTO.getFullName());
        existingEmployee.setEmail(employeeDTO.getEmail());
        existingEmployee.setPhone(employeeDTO.getPhone());
        existingEmployee.setDepartment(employeeDTO.getDepartment());
        existingEmployee.setDesignation(employeeDTO.getDesignation());
        existingEmployee.setPhotoUrl(employeeDTO.getPhotoUrl());
        existingEmployee.setIsActive(employeeDTO.getIsActive());
        
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDTO(updatedEmployee);
    }
    
    /**
     * Get employee by ID
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return convertToDTO(employee);
    }
    
    /**
     * Get employee by employee ID
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        return convertToDTO(employee);
    }
    
    /**
     * Get all employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all active employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Search employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> searchEmployees(String search) {
        return employeeRepository.searchEmployees(search).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete employee
     */
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }
    
    /**
     * Check if employee exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(String employeeId) {
        return employeeRepository.existsByEmployeeId(employeeId);
    }
    
    /**
     * Get employee count
     */
    @Transactional(readOnly = true)
    public long getActiveEmployeeCount() {
        return employeeRepository.countByIsActiveTrue();
    }
    
    /**
     * Convert Entity to DTO
     */
    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFullName(employee.getFullName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setDepartment(employee.getDepartment());
        dto.setDesignation(employee.getDesignation());
        dto.setPhotoUrl(employee.getPhotoUrl());
        dto.setIsActive(employee.getIsActive());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convert DTO to Entity
     */
    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setPhotoUrl(dto.getPhotoUrl());
        employee.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return employee;
    }
}
