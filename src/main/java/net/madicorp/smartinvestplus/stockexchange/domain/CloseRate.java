package net.madicorp.smartinvestplus.stockexchange.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 00:08
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"stockExchangeSymbol", "securitySymbol", "date", "rate", "generated"})
public class CloseRate {
    @JsonProperty("stock_exchange")
    private String stockExchangeSymbol;
    @JsonProperty("security")
    private String securitySymbol;
    private LocalDate date;
    private Double rate;
    private boolean generated;
}
