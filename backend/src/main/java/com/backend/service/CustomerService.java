package com.backend.service;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.entity.Customer;
import com.backend.entity.CustomerTargetAllocation;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.CustomerTargetAllocationRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;
    private final InvestmentRepository investmentRepository;
    private final CustomerTargetAllocationRepository targetAllocationRepository;

    public CustomerService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository,
                            InvestmentRepository investmentRepository,
                            CustomerTargetAllocationRepository targetAllocationRepository) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
        this.investmentRepository = investmentRepository;
        this.targetAllocationRepository = targetAllocationRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setRiskProfile(request.getRiskProfile());
        customer.setInvestmentGoal(request.getInvestmentGoal());
        customer.setNotes(request.getNotes());
        customer.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "Active" : request.getStatus());

        Customer saved = customerRepository.save(customer);

        Portfolio portfolio = new Portfolio();
        portfolio.setCustomer(saved);
        portfolioRepository.save(portfolio);

        saveTargetAllocation(saved, request.getTargetAllocation());

        return toResponseDTO(saved);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!customer.getEmail().equalsIgnoreCase(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setRiskProfile(request.getRiskProfile());
        customer.setInvestmentGoal(request.getInvestmentGoal());
        customer.setNotes(request.getNotes());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            customer.setStatus(request.getStatus());
        }

        Customer saved = customerRepository.save(customer);
        saveTargetAllocation(saved, request.getTargetAllocation());

        return toResponseDTO(saved);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.delete(customer);
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customer.getId());
        BigDecimal totalInvestment = investments.stream()
            .map(i -> i.getQuantity().multiply(i.getPurchasePrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentValue = investments.stream()
                .map(i -> i.getQuantity().multiply(i.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal profitLoss = currentValue.subtract(totalInvestment);
        double returnPercentage = totalInvestment.compareTo(BigDecimal.ZERO) > 0
            ? profitLoss.multiply(BigDecimal.valueOf(100)).divide(totalInvestment, 4, RoundingMode.HALF_UP).doubleValue()
            : 0.0;

        Map<String, BigDecimal> targetAllocation = targetAllocationRepository.findByCustomerId(customer.getId()).stream()
                .collect(Collectors.toMap(
                        CustomerTargetAllocation::getAssetType,
                        CustomerTargetAllocation::getTargetPercentage,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRiskProfile(),
                customer.getInvestmentGoal(),
                customer.getNotes(),
                customer.getCreatedDate(),
                customer.getStatus() == null || customer.getStatus().isBlank() ? "Active" : customer.getStatus(),
                currentValue,
                totalInvestment,
                currentValue,
                profitLoss,
                returnPercentage,
                targetAllocation
        );
    }

    private void saveTargetAllocation(Customer customer, Map<String, BigDecimal> targetAllocation) {
        targetAllocationRepository.deleteByCustomerId(customer.getId());
        if (targetAllocation == null || targetAllocation.isEmpty()) {
            return;
        }

        for (Map.Entry<String, BigDecimal> entry : targetAllocation.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            BigDecimal value = entry.getValue();
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }

            CustomerTargetAllocation allocation = new CustomerTargetAllocation();
            allocation.setCustomer(customer);
            allocation.setAssetType(entry.getKey());
            allocation.setTargetPercentage(value);
            targetAllocationRepository.save(allocation);
        }
    }
}
