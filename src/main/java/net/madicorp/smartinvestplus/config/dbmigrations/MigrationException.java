package net.madicorp.smartinvestplus.config.dbmigrations;

/**
 * User: sennen
 * Date: 02/07/2016
 * Time: 22:06
 */
public class MigrationException extends RuntimeException {
    public MigrationException(String message, Exception cause) {
        super(message, cause);
    }
}
