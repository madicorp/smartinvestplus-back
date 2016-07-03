package net.madicorp.smartinvestplus.stockexchange;

import net.madicorp.smartinvestplus.security.AuthoritiesConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.ws.http.HTTPException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 * <p>
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@Component
@Path("/api")
public class StockExchangeResource {
    private final Logger log = LoggerFactory.getLogger(StockExchangeResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private StockExchangeRepository repository;

    /**
     * GET  /api/stock-exchanges/ : Get all stock exchanges places.
     *
     * @return JSONArray containing each stock exchange and the links to its securities
     */
    @Path("/stock-exchanges/")
    @GET
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ANONYMOUS)
    public JSONArray getStockExchanges() {
        log.debug("REST request to get stock exchanges");
        List<StockExchangeWithSecurities> stockExchanges = repository.findAll();
        return stockExchanges.stream()
                             .map(this::buildStockExchangeJSON)
                             .collect(JSONArray::new,
                                      JSONArray::put,
                                      JSONArray::put);
    }

    private JSONObject buildStockExchangeJSON(StockExchangeWithSecurities stockExchangeWithSecurities) {
        JSONObject stockExchangeJSON = new JSONObject(stockExchangeWithSecurities.getStockExchange());
        UriBuilder securityURIBuilder = uriInfo.getAbsolutePathBuilder()
                                               .path(stockExchangeWithSecurities.getSymbol())
                                               .path("securities")
                                               .path("{arg1}");
        List<String> links = stockExchangeWithSecurities.getSecurities()
                                                        .stream()
                                                        .map(security -> securityURIBuilder.build(security.getSymbol()))
                                                        .map(URI::getPath)
                                                        .collect(Collectors.toList());
        try {
            stockExchangeJSON.put("links", links);
        } catch (JSONException e) {
            throw new HTTPException(500);
        }
        return stockExchangeJSON;
    }
}
