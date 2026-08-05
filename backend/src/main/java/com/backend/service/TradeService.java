package com.backend.service;

import com.backend.dto.SellInvestmentRequestDTO;
import com.backend.dto.TradeResponseDTO;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.entity.Trade;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final InvestmentRepository investmentRepository;

    public TradeService(TradeRepository tradeRepository, InvestmentRepository investmentRepository) {
        this.tradeRepository = tradeRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional(readOnly = true)
    public List<TradeResponseDTO> getTradesByCustomer(Long customerId) {
        return tradeRepository.findByCustomerIdOrderByTradeDateDescIdDesc(customerId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void recordBuyTrade(Investment investment) {
        Portfolio portfolio = investment.getPortfolio();

        Trade trade = new Trade();
        trade.setPortfolio(portfolio);
        trade.setCustomer(portfolio.getCustomer());
        trade.setInvestmentId(investment.getId());
        trade.setAssetName(investment.getAssetName());
        trade.setAssetType(investment.getAssetType());
        trade.setTicker(investment.getTicker());
        trade.setTradeType("Buy");
        trade.setQuantity(investment.getQuantity());
        trade.setPrice(investment.getPurchasePrice());
        trade.setTradeDate(investment.getPurchaseDate() == null ? LocalDate.now() : investment.getPurchaseDate());
        trade.setRealisedPL(null);

        tradeRepository.save(trade);
    }

    @Transactional
    public TradeResponseDTO sellInvestment(Long investmentId, SellInvestmentRequestDTO request) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new InvestmentNotFoundException(investmentId));

        if (request.getQuantity().compareTo(investment.getQuantity()) > 0) {
            throw new IllegalArgumentException("Sell quantity cannot exceed current holding quantity");
        }

        Portfolio portfolio = investment.getPortfolio();
        BigDecimal realisedPL = request.getSellPrice().subtract(investment.getPurchasePrice())
                .multiply(request.getQuantity());

        Trade trade = new Trade();
        trade.setPortfolio(portfolio);
        trade.setCustomer(portfolio.getCustomer());
        trade.setInvestmentId(investment.getId());
        trade.setAssetName(investment.getAssetName());
        trade.setAssetType(investment.getAssetType());
        trade.setTicker(investment.getTicker());
        trade.setTradeType("Sell");
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getSellPrice());
        trade.setTradeDate(request.getTradeDate() == null ? LocalDate.now() : request.getTradeDate());
        trade.setRealisedPL(realisedPL);

        Trade savedTrade = tradeRepository.save(trade);

        BigDecimal remaining = investment.getQuantity().subtract(request.getQuantity());
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            investmentRepository.delete(investment);
        } else {
            investment.setQuantity(remaining);
            investmentRepository.save(investment);
        }

        return toResponseDTO(savedTrade);
    }

    private TradeResponseDTO toResponseDTO(Trade trade) {
        return new TradeResponseDTO(
                trade.getId(),
                trade.getPortfolio().getId(),
                trade.getCustomer().getId(),
                trade.getInvestmentId(),
                trade.getAssetName(),
                trade.getAssetType(),
                trade.getTicker(),
                trade.getTradeType(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTradeDate(),
                trade.getRealisedPL()
        );
    }
}
