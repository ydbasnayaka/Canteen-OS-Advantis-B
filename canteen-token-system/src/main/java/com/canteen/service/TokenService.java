package com.canteen.service;

import com.canteen.dto.TokenDTO;
import com.canteen.model.Employee;
import com.canteen.model.Token;
import com.canteen.repository.EmployeeRepository;
import com.canteen.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Token Service - Business logic for token management
 */
@Service
@Transactional
public class TokenService {
    
    private final TokenRepository tokenRepository;
    private final EmployeeRepository employeeRepository;
    private final QRCodeService qrCodeService;
    
    @Autowired
    public TokenService(TokenRepository tokenRepository, 
                       EmployeeRepository employeeRepository,
                       QRCodeService qrCodeService) {
        this.tokenRepository = tokenRepository;
        this.employeeRepository = employeeRepository;
        this.qrCodeService = qrCodeService;
    }
    
    /**
     * Issue new token to employee
     */
    public TokenDTO issueToken(String employeeId, Token.MealType mealType, String issuedBy) throws IOException {
        // Validate employee exists
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        
        if (!employee.getIsActive()) {
            throw new RuntimeException("Employee is not active");
        }
        
        // Check if token already issued for today
        LocalDate today = LocalDate.now();
        if (tokenRepository.existsByEmployeeIdAndIssueDateAndMealTypeAndStatus(
                employeeId, today, mealType, Token.TokenStatus.ISSUED)) {
            throw new RuntimeException("Token already issued for today");
        }
        
        // Generate token
        String tokenNumber = qrCodeService.generateTokenNumber();
        String qrData = qrCodeService.generateTokenData(employeeId, tokenNumber);
        String qrBase64 = qrCodeService.generateQRCodeBase64(qrData);
        
        Token token = new Token();
        token.setTokenNumber(tokenNumber);
        token.setEmployeeId(employeeId);
        token.setQrCodeData(qrData);
        token.setIssueDate(today);
        token.setMealType(mealType);
        token.setStatus(Token.TokenStatus.ISSUED);
        token.setIssuedBy(issuedBy);
        
        Token savedToken = tokenRepository.save(token);
        
        TokenDTO dto = convertToDTO(savedToken);
        dto.setQrCodeBase64(qrBase64);
        dto.setEmployeeName(employee.getFullName());
        dto.setEmployeeDepartment(employee.getDepartment());
        
        return dto;
    }
    
    /**
     * Redeem token using QR code data
     */
    public TokenDTO redeemToken(String qrCodeData, String redeemedBy) {
        Token token = tokenRepository.findByQrCodeData(qrCodeData)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        
        if (token.getStatus() == Token.TokenStatus.REDEEMED) {
            throw new RuntimeException("Token already redeemed");
        }
        
        if (token.getStatus() == Token.TokenStatus.EXPIRED) {
            throw new RuntimeException("Token has expired");
        }
        
        if (token.getStatus() == Token.TokenStatus.CANCELLED) {
            throw new RuntimeException("Token has been cancelled");
        }
        
        token.setStatus(Token.TokenStatus.REDEEMED);
        token.setRedeemTime(LocalDateTime.now());
        token.setRedeemedBy(redeemedBy);
        
        Token updatedToken = tokenRepository.save(token);
        return convertToDTO(updatedToken);
    }
    
    /**
     * Redeem token by token number
     */
    public TokenDTO redeemTokenByNumber(String tokenNumber, String redeemedBy) {
        Token token = tokenRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        if (token.getStatus() == Token.TokenStatus.REDEEMED) {
            throw new RuntimeException("Token already redeemed");
        }
        
        if (token.getStatus() == Token.TokenStatus.EXPIRED) {
            throw new RuntimeException("Token has expired");
        }
        
        token.setStatus(Token.TokenStatus.REDEEMED);
        token.setRedeemTime(LocalDateTime.now());
        token.setRedeemedBy(redeemedBy);
        
        Token updatedToken = tokenRepository.save(token);
        return convertToDTO(updatedToken);
    }
    
    /**
     * Get token by ID
     */
    @Transactional(readOnly = true)
    public TokenDTO getTokenById(Long id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        return convertToDTO(token);
    }
    
