package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.SecurityWithStockExchange;
import org.jongo.Jongo;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 10:42
 */
@Repository("stockExchangeRepository")
public class JongoStockExchangeRepository implements StockExchangeRepository {
    @Inject
    private Jongo jongo;

    public SecurityWithStockExchange findSecurity(String stockExchangeSymbol, String securitySymbol) {
        return jongo.getCollection("stock_exchange")
                    .aggregate("{" +
                               "    $unwind: '$securities'" +
                               "}")
                    .and("{" +
                         "   $match: {" +
                         "       'securities._id': '" + securitySymbol + "'," +
                         "       '_id': '" + stockExchangeSymbol + "'" +
                         "   }" +
                         "}")
                    .and("{" +
                         "   $project: {" +
                         "       '_id': '$securities._id'," +
                         "       'name': '$securities.name'," +
                         "       'stockExchange': {" +
                         "              'name':'$name'," +
                         "              '_id': '$_id'" +
                         "        }" +
                         "   }" +
                         "}")
                    .as(SecurityWithStockExchange.class)
                    .next();
    }
}
