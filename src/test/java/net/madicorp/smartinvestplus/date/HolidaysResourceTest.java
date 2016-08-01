package net.madicorp.smartinvestplus.date;

import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeRepository;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.mockito.Mockito;

import javax.inject.Inject;
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
@SpringApplicationConfiguration(DateResourceTestConfig.class)
public class HolidaysResourceTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    @Inject
    private static StockExchangeRepository mockRepo;

    @Before
    public void resetMock() throws Exception {
        Mockito.reset(mockRepo);
    }

    @Test
    public void should_add_holiday_and_return_201_if_does_not_exist_in_stock_exchange() throws Exception {
        // GIVEN
        when(mockRepo.getHolidays("BRVM")).thenReturn(Collections.emptySet());

        // WHEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.put("/api/stock-exchanges/brvm/holidays",
                                   Entity.json("\"" + july142016String + "\""));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .created()
                         .location("/api/stock-exchanges/brvm/holidays/20160714")
                         .contains("$.stock_exchange", "BRVM")
                         .contains("$.date", july142016String);
        verify(mockRepo).addHoliday("BRVM", july142016);
    }

    @Test
    public void should_return_400_and_if_holiday_exists_in_stock_exchange() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.getHolidays("BRVM")).thenReturn(Collections.singleton(july142016));

        // WHEN
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);
        Response actual = rule.put("/api/stock-exchanges/brvm/holidays", Entity.json("\"" + july142016String + "\""));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .badRequest();
        verify(mockRepo, never()).addHoliday("BRVM", july142016);
    }

    @Test
    public void should_return_200_if_it_can_get_holiday_in_stock_exchange() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.containsHoliday("BRVM", july142016)).thenReturn(true);
        String july142016String = july142016.format(DateTimeFormatter.ISO_DATE);

        // WHEN
        Response actual =
            rule.get("/api/stock-exchanges/brvm/holidays/" + july142016.format(DateTimeFormatter.BASIC_ISO_DATE));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.stock_exchange", "BRVM")
                         .contains("$.date", july142016String);
    }

    @Test
    public void should_return_404_if_it_can_get_holiday_in_stock_exchange() throws Exception {
        // GIVEN
        LocalDate july142016 = LocalDate.of(2016, Month.JULY, 14);
        when(mockRepo.containsHoliday("BRVM", july142016)).thenReturn(false);

        // WHEN
        Response actual =
            rule.get("/api/stock-exchanges/brvm/holidays/" + july142016.format(DateTimeFormatter.BASIC_ISO_DATE));

        // THEN
        ResponseAssertion.assertThat(actual).notFound();
    }
}
