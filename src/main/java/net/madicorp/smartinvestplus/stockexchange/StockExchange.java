package net.madicorp.smartinvestplus.stockexchange;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jongo.marshall.jackson.oid.MongoId;
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
public class StockExchange {

    @NotNull
    @MongoId
    private String symbol;

    @NotNull
    private String name;

    private List<Title> titles = new ArrayList<>();
}
