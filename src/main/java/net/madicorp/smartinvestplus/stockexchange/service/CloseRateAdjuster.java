package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;

/**
 * User: sennen
 * Date: 11/07/2016
 * Time: 21:03
 */
public interface CloseRateAdjuster {
    CloseRate adjust(CloseRate closeRate);
}
