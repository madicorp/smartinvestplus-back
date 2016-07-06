package net.madicorp.smartinvestplus.stockexchange.repository;

import net.madicorp.smartinvestplus.stockexchange.StockExchangeWithSecurities;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 14:32
 */
public interface StockExchangeCRUDRepository extends MongoRepository<StockExchangeWithSecurities, String> {
}
