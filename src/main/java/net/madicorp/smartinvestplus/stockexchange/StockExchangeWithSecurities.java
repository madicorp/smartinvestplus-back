package net.madicorp.smartinvestplus.stockexchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jongo.marshall.jackson.oid.MongoId;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 00:08
 */
@Document(collection = "stock_exchange")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"stockExchange"})
@ToString(of = {"stockExchange"})
public class StockExchangeWithSecurities {
    @JsonIgnore
    private StockExchange stockExchange = new StockExchange();
    private List<Security> securities = new ArrayList<>();

    public void setSymbol(String symbol) {
        stockExchange.setSymbol(symbol);
    }

    @NotNull
    @MongoId
    public String getSymbol() {
        return stockExchange.getSymbol();
    }

    @JsonProperty
    @NotNull
    public void setName(String name) {
        stockExchange.setName(name);
    }

    public String getName() {
        return stockExchange.getName();
    }
}
