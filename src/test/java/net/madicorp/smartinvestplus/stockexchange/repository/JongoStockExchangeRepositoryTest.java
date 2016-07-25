package net.madicorp.smartinvestplus.stockexchange.repository;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.division;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.security;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 23:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
public class JongoStockExchangeRepositoryTest {
    @Rule
    public MongoDbRule remoteMongoDbRule =
        new MongoDbRule(mongoDb().port(RepositoryTestConfig.MONGO_PORT).databaseName("smartinvestplus").build());

    @Autowired
    private JongoStockExchangeRepository subject;

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_return_sec_1_stock_exchange() throws Exception {
        // GIVEN
        String stockExchangeSymbol = "BRVM";
        String securitySymbol = "sec_1";

        // WHEN
        SecurityWithStockExchange actual = subject.findSecurity(stockExchangeSymbol, securitySymbol);

        // THEN
        Assertions.assertThat(actual.getStockExchange())
                  .hasFieldOrPropertyWithValue("symbol", "BRVM")
                  .hasFieldOrPropertyWithValue("name", "Bourse Régionale des Valeurs Mobilières");
        Assertions.assertThat(actual)
                  .hasFieldOrPropertyWithValue("symbol", "sec_1")
                  .hasFieldOrPropertyWithValue("name", "Security 1");
    }

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_add_division_in_empty_divisions_array() throws Exception {
        // GIVEN
        SecurityWithStockExchange security = security();
        Division division = division(LocalDate.of(2016, 7, 10), .7);

        // WHEN
        subject.addDivision(security.getStockExchange().getSymbol(), security.getSymbol(), division);

        // THEN
        SecurityWithStockExchange actualSecurity =
            subject.findSecurity("BRVM", "sec_1");
        Assertions.assertThat(actualSecurity.getDivisions()).containsOnly(division);
        SecurityWithStockExchange sec2 =
            subject.findSecurity("BRVM", "sec_2");
        Assertions.assertThat(sec2.getDivisions()).doesNotContain(division);
    }

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_add_division_in_non_empty_divisions_array() throws Exception {
        // GIVEN
        SecurityWithStockExchange security = security(2);
        Division division = division(LocalDate.of(2016, 7, 11), .8);

        // WHEN
        subject.addDivision(security.getStockExchange().getSymbol(), security.getSymbol(), division);

        // THEN
        SecurityWithStockExchange actualSecurity =
            subject.findSecurity("BRVM", "sec_2");
        Assertions.assertThat(actualSecurity.getDivisions()).hasSize(2).contains(division);
        SecurityWithStockExchange sec2 =
            subject.findSecurity("BRVM", "sec_1");
        Assertions.assertThat(sec2.getDivisions()).doesNotContain(division);
    }

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_get_inserted_divisions() throws Exception {
        // GIVEN
        SecurityWithStockExchange security = security(2);
        LocalDate july72016 = LocalDate.of(2016, 7, 9);
        Division division = division(july72016, .8);
        String stockExchangeSymbol = security.getStockExchange().getSymbol();
        String securitySymbol = security.getSymbol();
        subject.addDivision(stockExchangeSymbol, securitySymbol, division);
        division = division(LocalDate.of(2016, 7, 10), .7);
        subject.addDivision(stockExchangeSymbol, securitySymbol, division);
        division = division(LocalDate.of(2016, 7, 8), .6);
        subject.addDivision(stockExchangeSymbol, securitySymbol, division);

        // WHEN
        Iterable<Division> actual = subject.getDivisions(stockExchangeSymbol, securitySymbol, july72016);

        // THEN
        Assertions.assertThat(actual)
                  .containsOnly(division(july72016, .8), division(LocalDate.of(2016, 7, 8), .6));
    }

    @Test
    public void should_add_holiday() throws Exception {
        // GIVEN
        LocalDate july62016 = LocalDate.of(2016, Month.JULY, 6);

        // WHEN
        subject.addHoliday("BRVM", july62016);

        // THEN;
        Assertions.assertThat(subject.getHolidays("BRVM")).containsOnly(july62016);
    }

    @Test
    public void should_be_able_to_tell_if_stock_exchange_has_given_holiday() throws Exception {
        // GIVEN
        LocalDate july62016 = LocalDate.of(2016, Month.JULY, 6);
        subject.addHoliday("BRVM", july62016);

        // WHEN
        boolean actual = subject.containsHoliday("BRVM", july62016);

        // THEN;
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_get_existing_division() throws Exception {
        // GIVEN
        SecurityWithStockExchange security = security();
        LocalDate divisionDate = LocalDate.of(2016, 7, 10);
        Division division = division(divisionDate, .7);
        subject.addDivision(security.getStockExchange().getSymbol(), security.getSymbol(), division);

        // WHEN
        Optional<Division> actual = subject.getDivision("BRVM", "sec_1", divisionDate);

        // THEN
        Assertions.assertThat(actual).contains(division);
    }

    @Test
    @UsingDataSet(locations = "/data/stock_exchanges.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void should_return_empty_optional_if_does_not_contain_given_division() throws Exception {
        // GIVEN
        LocalDate divisionDate = LocalDate.of(2016, 7, 10);

        // WHEN
        Optional<Division> actual = subject.getDivision("BRVM", "sec_1", divisionDate);

        // THEN
        Assertions.assertThat(actual).isEmpty();
    }
}
