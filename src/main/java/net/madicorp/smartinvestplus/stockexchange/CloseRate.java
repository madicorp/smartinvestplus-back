package net.madicorp.smartinvestplus.stockexchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 00:08
 */
@Getter
@Setter
@ToString
public class CloseRate {
    @JsonIgnore
    private Security security;
    private LocalDate date;
    private Double rate;
}
