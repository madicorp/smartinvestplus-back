package net.madicorp.smartinvestplus.config.dbmigrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;
import de.undercouch.bson4jackson.BsonModule;
import net.madicorp.smartinvestplus.domain.Authority;
import net.madicorp.smartinvestplus.domain.User;
import net.madicorp.smartinvestplus.service.mustache.ListStringMustacheTemplate;
import net.madicorp.smartinvestplus.service.mustache.MustacheService;
import net.madicorp.smartinvestplus.stockexchange.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.Security;
import net.madicorp.smartinvestplus.stockexchange.StockExchangeWithSecurities;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {
    private static final Authority ROLE_ADMIN = new Authority("ROLE_ADMIN");
    private static final Authority ROLE_USER = new Authority("ROLE_USER");

    private final MustacheService mustacheService = new MustacheService();

    @ChangeSet(order = "01", author = "initiator", id = "01-addAuthorities")
    public void addAuthorities(DB db) {
        MongoCollection authorities = collection(db, "sip_authority");
        authorities.insert(ROLE_ADMIN);
        authorities.insert(ROLE_USER);
    }

    @ChangeSet(order = "02", author = "initiator", id = "02-addUsers")
    public void addUsers(DB db) {
        MongoCollection users = collection(db, "sip_user");
        users.ensureIndex(mongoIndex("login"));
        users.ensureIndex(mongoIndex("email"));
        users.insert(user("user-0", "system", "$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG", "",
                          "System", "system@localhost", true, "en", "system", ROLE_ADMIN, ROLE_USER));
        users.insert(user("user-1", "anonymousUser", "$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO",
                          "Anonymous", "User", "anonymous@localhost", true, "en", "system"));
        users.insert(user("user-2", "admin", "$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC", "admin",
                          "Administrator", "admin@localhost", true, "en", "system", ROLE_ADMIN, ROLE_USER));
        users.insert(user("user-3", "user", "$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K", "", "User",
                          "user@localhost", true, "en", "system", ROLE_USER));
    }

    @ChangeSet(order = "03", author = "initiator", id = "03-addStockExchanges")
    public void addStockExchangesAndTitles(DB db) {
        MongoCollection stockExchanges = collection(db, "stock_exchange");
        stockExchanges.ensureIndex(mongoIndex("_id", "titles"));
        StockExchangeWithSecurities brvm = new StockExchangeWithSecurities();
        brvm.setName("Bourse régionale des valeurs Mobilières");
        brvm.setSymbol("BRVM");

        ClassPathResource titlesResource = new ClassPathResource("config/mongobee/changeset-1/titles.csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(titlesResource.getFile()))) {
            reader.lines()
                  // Skip header
                  .skip(1)
                  .map(this::parseTitle)
                  .forEach((title) -> brvm.getSecurities().add(title));
        } catch (IOException e) {
            throw new MigrationException("Unexpected exception reading file", e);
        }

        stockExchanges.insert(brvm);
    }

    @ChangeSet(order = "04", author = "initiator", id = "04-addClosingPrices")
    public void addClosingPrices(DB db) {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] closePricesResources;
        try {
            closePricesResources =
                resourcePatternResolver.getResources("config/mongobee/changeset-1/close/*.csv");
        } catch (IOException e) {
            throw new MigrationException("Unexpected exception reading file", e);
        }
        for (Resource closePricesResource : closePricesResources) {
            insertClosingPrices(db, closePricesResource);
        }
    }

    private void insertClosingPrices(DB db, Resource closePricesResource) {
        String closePricesResourceFileName = closePricesResource.getFilename();
        MongoCollection closingPricesCollection =
            collection(db, closePricesResourceFileName.replace(".csv", "") + "_closing_prices");
        closingPricesCollection.ensureIndex(mongoIndex("date"));
        try (BufferedReader reader = new BufferedReader(new FileReader(closePricesResource.getFile()))) {
            CloseRate[] closeRates = reader.lines()
                                           // Skip header
                                           .skip(1)
                                           .map(this::parseClosingPrice)
                                           .toArray(CloseRate[]::new);
            closingPricesCollection.insert(closeRates);
        } catch (IOException e) {
            throw new MigrationException("Unexpected exception reading file", e);
        }
    }

    private CloseRate parseClosingPrice(String line) {
        String[] data = line.split(";");
        LocalDate date = LocalDate.parse(data[0], DateTimeFormatter.ofPattern("uuuu/MM/dd"));
        Double rate = new Double(data[1]);
        CloseRate closeRate = new CloseRate();
        closeRate.setDate(date);
        closeRate.setRate(rate);
        return closeRate;
    }

    private Security parseTitle(String line) {
        String[] data = line.split(";");
        Security security = new Security();
        security.setName(data[0]);
        security.setSymbol(data[1]);
        return security;
    }

    private static MongoCollection collection(DB db, String name) {
        Mapper mapper = new JacksonMapper.Builder().registerModule(new BsonModule())
                                                   .registerModule(new JavaTimeModule())
                                                   .setVisibilityChecker(new VisibilityChecker.Std(
                                                       JsonAutoDetect.Visibility.PUBLIC_ONLY).withFieldVisibility(
                                                       JsonAutoDetect.Visibility.NONE))
                                                   .build();
        Jongo jongo = new Jongo(db, mapper);
        return jongo.getCollection(name);
    }

    private String mongoIndex(String field, String... fields) {
        ListStringMustacheTemplate<String> indices;
        try {
            indices = mustacheService.compileList("mongo_indices");
        } catch (IOException e) {
            throw new MigrationException("Unable to find mongo_indices template", e);
        }
        ArrayList<String> indexFields = new ArrayList<>();
        indexFields.add(field);
        Collections.addAll(indexFields, fields);
        return indices.render(indexFields);
    }

    private static User user(String id, String login, String password, String firstName, String lastName,
                             String email, boolean activated, String langKey, String creator,
                             Authority... authorities) {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setActivated(activated);
        user.setLangKey(langKey);
        user.setCreatedBy(creator);
        user.setCreatedBy(creator);
        user.setCreatedDate(ZonedDateTime.now());
        if (authorities != null && authorities.length > 0) {
            user.setAuthorities(new HashSet<>(Arrays.asList(authorities)));
        }
        return user;
    }
}
