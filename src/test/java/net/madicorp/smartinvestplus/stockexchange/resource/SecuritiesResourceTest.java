package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData;
import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.SecurityWithStockExchange;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.stockexchange.service.CloseRateService;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.closeRate;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.division;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 21:46
 */
@SpringApplicationConfiguration(StockExchangeResourceTestConfig.class)
public class SecuritiesResourceTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @Inject
    private static StockExchangeCRUDRepository mockCRUDRepo;
    @Inject
    private static StockExchangeRepository mockRepo;
    @Inject
    private static CloseRateService mockCloseRateService;

    @Test
    public void should_return_brvm_securities() throws Exception {
        // GIVEN
        when(mockCRUDRepo.findOne("BRVM")).thenReturn(StockExchangeMockData.stockExchange());

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/BRVM/securities");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .hasSize("$", 2)
                         .contains("$[0].symbol", "sec_1")
                         .contains("$[0].name", "Security 1")
                         .contains("$[0].links[0].rel", "self")
                         .contains("$[0].links[0].href", "/api/stock-exchanges/BRVM/securities/sec_1")
                         .contains("$[0].links[1].rel", "close-rates")
                         .contains("$[0].links[1].href", "/api/stock-exchanges/BRVM/securities/sec_1/close-rates");
    }

    @Test
    public void should_return_404_when_stock_exchange_is_not_found_for_security() throws Exception {
        // GIVEN
        when(mockCRUDRepo.findOne("BRVM")).thenReturn(null);

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/BRVM/securities");

        // THEN
        ResponseAssertion.assertThat(actual).notFound();
    }

    @Test
    public void should_return_sec_1_security() throws Exception {
        // GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(StockExchangeMockData.security());

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/BRVM/securities/sec_1");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.stockExchange.symbol", "BRVM")
                         .contains("$.stockExchange.name", "Bourse Régionale des VM")
                         .contains("$.symbol", "sec_1")
                         .contains("$.name", "Security 1");
    }

    @Test
    public void should_return_sec_1_close_rates() throws Exception {
        // GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(StockExchangeMockData.security());
        Iterator<CloseRate> closeRates = Arrays.asList(
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 8), 990, true),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 9), 1005, false),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 10), 1005, true),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 11), 1010, false),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 12), 1010, true)).iterator();
        when(mockCloseRateService.getOneMonthToDateCloseRates("BRVM", "sec_1", LocalDate.now()))
            .thenReturn(closeRates);

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/BRVM/securities/sec_1/close-rates");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .hasSize("$", 5)
                         .contains("$[0].date",
                                   LocalDate.of(2016, Month.FEBRUARY, 8).format(DateTimeFormatter.ISO_DATE))
                         .contains("$[0].rate", 990.)
                         .contains("$[0].generated", true);
        verify(mockCloseRateService).saveGenerated(closeRates);
    }

    @Test
    public void should_create_a_division() throws Exception {
        //GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(StockExchangeMockData.security());

        // WHEN
        String july122016 = LocalDate.of(2016, Month.JULY, 12).format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.put("/api/stock-exchanges/BRVM/securities/sec_1/divisions",
                                   Entity.json("{" +
                                               "    \"rate\": 0.4," +
                                               "    \"date\": \"" + july122016 + "\"" +
                                               "}"));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .created()
                         .location("/api/stock-exchanges/BRVM/securities/sec_1/divisions/20160712")
                         .contains("$.date", july122016)
                         .contains("$.rate", .4);
    }

    @Test
    public void should_return_bad_request_if_division_exists() throws Exception {
        //GIVEN
        SecurityWithStockExchange security = StockExchangeMockData.security();
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(security);
        LocalDate july122016 = LocalDate.of(2016, Month.JULY, 12);
        security.addDivision(division(july122016, .4));

        // WHEN
        Response actual = rule.put("/api/stock-exchanges/BRVM/securities/sec_1/divisions",
                                   Entity.json("{" +
                                               "    \"rate\": 0.4," +
                                               "    \"date\": \"" +
                                               july122016.format(DateTimeFormatter.ISO_DATE) + "\"" +
                                               "}"));


        // THEN
        ResponseAssertion.assertThat(actual).badRequest();
    }

    @Test
    public void should_return_404_when_security_is_not_found() throws Exception {
        // GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(null);

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/BRVM/securities/sec_1");

        // THEN
        ResponseAssertion.assertThat(actual).notFound();
    }

    @Test
    public void should_return_404_when_security_is_not_found_for_division() throws Exception {
        // GIVEN
        when(mockRepo.findSecurity("BRVM", "sec_1")).thenReturn(null);

        // WHEN
        String july122016 = LocalDate.of(2016, Month.JULY, 12).format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.put("/api/stock-exchanges/BRVM/securities/sec_1/divisions",
                                   Entity.json("{" +
                                               "    \"rate\": 0.4," +
                                               "    \"date\": \"" + july122016 + "\"" +
                                               "}"));

        // THEN
        ResponseAssertion.assertThat(actual).notFound();
    }

    @Test
    public void should_return_200_if_it_can_get_division_in_security() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.getDivision("brvm", "sec_1", july142016)).thenReturn(Optional.of(division(july142016, .4)));
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/brvm/securities/sec_1/divisions/" +
                                   july142016.format(DateTimeFormatter.BASIC_ISO_DATE));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.rate", .4)
                         .contains("$.date", july142016String);
    }

    @Test
    public void should_return_404_if_it_can_get_holiday_in_stock_exchange() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.getDivision("brvm", "sec_1", july142016)).thenReturn(Optional.empty());

        // WHEN
        Response actual = rule.get("/api/stock-exchanges/brvm/securities/sec_1/divisions/" +
                                   july142016.format(DateTimeFormatter.BASIC_ISO_DATE));

        // THEN
        ResponseAssertion.assertThat(actual).notFound();
    }
}
