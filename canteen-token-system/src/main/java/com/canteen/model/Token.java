package com.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Token Entity - Manages meal tokens
 * Each token is linked to an employee and has a unique QR code
 */
@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token_number", unique = true, nullable = false)
    private String tokenNumber;
    
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    
    @Column(name = "qr_code_data", unique = true, nullable = false, length = 1000)
    private String qrCodeData;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;
    
    @Column(name = "issue_time")
    private LocalDateTime issueTime;
    
    @Column(name = "redeem_time")
    private LocalDateTime redeemTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TokenStatus status = TokenStatus.ISSUED;
    
    @Column(name = "issued_by")
    private String issuedBy;
    
    @Column(name = "redeemed_by")
    private String redeemedBy;
    
    @Column(name = "remarks")
    private String remarks;
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACKS
    }
    
    public enum TokenStatus {
        ISSUED, REDEEMED, EXPIRED, CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        issueTime = LocalDateTime.now();
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
    }
}
