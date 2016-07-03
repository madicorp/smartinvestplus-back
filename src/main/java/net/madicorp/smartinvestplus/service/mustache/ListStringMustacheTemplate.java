package net.madicorp.smartinvestplus.service.mustache;

import com.github.mustachejava.Mustache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * User: sennen
 * Date: 01/07/2016
 * Time: 00:19
 */
@RequiredArgsConstructor
public class ListStringMustacheTemplate<T> {
    private final Mustache mustache;

    public String render(List<T> entities) {
        StringWriter resultWriter = new StringWriter();
        int size = entities.size();
        List<SeparatedEntity<T>> separatedEntities = IntStream.range(0, size)
                                                    .boxed()
                                                    .map((i) -> new SeparatedEntity<>(entities.get(i), i != (size - 1)))
                                                    .collect(Collectors.toList());
        mustache.execute(resultWriter, separatedEntities);
        return resultWriter.toString();
    }

    @Getter
    @RequiredArgsConstructor
    private static class SeparatedEntity<T> {
        private final T value;
        private final boolean separator;
    }
}
