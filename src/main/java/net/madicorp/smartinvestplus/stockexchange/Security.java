package net.madicorp.smartinvestplus.stockexchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 00:08
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of={"symbol"})
@ToString
public class Security {
    @JsonIgnore
    private StockExchange stockExchange;
    private String name;
    private String symbol;
}
