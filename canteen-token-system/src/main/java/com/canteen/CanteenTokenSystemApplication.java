package com.canteen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Canteen Token Management System
 * Main Application Entry Point
 * 
 * Features:
 * - Employee Management
 * - QR Code Token Generation
 * - Security Desk Check-in
 * - Canteen Counter Redemption
 */
@SpringBootApplication
@EnableScheduling
public class CanteenTokenSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CanteenTokenSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("  Canteen Token Management System");
        System.out.println("  Started Successfully!");
        System.out.println("========================================");
        System.out.println("  Access URLs:");
        System.out.println("  - Security Desk: http://localhost:8080/security");
        System.out.println("  - Canteen Counter: http://localhost:8080/canteen");
        System.out.println("  - Employee Management: http://localhost:8080/employees");
        System.out.println("  - Dashboard: http://localhost:8080/");
        System.out.println("========================================");
    }
}
