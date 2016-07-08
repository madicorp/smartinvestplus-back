package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 15:05
 */
public interface PreviousCloseRateProvider extends Function<LocalDate, CloseRate> {
}
