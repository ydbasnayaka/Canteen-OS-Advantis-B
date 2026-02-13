package com.canteen.controller;

import com.canteen.service.EmployeeService;
import com.canteen.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dashboard Controller - Main dashboard and navigation
 */
@Controller
public class DashboardController {
    
    private final EmployeeService employeeService;
    private final TokenService tokenService;
    
    @Autowired
    public DashboardController(EmployeeService employeeService, TokenService tokenService) {
        this.employeeService = employeeService;
        this.tokenService = tokenService;
    }
    
    /**
     * Main Dashboard
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard - Canteen Token System");
        model.addAttribute("activeEmployees", employeeService.getActiveEmployeeCount());
        model.addAttribute("todayStats", tokenService.getTodayStats());
        model.addAttribute("todayDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        return "dashboard";
    }
    
    /**
     * Security Desk Page
     */
    @GetMapping("/security")
    public String securityDesk(Model model) {
        model.addAttribute("pageTitle", "Security Desk - Token Issuance");
        model.addAttribute("todayDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        return "security-desk";
    }
    
    /**
     * Canteen Counter Page
     */
    @GetMapping("/canteen")
    public String canteenCounter(Model model) {
        model.addAttribute("pageTitle", "Canteen Counter - Token Redemption");
        model.addAttribute("todayDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        return "canteen-counter";
    }
    
    /**
     * Employee Management Page
     */
    @GetMapping("/employees")
    public String employeeManagement(Model model) {
        model.addAttribute("pageTitle", "Employee Management");
        return "employee-management";
    }
    
    /**
     * Token History Page
     */
    @GetMapping("/history")
    public String tokenHistory(Model model) {
        model.addAttribute("pageTitle", "Token History");
        return "token-history";
    }
}
