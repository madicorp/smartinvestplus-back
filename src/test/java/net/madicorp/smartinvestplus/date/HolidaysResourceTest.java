package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.test.HttpTestConfig;
import net.madicorp.smartinvestplus.test.HttpTestInjectBean;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * User: sennen
 * Date: 12/07/2016
 * Time: 22:05
 */
@HttpTestConfig(DateResourceTestConfig.class)
public class HolidaysResourceTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @HttpTestInjectBean
    private static StockExchangeRepository mockRepo;

    @Test
    public void should_return_201_and_add_holiday_if_does_not_exist() throws Exception {
        // GIVEN
        when(mockRepo.getHolidays("brvm")).thenReturn(Collections.emptySet());

        // WHEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.target("/api/stock-exchanges/brvm/holidays")
                              .request()
                              .put(Entity.json("\"" + july142016String + "\""));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .created()
                         .location("/api/stock-exchanges/brvm/holidays/20160714")
                         .contains("$.stock_exchange", "brvm")
                         .contains("$.date", july142016String);
        verify(mockRepo).addHoliday("brvm", july142016);
    }

    @Test
    public void should_return_400_and_if_holiday_does_not_exist() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.getHolidays("brvm")).thenReturn(Collections.singleton(july142016));

        // WHEN
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.target("/api/stock-exchanges/brvm/holidays")
                              .request()
                              .put(Entity.json("\"" + july142016String + "\""));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest();
        verify(mockRepo, never()).addHoliday("brvm", july142016);
    }
}
