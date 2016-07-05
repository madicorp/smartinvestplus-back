package net.madicorp.smartinvestplus.stockexchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Path("/api")
public class StockExchangeResource {
    private final Logger log = LoggerFactory.getLogger(StockExchangeResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private StockExchangeRepository repository;

    @Inject
    private StockExchangeService stockExchangeService;

    /**
     * GET  /api/stock-exchanges/ : Get all stock exchanges places.
     *
     * @return JSONArray containing each stock exchange and the links to its securities
     */
    @Path("/stock-exchanges/")
    @GET
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public JSONArray getStockExchanges() {
        log.debug("REST request to get stock exchanges");
        List<StockExchangeWithSecurities> stockExchanges = repository.findAll();
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        return stockExchanges.stream()
                             .map(stockExchange ->
                                      this.buildStockExchangeJSON(stockExchange,
                                                                  () -> uriBuilder.path(stockExchange.getSymbol())))
                             .collect(JSONArray::new,
                                      JSONArray::put,
                                      JSONArray::put);
    }

    @Path("/stock-exchanges/{symbol}")
    @GET
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getStockExchange(@PathParam("symbol") String symbol) {
        log.debug("REST request to get stock exchange: {}", symbol);
        StockExchangeWithSecurities stockExchange =
            stockExchangeService.getStockExchange(symbol).orElseThrow(() -> new StockExchangeNotFoundException(symbol));
        return this.buildStockExchangeJSON(stockExchange, () -> uriInfo.getAbsolutePathBuilder());
    }

    private JSONObject buildStockExchangeJSON(StockExchangeWithSecurities stockExchangeWithSecurities,
                                              Supplier<UriBuilder> uriBuilderSupplier) {
        JSONObject stockExchangeJSON = new JSONObject(stockExchangeWithSecurities.getStockExchange());
        UriBuilder securityURIBuilder = uriBuilderSupplier.get().path("securities").path("{arg1}");
        List<String> links = stockExchangeWithSecurities.getSecurities()
                                                        .stream()
                                                        .map(security -> securityURIBuilder.build(security.getSymbol()))
                                                        .map(URI::getPath)
                                                        .collect(Collectors.toList());
        try {
            stockExchangeJSON.put("links", links);
        } catch (JSONException e) {
            String linkErrorMessage =
                String.format("Failed to parse links for stock exchanges %s", stockExchangeWithSecurities.getSymbol());
            throw new InternalServerErrorException(linkErrorMessage, e);
        }
        return stockExchangeJSON;
    }
}
