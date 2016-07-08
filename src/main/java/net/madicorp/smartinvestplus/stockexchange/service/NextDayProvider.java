package net.madicorp.smartinvestplus.stockexchange.service;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 15:01
 */
public interface NextDayProvider extends UnaryOperator<LocalDate> {
}
