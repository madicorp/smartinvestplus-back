package net.madicorp.smartinvestplus.stockexchange;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClosingPrice {
    private LocalDate date;
    private Double rate;
}
