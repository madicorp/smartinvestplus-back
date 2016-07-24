package net.madicorp.smartinvestplus.stockexchange.repository;

import com.mongodb.DBObject;
import net.madicorp.smartinvestplus.date.StockExchangeHoliday;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public void addDivision(String stockExchangeSymbol, String securitySymbol, Division division) {
        jongo.getCollection("stock_exchange")
             .update(
                 "{" +
                 "   '_id': #," +
                 "   'securities._id': #" +
                 "}",
                 stockExchangeSymbol,
                 securitySymbol
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
    public Iterable<Division> getDivisions(String stockExchangeSymbol, String securitySymbol, LocalDate to) {
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

    @Override
    public void addHoliday(String stockExchangeSymbol, LocalDate holiday) {
        jongo.getCollection("stock_exchange")
             .update(
                 "{" +
                 "   '_id': #" +
                 "}",
                 stockExchangeSymbol
             )
             .upsert()
             .with(
                 "{" +
                 "  '$addToSet': {" +
                 "      'holidays': #" +
                 "  }" +
                 "}",
                 holiday
             );
    }

    @Override
    public Set<LocalDate> getHolidays(String stockExchangeSymbol) {
        Aggregate.ResultsIterator<StockExchangeHoliday> holidays =
            jongo.getCollection("stock_exchange")
                 .aggregate("{" +
                            "    '$match': {" +
                            "        '_id': #" +
                            "    }" +
                            "}",
                            stockExchangeSymbol)
                 .and("{" +
                      " '$unwind': '$holidays'" +
                      "}")
                 .and("{" +
                      " '$project': {" +
                      "     '_id': 0," +
                      "     'stock_exchange': '$_id'," +
                      "     'date': '$holidays'" +
                      " }" +
                      "}")
                 .as(StockExchangeHoliday.class);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(holidays, Spliterator.ORDERED), true)
                            .map(StockExchangeHoliday::getDate)
                            .collect(Collectors.toSet());
    }

    @Override
    public boolean containsHoliday(String stockExchangeSymbol, LocalDate holiday) {
        return jongo.getCollection("stock_exchange")
                    .find("{" +
                          "     '_id': #," +
                          "     'holidays': #" +
                          "}", stockExchangeSymbol, holiday)
                    .as(DBObject.class)
                    .hasNext();
    }

    @Override
    public Optional<Division> getDivision(String stockExchangeSymbol, String securitySymbol, LocalDate divisionDate) {
        Aggregate.ResultsIterator<Division> division = jongo.getCollection("stock_exchange")
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
                                                                 "    $unwind: '$securities.divisions'" +
                                                                 "}")
                                                            .and("{" +
                                                                 "   $match: {" +
                                                                 "       'securities.divisions.date': #" +
                                                                 "   }" +
                                                                 "}", divisionDate)
                                                            .and("{" +
                                                                 "   $project: {" +
                                                                 "       'date': '$securities.divisions.date'," +
                                                                 "       'rate': '$securities.divisions.rate'" +
                                                                 "   }" +
                                                                 "}")
                                                            .as(Division.class);
        return division.hasNext() ? Optional.of(division.next()) : Optional.empty();
    }
}
