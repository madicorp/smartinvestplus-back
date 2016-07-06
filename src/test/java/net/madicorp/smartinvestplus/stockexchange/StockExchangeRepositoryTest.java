package net.madicorp.smartinvestplus.stockexchange;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 23:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
public class StockExchangeRepositoryTest {
    @Rule
    public MongoDbRule remoteMongoDbRule =
        new MongoDbRule(mongoDb().port(RepositoryTestConfig.MONGO_PORT).databaseName("smartinvestplus").build());

    @Autowired
    private StockExchangeRepository subject;

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
}
