package com.canteen.repository;

import com.canteen.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Token Repository - Database operations for Token entity
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findByTokenNumber(String tokenNumber);
    
    Optional<Token> findByQrCodeData(String qrCodeData);
    
    List<Token> findByEmployeeId(String employeeId);
    
    List<Token> findByEmployeeIdAndIssueDate(String employeeId, LocalDate issueDate);
    
    List<Token> findByIssueDate(LocalDate issueDate);
    
    List<Token> findByStatus(Token.TokenStatus status);
    
    List<Token> findByIssueDateAndStatus(LocalDate issueDate, Token.TokenStatus status);
    
    List<Token> findByMealTypeAndIssueDate(Token.MealType mealType, LocalDate issueDate);
    
    @Query("SELECT t FROM Token t WHERE t.employeeId = :employeeId AND t.issueDate = :issueDate AND t.mealType = :mealType")
    Optional<Token> findByEmployeeIdAndIssueDateAndMealType(
            @Param("employeeId") String employeeId,
            @Param("issueDate") LocalDate issueDate,
            @Param("mealType") Token.MealType mealType);
    
    @Query("SELECT COUNT(t) FROM Token t WHERE t.issueDate = :date AND t.mealType = :mealType")
    long countByIssueDateAndMealType(@Param("date") LocalDate date, @Param("mealType") Token.MealType mealType);
    
    @Query("SELECT COUNT(t) FROM Token t WHERE t.issueDate = :date AND t.status = :status")
    long countByIssueDateAndStatus(@Param("date") LocalDate date, @Param("status") Token.TokenStatus status);
    
    @Query("SELECT t FROM Token t WHERE t.issueDate = :date ORDER BY t.issueTime DESC")
    List<Token> findTodayTokens(@Param("date") LocalDate date);
    
    boolean existsByEmployeeIdAndIssueDateAndMealTypeAndStatus(
            String employeeId, LocalDate issueDate, Token.MealType mealType, Token.TokenStatus status);
}
