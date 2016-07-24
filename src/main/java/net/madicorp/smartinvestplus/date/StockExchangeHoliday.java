package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 21:58
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"stockExchangeSymbol", "date"})
public class StockExchangeHoliday {
    @JsonProperty("stock_exchange")
    private String stockExchangeSymbol;
    private LocalDate date;
}
