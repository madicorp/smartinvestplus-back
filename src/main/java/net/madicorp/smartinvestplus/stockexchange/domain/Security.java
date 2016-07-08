package net.madicorp.smartinvestplus.stockexchange.domain;

import lombok.*;
import org.jongo.marshall.jackson.oid.MongoId;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 00:08
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"symbol"})
@ToString
public class Security {
    @MongoId
    private String symbol;
    private String name;
    private boolean generated = false;
}
