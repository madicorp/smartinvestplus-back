package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import org.jongo.Jongo;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;

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
                         "       'securities._id': #," +
                         "       '_id': #" +
                         "   }" +
                         "}", securitySymbol, stockExchangeSymbol)
                    .and("{" +
                         "   $project: {" +
                         "       '_id': '$securities._id'," +
                         "       'name': '$securities.name'," +
                         "       'stockExchange': {" +
                         "              'name':'$name'," +
                         "              '_id': '$_id'" +
                         "        }," +
                         "        'divisions': '$securities.divisions'" +
                         "   }" +
                         "}")
                    .as(SecurityWithStockExchange.class)
                    .next();
    }

    @Override
    public void addDivision(SecurityWithStockExchange security, Division division) {
        jongo.getCollection("stock_exchange")
             .update(
                 "{" +
                 "   '_id': #," +
                 "   'securities._id': #" +
                 "}",
                 security.getStockExchange().getSymbol(),
                 security.getSymbol()
             )
             .upsert()
             .with(
                 "{" +
                 "  '$push': {" +
                 "      'securities.$.divisions': #" +
                 "  }" +
                 "}",
                 division
             );
    }

    @Override
    public Iterable<Division> getDivisions(SecurityWithStockExchange security, LocalDate to) {
        return jongo.getCollection("stock_exchange")
                    .aggregate("{" +
                               "    $unwind: '$securities'" +
                               "}")
                    .and("{" +
                         "   $match: {" +
                         "       'securities._id': #," +
                         "       '_id': #" +
                         "   }" +
                         "}", security.getSymbol(), security.getStockExchange().getSymbol())
                    .and("{" +
                         "    $unwind: '$securities.divisions'" +
                         "}")
                    .and("{" +
                         "   $match: {" +
                         "       'securities.divisions.date': {" +
                         "          '$lte': #" +
                         "       }" +
                         "   }" +
                         "}", to)
                    .and("{" +
                         "   $project: {" +
                         "       'date': '$securities.divisions.date'," +
                         "       'rate': '$securities.divisions.rate'" +
                         "   }" +
                         "}")
                    .as(Division.class);
    }
}
