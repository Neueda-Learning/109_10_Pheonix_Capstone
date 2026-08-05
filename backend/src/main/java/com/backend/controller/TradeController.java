package com.backend.controller;

import com.backend.dto.TradeResponseDTO;
import com.backend.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Trades", description = "Trade history APIs")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/customers/{id}/trades")
    @Operation(summary = "Get customer trade history", description = "Returns all buy/sell trades for a customer")
    @Parameter(name = "id", description = "Customer ID", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trades fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TradeResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public List<TradeResponseDTO> getTradesByCustomer(@PathVariable Long id) {
        return tradeService.getTradesByCustomer(id);
    }
}
