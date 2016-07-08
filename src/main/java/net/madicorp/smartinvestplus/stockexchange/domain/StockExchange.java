package net.madicorp.smartinvestplus.stockexchange.domain;

import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of={"symbol"})
@ToString(of = {"symbol", "name"})
public class StockExchange {
    @MongoId
    private String symbol;
    private String name;
}
