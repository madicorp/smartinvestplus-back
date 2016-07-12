package net.madicorp.smartinvestplus.date;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * User: sennen
 * Date: 07/07/2016
 * Time: 21:12
 */
@Service
public class DateService {
    public LocalDate nextOpenDay(LocalDate date, Set<LocalDate> holidays) {
        date = date.plusDays(1);
        while(holidays.contains(date) ||
              DayOfWeek.SATURDAY.equals(date.getDayOfWeek()) ||
              DayOfWeek.SUNDAY.equals(date.getDayOfWeek())) {
            date = date.plusDays(1);
        }
        return date;
    }
}
