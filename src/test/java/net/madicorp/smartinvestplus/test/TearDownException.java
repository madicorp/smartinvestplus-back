package net.madicorp.smartinvestplus.test;

/**
 * User: sennen
 * Date: 13/07/2016
 * Time: 08:40
 */
public class TearDownException extends RuntimeException {
    TearDownException(Exception cause) {
        super(cause);
    }
}
