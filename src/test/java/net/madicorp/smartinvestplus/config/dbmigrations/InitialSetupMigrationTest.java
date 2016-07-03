package net.madicorp.smartinvestplus.config.dbmigrations;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.DB;
import de.undercouch.bson4jackson.BsonModule;
import net.madicorp.smartinvestplus.stockexchange.ClosingPrice;
import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.stockexchange.StockExchange;
import org.assertj.core.api.Assertions;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.marshall.jackson.JacksonMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;

/**
 * User: sennen
 * Date: 01/07/2016
 * Time: 12:38
 */
public class InitialSetupMigrationTest {
    @ClassRule
    public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule().build();

    @Rule
    public MongoDbRule embeddedMongoDbRule = newMongoDbRule().defaultEmbeddedMongoDb("test");

    private final InitialSetupMigration subject = new InitialSetupMigration();
    private DB db;

    @Before
    public void initDb() throws Exception {
        db = embeddedMongoDbRule.getDatabaseOperation().connectionManager().getDB("test");
    }

    @Test
    public void should_add_authorities() throws Exception {
        // GIVEN

        // WHEN
        subject.addAuthorities(db);

        // THEN
        Jongo jongo = jongo();
        MongoCursor<Authority> authorities = jongo.getCollection("sip_authority").find().as(Authority.class);
        Assertions.assertThat((Iterable<Authority>) authorities).containsOnly(new Authority("ROLE_ADMIN"),
                                                                              new Authority("ROLE_USER"));
    }

    @Test
    public void should_add_all_users() throws Exception {
        // GIVEN

        // WHEN
        subject.addUsers(db);

        // THEN
        Jongo jongo = jongo();
        MongoCollection users = jongo.getCollection("sip_user");
        MongoCursor<String> logins = users.find()
                                          .map((result) -> result.get("login").toString());
        Assertions.assertThat((Iterable<String>) logins).containsOnly("system", "anonymoususer", "admin", "user");
    }

    @Test
    public void should_add_stock_exchanges() throws Exception {
        // GIVEN

        // WHEN
        subject.addStockExchangesAndTitles(db);

        // THEN
        Jongo jongo = jongo();
        MongoCollection stockExchanges = jongo.getCollection("stock_exchange");
        StockExchange brvm = stockExchanges.find().as(StockExchange.class).next();
        Assertions.assertThat(brvm.getName()).isEqualTo("Bourse régionale des valeurs Mobilières");
        Assertions.assertThat(brvm.getTitles())
                  .extracting("symbol")
                  .contains("ABJC", "BICC", "BNBC");
    }

    @Test
    public void should_add_closing_prices() throws Exception {
        // GIVEN

        // WHEN
        subject.addClosingPrices(db);

        // THEN
        Jongo jongo = jongo();
        MongoCollection airLiquideCiClosingPrices = jongo.getCollection("BRVM_SIVC_closing_prices");
        LocalDate firstAirLiquideDate = airLiquideCiClosingPrices.find()
                                                                 .limit(1)
                                                                 .as(ClosingPrice.class)
                                                                 .next()
                                                                 .getDate();
        Assertions.assertThat(firstAirLiquideDate).isEqualTo("2016-03-18");
        LocalDate lastAirLiguideDate = airLiquideCiClosingPrices.find()
                                                                .sort("{_id:-1}")
                                                                .limit(1)
                                                                .as(ClosingPrice.class)
                                                                .next()
                                                                .getDate();
        Assertions.assertThat(lastAirLiguideDate).isEqualTo("2008-08-01");
    }

    private Jongo jongo() {

        Mapper mapper = new JacksonMapper.Builder()
            .registerModule(new BsonModule())
            .registerModule(new JavaTimeModule())
            .build();
        return new Jongo(db, mapper);
    }

}
