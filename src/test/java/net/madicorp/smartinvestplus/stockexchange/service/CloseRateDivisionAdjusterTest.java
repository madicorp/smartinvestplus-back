package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;
import net.madicorp.smartinvestplus.stockexchange.domain.Division;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.util.TreeMap;

import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.closeRate;
import static net.madicorp.smartinvestplus.stockexchange.StockExchangeMockData.division;

/**
 * User: sennen
 * Date: 11/07/2016
 * Time: 19:56
 */
public class CloseRateDivisionAdjusterTest {
    @Test
    public void should_adjust_rate_to_half_of_its_initial_value() throws Exception {
        // GIVEN
        CloseRate closeRate = closeRate(LocalDate.of(2016, 7, 11), 1000, true);
        TreeMap<LocalDate, Division> divisions = new TreeMap<>();
        divisions.put(LocalDate.of(2016, 7, 13), division(LocalDate.of(2016, 7, 13), .2));
        divisions.put(LocalDate.of(2016, 6, 13), division(LocalDate.of(2016, 6, 13), .25));
        divisions.put(LocalDate.of(2016, 5, 13), division(LocalDate.of(2016, 5, 13), 2));
        CloseRateDivisionAdjuster subject = new CloseRateDivisionAdjuster(divisions);

        // WHEN
        CloseRate actual = subject.adjust(closeRate);

        // THEN
        Assertions.assertThat(actual)
                  .hasFieldOrPropertyWithValue("rate", 500.)
                  .hasFieldOrPropertyWithValue("date", closeRate.getDate())
                  .hasFieldOrPropertyWithValue("stockExchangeSymbol", closeRate.getStockExchangeSymbol())
                  .hasFieldOrPropertyWithValue("securitySymbol", closeRate.getSecuritySymbol())
                  .hasFieldOrPropertyWithValue("generated", closeRate.isGenerated());
    }
}
