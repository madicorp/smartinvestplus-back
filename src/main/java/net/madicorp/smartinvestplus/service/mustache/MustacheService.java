package net.madicorp.smartinvestplus.service.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class MustacheService {
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    @Cacheable
    public <T> StringMustacheTemplate<T> compile(String templateName) throws IOException {
        String templateLocation = getTemplateLocation(templateName);
        Mustache mustacheTemplate = mustacheFactory.compile(templateLocation);
        return new StringMustacheTemplate<>(mustacheTemplate);
    }

    @Cacheable
    public <T> ListStringMustacheTemplate<T> compileList(String templateName) throws IOException {
        String templateLocation = getTemplateLocation(templateName);
        Mustache mustacheTemplate = mustacheFactory.compile(templateLocation);
        return new ListStringMustacheTemplate<>(mustacheTemplate);
    }

    private String getTemplateLocation(String templateName) throws IOException {
        ClassPathResource templateLocation = new ClassPathResource("templates/mustache/" + templateName + ".mustache");
        return templateLocation.getFile().getAbsolutePath();
    }
}
