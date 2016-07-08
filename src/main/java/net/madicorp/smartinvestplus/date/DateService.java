package net.madicorp.smartinvestplus.date;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.SortedSet;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 21:12
 */
@Service
public class DateService {
    public LocalDate previousDayIn(SortedSet<LocalDate> dates, LocalDate date) {
        return dates.headSet(date).last();
    }

    public LocalDate nextOpenDay(LocalDate date) {
        int daysToAdd = 1;
        if (DayOfWeek.FRIDAY.equals(date.getDayOfWeek())) {
            daysToAdd = 3;
        }
        if (DayOfWeek.SATURDAY.equals(date.getDayOfWeek())) {
            daysToAdd = 2;
        }
        return date.plusDays(daysToAdd);
    }
}
