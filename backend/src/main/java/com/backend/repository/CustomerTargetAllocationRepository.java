package com.backend.repository;

import com.backend.entity.CustomerTargetAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerTargetAllocationRepository extends JpaRepository<CustomerTargetAllocation, Long> {

    List<CustomerTargetAllocation> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long customerId);
}
