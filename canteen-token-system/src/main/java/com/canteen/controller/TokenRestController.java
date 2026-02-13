package com.canteen.controller;

import com.canteen.dto.ApiResponse;
import com.canteen.dto.TokenDTO;
import com.canteen.model.Token;
import com.canteen.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Token REST Controller - API endpoints for token management
 */
@RestController
@RequestMapping("/api/tokens")
@CrossOrigin(origins = "*")
public class TokenRestController {
    
    private final TokenService tokenService;
    
    @Autowired
    public TokenRestController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    /**
     * Issue new token
     */
    @PostMapping("/issue")
    public ResponseEntity<ApiResponse<TokenDTO>> issueToken(@RequestBody Map<String, String> request) {
        try {
            String employeeId = request.get("employeeId");
            String mealTypeStr = request.getOrDefault("mealType", "LUNCH");
            String issuedBy = request.getOrDefault("issuedBy", "Security Desk");
            
            Token.MealType mealType = Token.MealType.valueOf(mealTypeStr.toUpperCase());
            TokenDTO token = tokenService.issueToken(employeeId, mealType, issuedBy);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Token issued successfully", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Redeem token using QR code
     */
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<TokenDTO>> redeemToken(@RequestBody Map<String, String> request) {
        try {
            String qrCodeData = request.get("qrCodeData");
            String redeemedBy = request.getOrDefault("redeemedBy", "Canteen Staff");
            
            TokenDTO token = tokenService.redeemToken(qrCodeData, redeemedBy);
            return ResponseEntity.ok(ApiResponse.success("Token redeemed successfully", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Redeem token by token number
     */
    @PostMapping("/redeem-by-number")
    public ResponseEntity<ApiResponse<TokenDTO>> redeemTokenByNumber(@RequestBody Map<String, String> request) {
        try {
            String tokenNumber = request.get("tokenNumber");
            String redeemedBy = request.getOrDefault("redeemedBy", "Canteen Staff");
            
            TokenDTO token = tokenService.redeemTokenByNumber(tokenNumber, redeemedBy);
            return ResponseEntity.ok(ApiResponse.success("Token redeemed successfully", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get token by number
     */
    @GetMapping("/{tokenNumber}")
    public ResponseEntity<ApiResponse<TokenDTO>> getTokenByNumber(@PathVariable String tokenNumber) {
        try {
            TokenDTO token = tokenService.getTokenByNumber(tokenNumber);
            return ResponseEntity.ok(ApiResponse.success(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get employee tokens
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<TokenDTO>>> getEmployeeTokens(@PathVariable String employeeId) {
        List<TokenDTO> tokens = tokenService.getEmployeeTokens(employeeId);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
    
    /**
     * Get today's tokens
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<TokenDTO>>> getTodayTokens() {
        List<TokenDTO> tokens = tokenService.getTodayTokens();
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
    
    /**
     * Get today's tokens by status
     */
    @GetMapping("/today/{status}")
    public ResponseEntity<ApiResponse<List<TokenDTO>>> getTodayTokensByStatus(@PathVariable String status) {
        try {
            Token.TokenStatus tokenStatus = Token.TokenStatus.valueOf(status.toUpperCase());
            List<TokenDTO> tokens = tokenService.getTodayTokensByStatus(tokenStatus);
            return ResponseEntity.ok(ApiResponse.success(tokens));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status"));
        }
    }
    
    /**
     * Get today's stats
     */
    @GetMapping("/stats/today")
    public ResponseEntity<ApiResponse<TokenService.TokenStats>> getTodayStats() {
        TokenService.TokenStats stats = tokenService.getTodayStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    /**
     * Cancel token
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<TokenDTO>> cancelToken(@RequestBody Map<String, String> request) {
        try {
            String tokenNumber = request.get("tokenNumber");
            String reason = request.getOrDefault("reason", "Cancelled by admin");
            
            TokenDTO token = tokenService.cancelToken(tokenNumber, reason);
            return ResponseEntity.ok(ApiResponse.success("Token cancelled successfully", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
