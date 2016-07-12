package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 23:00
 */
@Repository
public class JongoCloseRateRepository implements CloseRateRepository {
    private static final String CLOSE_RATES = "close_rates";
    private Jongo jongo;

    @Inject
    public JongoCloseRateRepository(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public Iterator<CloseRate> findOneMonthToDateCloseRates(String stockExchangeSymbol, String securitySymbol,
                                                            LocalDate to) {
        LocalDate oneMonthEarlier = to.minusMonths(1);
        MongoCollection closeRates = jongo.getCollection(CLOSE_RATES);
        return closeRates.find("{" +
                               "   stock_exchange: #," +
                               "   security: #," +
                               "   date: {" +
                               "       '$gte': #," +
                               "       '$lte': #" +
                               "   }" +
                               "}", stockExchangeSymbol, securitySymbol, oneMonthEarlier, to).as(CloseRate.class);
    }

    @Override
    public Optional<CloseRate> findClosestCloseRateInPast(String stockExchangeSymbol, String securitySymbol,
                                                          LocalDate date) {
        MongoCollection closeRates = jongo.getCollection(CLOSE_RATES);
        MongoCursor<CloseRate> closestCloseRateInPast = closeRates.find("{" +
                                                                        "   stock_exchange: #," +
                                                                        "   security: #," +
                                                                        "   date: {" +
                                                                        "       '$lte': #" +
                                                                        "   }" +
                                                                        "}", stockExchangeSymbol, securitySymbol, date)
                                                                  .sort("{" +
                                                                        "    date: -1" +
                                                                        "}")
                                                                  .limit(1)
                                                                  .as(CloseRate.class);
        if (!closestCloseRateInPast.hasNext()) {
            return Optional.empty();
        }
        return Optional.of(closestCloseRateInPast.next());
    }

    @Override
    public void save(CloseRate[] generatedCloseRates) {
        MongoCollection closeRates = jongo.getCollection(CLOSE_RATES);
        closeRates.insert(generatedCloseRates);
    }
}
