package com.backend.repository;

import com.backend.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByCustomerIdOrderByTradeDateDescIdDesc(Long customerId);
}
