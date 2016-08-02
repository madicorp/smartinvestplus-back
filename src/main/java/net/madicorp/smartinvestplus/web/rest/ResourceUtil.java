package net.madicorp.smartinvestplus.web.rest;

import com.google.common.base.Joiner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * User: sennen
 * Date: 23/07/2016
 * Time: 20:13
 */
@Service
public class ResourceUtil {

    public Response.ResponseBuilder badRequestBuilder() {
        return Response.status(400).type(MediaType.TEXT_PLAIN_TYPE);
    }

    public Response serverError() {
        return Response.serverError().build();
    }

    public Response noContent() {
        return Response.noContent().build();
    }

    public Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    public Response.ResponseBuilder addHeaders(Response.ResponseBuilder builder, HttpHeaders headers) {
        headers.entrySet()
               .forEach(header -> {
                   String headerValue = Joiner.on(";").join(header.getValue());
                   builder.header(header.getKey(), headerValue);
               });
        return builder;
    }

    public String getBaseUrl(UriInfo uriInfo) {
        return uriInfo.getBaseUri().toString();
    }

    public UriBuilder getUriBuilder(UriInfo uriInfo) {
        return uriInfo.getAbsolutePathBuilder();
    }

    public Pageable page(Integer page, Integer size) {
        page = page != null ? page : 0;
        size = size != null ? size : 20;
        return new PageRequest(page, size);
    }

}
