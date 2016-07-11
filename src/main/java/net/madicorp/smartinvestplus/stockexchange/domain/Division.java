package net.madicorp.smartinvestplus.stockexchange.domain;

import lombok.*;

import java.time.LocalDate;

/**
 * User: sennen
 * Date: 10/07/2016
 * Time: 13:40
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"date"})
@ToString
public class Division implements Comparable<Division> {
    private LocalDate date;
    private Double rate;

    @Override
    public int compareTo(Division other) {
        if (other == null) {
            return 1;
        }
        return date.compareTo(other.getDate());
    }
}
