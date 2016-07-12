package net.madicorp.smartinvestplus.stockexchange.repository;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import org.assertj.core.api.Assertions;
import org.jongo.Jongo;
import org.jongo.MongoCursor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.Optional;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.closeRate;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 23:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
public class JongoCloseRateRepositoryTest {
    @Rule
    public MongoDbRule remoteMongoDbRule =
        new MongoDbRule(mongoDb().port(RepositoryTestConfig.MONGO_PORT).databaseName("smartinvestplus").build());

    @Autowired
    private CloseRateRepository subject;

    @Autowired
    private Jongo jongo;

    private void insertCloseRates() throws Exception {
        jongo.getCollection("close_rates")
             .insert(
                 closeRate(LocalDate.of(2016, Month.JANUARY, 23), 990),
                 closeRate(LocalDate.of(2016, Month.FEBRUARY, 23), 1000),
                 closeRate(LocalDate.of(2016, Month.FEBRUARY, 26), 1005),
                 closeRate(LocalDate.of(2016, Month.MARCH, 15), 1010),
                 closeRate(LocalDate.of(2016, Month.MARCH, 20), 980),
                 closeRate(LocalDate.of(2016, Month.MARCH, 25), 1020)
             );
    }

    @Before
    public void resetCloseRates() throws Exception {
        jongo.getCollection("close_rates").remove().wasAcknowledged();
    }

    @Test
    public void should_return_close_rates_of_sec_1_for_one_month() throws Exception {
        // GIVEN
        insertCloseRates();
        LocalDate to = LocalDate.of(2016, Month.MARCH, 26);

        // WHEN
        Iterator<CloseRate> actual = subject.findOneMonthToDateCloseRates("BRVM", "sec_1", to);

        // THEN
        Assertions.assertThat(actual)
                  .containsOnly(
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 26), 1005),
                      closeRate(LocalDate.of(2016, Month.MARCH, 15), 1010),
                      closeRate(LocalDate.of(2016, Month.MARCH, 20), 980),
                      closeRate(LocalDate.of(2016, Month.MARCH, 25), 1020)
                  );
    }

    @Test
    public void should_return_feb_23_as_closest_rate_for_feb_25() throws Exception {
        // GIVEN
        insertCloseRates();
        LocalDate feb25 = LocalDate.of(2016, Month.FEBRUARY, 25);

        // WHEN
        Optional<CloseRate> actual = subject.findClosestCloseRateInPast("BRVM", "sec_1", feb25);

        // THEN
        Assertions.assertThat(actual).contains(closeRate(LocalDate.of(2016, Month.FEBRUARY, 23), 1000));
    }

    @Test
    public void should_return_empty_optional_as_closest_date_if_no_close_rate_in_past() throws Exception {
        // GIVEN
        insertCloseRates();
        LocalDate jan1 = LocalDate.of(2016, Month.JANUARY, 1);

        // WHEN
        Optional<CloseRate> actual = subject.findClosestCloseRateInPast("BRVM", "sec_1", jan1);

        // THEN
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    public void should_save_closed_rates() throws Exception {
        // GIVEN
        CloseRate[] closeRates = {
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 8), 990, true),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 9), 1005, false),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 10), 1005, true),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 11), 1010, false),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 12), 1010, true)
        };

        // WHEN
        subject.save(closeRates);

        // THEN
        MongoCursor<CloseRate> actual = jongo.getCollection("close_rates")
                                             .find()
                                             .as(CloseRate.class);
        Assertions.assertThat((Iterator<CloseRate>) actual).containsOnly(closeRates);
    }
}
