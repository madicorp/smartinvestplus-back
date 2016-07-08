package net.madicorp.smartinvestplus.date;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.TreeSet;

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
        LocalDate actual = subject.nextOpenDay(february26);

        // THEN
        Assertions.assertThat(actual).isEqualTo(february29);
    }

    @Test
    public void should_return_FEB_24_as_previous_day_for_FEB_29_and_set_containing_FEB_23_24_and_MAR_3() throws Exception {
        // GIVEN
        TreeSet<LocalDate> dates = new TreeSet<>();
        LocalDate february26 = LocalDate.of(2016, Month.FEBRUARY, 24);
        dates.add(LocalDate.of(2016, Month.FEBRUARY, 23));
        dates.add(february26);
        dates.add(LocalDate.of(2016, Month.MARCH, 3));

        // WHEN
        LocalDate actual = subject.previousDayIn(dates, LocalDate.of(2016, Month.FEBRUARY, 29));

        // THEN
        Assertions.assertThat(actual).isEqualTo(february26);
    }

}
