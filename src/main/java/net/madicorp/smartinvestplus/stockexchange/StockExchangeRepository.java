package net.madicorp.smartinvestplus.stockexchange;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 14:32
 */
public interface StockExchangeRepository extends MongoRepository<StockExchangeWithSecurities, String> {
}
