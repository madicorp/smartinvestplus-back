package net.madicorp.smartinvestplus.date;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 21:15
 */
public class DateServiceTest {
    private DateService subject = new DateService();

    @Test
    public void should_return_FEB_29_as_next_open_day_for_FEB_26() throws Exception {
        // GIVEN
        LocalDate february26 = LocalDate.of(2016, Month.FEBRUARY, 26);
        LocalDate february29 = LocalDate.of(2016, Month.FEBRUARY, 29);

        // WHEN
        LocalDate actual = subject.nextOpenDay(february26, Collections.emptySet());

        // THEN
        Assertions.assertThat(actual).isEqualTo(february29);
    }

    @Test
    public void should_return_MAR_1_as_next_open_day_for_FEB_26_if_FEB_29_is_holiday() throws Exception {
        // GIVEN
        LocalDate february26 = LocalDate.of(2016, Month.FEBRUARY, 26);
        LocalDate february29 = LocalDate.of(2016, Month.FEBRUARY, 29);

        // WHEN
        LocalDate actual =
            subject.nextOpenDay(february26, Collections.singleton(february29));

        // THEN
        Assertions.assertThat(actual).isEqualTo(LocalDate.of(2016, Month.MARCH, 1));
    }
}
