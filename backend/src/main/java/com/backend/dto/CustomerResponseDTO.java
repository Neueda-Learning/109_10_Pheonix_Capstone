package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String riskProfile;
    private String investmentGoal;
    private String notes;
    private LocalDateTime joinedDate;
    private String status;
    private BigDecimal portfolioValue;
    private BigDecimal totalInvestment;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private double returnPercentage;
    private Map<String, BigDecimal> targetAllocation;

    public CustomerResponseDTO() {
    }

    public CustomerResponseDTO(Long id, String name, String email, String phone, String riskProfile,
                                String investmentGoal, String notes, LocalDateTime joinedDate, String status,
                                BigDecimal portfolioValue, BigDecimal totalInvestment,
                                BigDecimal currentValue, BigDecimal profitLoss, double returnPercentage,
                                Map<String, BigDecimal> targetAllocation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.riskProfile = riskProfile;
        this.investmentGoal = investmentGoal;
        this.notes = notes;
        this.joinedDate = joinedDate;
        this.status = status;
        this.portfolioValue = portfolioValue;
        this.totalInvestment = totalInvestment;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.returnPercentage = returnPercentage;
        this.targetAllocation = targetAllocation;
    }

    public CustomerResponseDTO(Long id, String name, String email, String phone, String riskProfile,
                                String investmentGoal, LocalDateTime joinedDate,
                                BigDecimal portfolioValue, BigDecimal totalInvestment,
                                BigDecimal currentValue, BigDecimal profitLoss, double returnPercentage) {
        this(id, name, email, phone, riskProfile, investmentGoal, null, joinedDate, "ACTIVE",
                portfolioValue, totalInvestment, currentValue, profitLoss, returnPercentage,
                Collections.emptyMap());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public String getInvestmentGoal() {
        return investmentGoal;
    }

    public void setInvestmentGoal(String investmentGoal) {
        this.investmentGoal = investmentGoal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(BigDecimal totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(double returnPercentage) {
        this.returnPercentage = returnPercentage;
    }

    public Map<String, BigDecimal> getTargetAllocation() {
        return targetAllocation;
    }

    public void setTargetAllocation(Map<String, BigDecimal> targetAllocation) {
        this.targetAllocation = targetAllocation;
    }
}
