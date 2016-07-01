package net.madicorp.smartinvestplus.service.mustache;

import com.github.mustachejava.Mustache;
import lombok.RequiredArgsConstructor;

import java.io.StringWriter;

/**
 * User: sennen
 * Date: 01/07/2016
 * Time: 00:19
 */
@RequiredArgsConstructor
class StringMustacheTemplate<T> {
    private final Mustache mustache;

    public String render(T entity) {
        StringWriter resultWriter = new StringWriter();
        mustache.execute(resultWriter, entity);
        return resultWriter.toString();
    }
}
