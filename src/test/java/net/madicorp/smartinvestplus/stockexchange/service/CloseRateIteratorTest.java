package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.closeRate;
import static org.mockito.Mockito.mock;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 15:45
 */
public class CloseRateIteratorTest {

    @Test
    public void should_iterate_over_3_generated_values_and_2_existing_and_adjust_them() throws Exception {
        // GIVEN
        List<CloseRate> existingCloseRates = Arrays.asList(
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 9), 1005),
            closeRate(LocalDate.of(2016, Month.FEBRUARY, 11), 1010)
        );
        PreviousCloseRateProvider previousCloseRateProvider =
            (date) -> closeRate(LocalDate.of(2016, Month.FEBRUARY, 1), 990);
        NextDayProvider nextDayProvider = (date) -> date.plusDays(1);

        // WHEN
        CloseRateIterator actual = new CloseRateIterator(
            LocalDate.of(2016, Month.FEBRUARY, 8), LocalDate.of(2016, Month.FEBRUARY, 12),
            existingCloseRates.iterator(), nextDayProvider, previousCloseRateProvider, CloseRate::new,
            closeRate -> {
                closeRate.setRate(closeRate.getRate() / 2);
                return closeRate;
            });

        // THEN
        Assertions.assertThat((Iterator<CloseRate>) actual)
                  .containsOnly(
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 8), 495, true),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 9), 502.5, false),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 10), 502.5, true),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 11), 505, false),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 12), 505, true)
                  );
    }

}
