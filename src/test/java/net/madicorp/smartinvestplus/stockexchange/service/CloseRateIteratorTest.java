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

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 15:45
 */
public class CloseRateIteratorTest {

    @Test
    public void should_iterate_over_3_generated_values_and_2_existing() throws Exception {
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
            existingCloseRates.iterator(), nextDayProvider, previousCloseRateProvider, CloseRate::new);

        // THEN
        Assertions.assertThat((Iterator<CloseRate>) actual)
                  .containsOnly(
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 8), 990, true),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 9), 1005, false),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 10), 1005, true),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 11), 1010, false),
                      closeRate(LocalDate.of(2016, Month.FEBRUARY, 12), 1010, true)
                  );
    }

}
