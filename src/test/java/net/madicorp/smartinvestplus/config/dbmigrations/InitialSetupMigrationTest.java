package net.madicorp.smartinvestplus.config.dbmigrations;

import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.DB;
import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;
import org.assertj.core.api.Assertions;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.junit.*;

import java.time.LocalDate;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static net.madicorp.smartinvestplus.config.CommonDbConfiguration.jongoMapper;

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
        Assertions.assertThat((Iterable<Authority>) authorities).containsOnly(authority("ROLE_ADMIN"),
                                                                              authority("ROLE_USER"));
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
        MongoCollection stockExchanges = jongo.getCollection("stock_exchanges");
        StockExchangeWithSecurities brvm = stockExchanges.find().as(StockExchangeWithSecurities.class).next();
        Assertions.assertThat(brvm.getSymbol()).isEqualTo("BRVM");
        Assertions.assertThat(brvm.getName()).isEqualTo("Bourse régionale des valeurs Mobilières");
        Assertions.assertThat(brvm.getSecurities())
                  .extracting("symbol")
                  .contains("ABJC", "BICC", "BNBC");
    }

    @Test
    public void should_add_close_rates() throws Exception {
        // GIVEN

        // WHEN
        subject.addCloseRates(db);

        // THEN
        Jongo jongo = jongo();
        MongoCollection actual = jongo.getCollection("close_rates");
        LocalDate firstAirLiquideDate = actual.find("{" +
                                                    "   'stock_exchange': 'brvm'," +
                                                    "   'security': 'sivc'" +
                                                    "}")
                                              .limit(1)
                                              .as(CloseRate.class)
                                              .next()
                                              .getDate();
        Assertions.assertThat(firstAirLiquideDate).isEqualTo("2016-03-18");
        LocalDate lastAirLiguideDate = actual.find("{" +
                                                   "   'stock_exchange': 'brvm'," +
                                                   "   'security': 'sivc'" +
                                                   "}")
                                             .sort("{_id:-1}")
                                             .limit(1)
                                             .as(CloseRate.class)
                                             .next()
                                             .getDate();
        Assertions.assertThat(lastAirLiguideDate).isEqualTo("2008-08-01");
    }

    private Jongo jongo() {
        return new Jongo(db, jongoMapper());
    }

    private Authority authority(String role) {
        Authority authority = new Authority();
        authority.setName(role);
        return authority;
    }

}
