package net.madicorp.smartinvestplus.service.mustache;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class MustacheService {
    private final String baseDir;

    @Autowired
    private MustacheFactory mustacheFactory;

    public MustacheService() {
        baseDir = MustacheService.class.getClassLoader().getResource(".").getFile();
    }

    @Cacheable
    public <T> StringMustacheTemplate<T> compile(String templateName) {
        Path templateLocation = Paths.get(baseDir, "templates/mustache", templateName + ".mustache");
        Mustache mustacheTemplate = mustacheFactory.compile(templateLocation.toString());
        return new StringMustacheTemplate<>(mustacheTemplate);
    }

    @Cacheable
    public <T> ListStringMustacheTemplate<T> compileList(String templateName) {
        Path templateLocation = Paths.get(baseDir, "src/main/resources/templates/mustache", templateName + ".mustache");
        Mustache mustacheTemplate = mustacheFactory.compile(templateLocation.toString());
        return new ListStringMustacheTemplate<>(mustacheTemplate);
    }
}
