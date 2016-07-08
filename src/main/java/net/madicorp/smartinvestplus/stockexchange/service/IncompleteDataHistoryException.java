package net.madicorp.smartinvestplus.stockexchange.service;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 18:03
 */
public class IncompleteDataHistoryException extends RuntimeException {
    public IncompleteDataHistoryException(String message) {
        super(message);
    }
}
