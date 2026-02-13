package com.canteen.dto;

import com.canteen.model.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Token Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    
    private Long id;
    private String tokenNumber;
    private String employeeId;
    private String employeeName;
    private String employeeDepartment;
    private String qrCodeData;
    private String qrCodeBase64;
    private LocalDate issueDate;
    private LocalDateTime issueTime;
    private LocalDateTime redeemTime;
    private Token.MealType mealType;
    private Token.TokenStatus status;
    private String issuedBy;
    private String redeemedBy;
    private String remarks;
}
