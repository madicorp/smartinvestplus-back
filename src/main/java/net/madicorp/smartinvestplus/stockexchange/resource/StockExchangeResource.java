package net.madicorp.smartinvestplus.stockexchange.resource;

import net.madicorp.smartinvestplus.domain.JSONHyperlinkBuilder;
import net.madicorp.smartinvestplus.stockexchange.domain.Security;
import net.madicorp.smartinvestplus.stockexchange.domain.StockExchangeWithSecurities;
import net.madicorp.smartinvestplus.stockexchange.domain.Symbol;
import net.madicorp.smartinvestplus.stockexchange.repository.StockExchangeCRUDRepository;
import net.madicorp.smartinvestplus.stockexchange.service.StockExchangeService;
import net.madicorp.smartinvestplus.web.rest.ResourceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Path("/api")
public class StockExchangeResource {
    private final Logger log = LoggerFactory.getLogger(StockExchangeResource.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ResourceUtil resourceUtil;

    @Inject
    private StockExchangeCRUDRepository repository;

    @Inject
    private StockExchangeService stockExchangeService;

    /**
     * GET  /api/stock-exchanges/ : Get all stock exchanges places.
     *
     * @return JSONArray containing each stock exchange and the links to its securities
     */
    @Path("/stock-exchanges/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getStockExchanges() {
        log.debug("REST request to get stock exchanges");
        List<StockExchangeWithSecurities> stockExchanges = repository.findAll();
        UriBuilder uriBuilder = resourceUtil.getUriBuilder(uriInfo);
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
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getStockExchange(@PathParam("symbol") @Symbol String symbol) {
        final String upperSymbol = symbol.toUpperCase();
        log.debug("REST request to get stock exchange: {}", upperSymbol);
        StockExchangeWithSecurities stockExchange =
            stockExchangeService.getStockExchange(upperSymbol)
                                .orElseThrow(() -> new StockExchangeNotFoundException(upperSymbol));
        return this.buildStockExchangeJSON(stockExchange, () -> resourceUtil.getUriBuilder(uriInfo));
    }

    private JSONObject buildStockExchangeJSON(StockExchangeWithSecurities stockExchange,
                                              Supplier<UriBuilder> uriBuilderSupplier) {
        JSONObject stockExchangeJSON = new JSONObject(stockExchange.getStockExchange());
        UriBuilder securityURIBuilder = uriBuilderSupplier.get().path("securities").path("{arg1}");
        List<JSONObject> securitiesHyperlinks =
            stockExchange.getSecurities()
                         .stream()
                         .map(security -> getSecurities(securityURIBuilder, security))
                         .collect(Collectors.toList());
        try {
            stockExchangeJSON.put("links", securitiesHyperlinks);
        } catch (JSONException e) {
            String linkErrorMessage = String.format("Failed to format securities for stock exchanges %s",
                                                    stockExchange.getSymbol());
            throw new InternalServerErrorException(linkErrorMessage, e);
        }
        return stockExchangeJSON;
    }

    private JSONObject getSecurities(UriBuilder securityURIBuilder, Security security) {
        try {
            String href = securityURIBuilder.build(security.getSymbol()).getPath();
            return JSONHyperlinkBuilder.init("security", href).build();
        } catch (JSONException e) {
            String linkErrorMessage = String.format("Failed to format security %s", security.getSymbol());
            throw new InternalServerErrorException(linkErrorMessage, e);
        }
    }
}