    /**
     * Get token by token number
     */
    @Transactional(readOnly = true)
    public TokenDTO getTokenByNumber(String tokenNumber) {
        Token token = tokenRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        return convertToDTO(token);
    }
    
    /**
     * Get all tokens for an employee
     */
    @Transactional(readOnly = true)
    public List<TokenDTO> getEmployeeTokens(String employeeId) {
        return tokenRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get today's tokens
     */
    @Transactional(readOnly = true)
    public List<TokenDTO> getTodayTokens() {
        return tokenRepository.findTodayTokens(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get today's tokens by status
     */
    @Transactional(readOnly = true)
    public List<TokenDTO> getTodayTokensByStatus(Token.TokenStatus status) {
        return tokenRepository.findByIssueDateAndStatus(LocalDate.now(), status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get today's stats
     */
    @Transactional(readOnly = true)
    public TokenStats getTodayStats() {
        LocalDate today = LocalDate.now();
        TokenStats stats = new TokenStats();
        stats.setTotalIssued(tokenRepository.countByIssueDateAndMealType(today, null));
        stats.setTotalRedeemed(tokenRepository.countByIssueDateAndStatus(today, Token.TokenStatus.REDEEMED));
        stats.setBreakfastIssued(tokenRepository.countByIssueDateAndMealType(today, Token.MealType.BREAKFAST));
        stats.setLunchIssued(tokenRepository.countByIssueDateAndMealType(today, Token.MealType.LUNCH));
        stats.setDinnerIssued(tokenRepository.countByIssueDateAndMealType(today, Token.MealType.DINNER));
        return stats;
    }
    
    /**
     * Cancel token
     */
    public TokenDTO cancelToken(String tokenNumber, String reason) {
        Token token = tokenRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        if (token.getStatus() == Token.TokenStatus.REDEEMED) {
            throw new RuntimeException("Cannot cancel redeemed token");
        }
        
        token.setStatus(Token.TokenStatus.CANCELLED);
        token.setRemarks(reason);
        
        Token updatedToken = tokenRepository.save(token);
        return convertToDTO(updatedToken);
    }
    
    /**
     * Convert Entity to DTO
     */
    private TokenDTO convertToDTO(Token token) {
        TokenDTO dto = new TokenDTO();
        dto.setId(token.getId());
        dto.setTokenNumber(token.getTokenNumber());
        dto.setEmployeeId(token.getEmployeeId());
        dto.setQrCodeData(token.getQrCodeData());
        dto.setIssueDate(token.getIssueDate());
        dto.setIssueTime(token.getIssueTime());
        dto.setRedeemTime(token.getRedeemTime());
        dto.setMealType(token.getMealType());
        dto.setStatus(token.getStatus());
        dto.setIssuedBy(token.getIssuedBy());
        dto.setRedeemedBy(token.getRedeemedBy());
        dto.setRemarks(token.getRemarks());
        
        if (token.getEmployee() != null) {
            dto.setEmployeeName(token.getEmployee().getFullName());
            dto.setEmployeeDepartment(token.getEmployee().getDepartment());
        }
        
        return dto;
    }
    
    /**
     * Token Stats inner class
     */
    public static class TokenStats {
        private long totalIssued;
        private long totalRedeemed;
        private long breakfastIssued;
        private long lunchIssued;
        private long dinnerIssued;
        
        public long getTotalIssued() { return totalIssued; }
        public void setTotalIssued(long totalIssued) { this.totalIssued = totalIssued; }
        
        public long getTotalRedeemed() { return totalRedeemed; }
        public void setTotalRedeemed(long totalRedeemed) { this.totalRedeemed = totalRedeemed; }
        
        public long getBreakfastIssued() { return breakfastIssued; }
        public void setBreakfastIssued(long breakfastIssued) { this.breakfastIssued = breakfastIssued; }
        
        public long getLunchIssued() { return lunchIssued; }
        public void setLunchIssued(long lunchIssued) { this.lunchIssued = lunchIssued; }
        
        public long getDinnerIssued() { return dinnerIssued; }
        public void setDinnerIssued(long dinnerIssued) { this.dinnerIssued = dinnerIssued; }
    }
}
