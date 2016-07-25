package net.madicorp.smartinvestplus.test;

import net.madicorp.smartinvestplus.config.JerseyConfig;
import net.madicorp.smartinvestplus.config.JerseyMapperProvider;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.function.Supplier;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 21:49
 */
class HttpTestHelperBuilder {
    private final Supplier<Class[]> configurationClassesSupplier;

    private HttpTestHelperBuilder(Supplier<Class[]> configurationClassesSupplier) {
        this.configurationClassesSupplier = configurationClassesSupplier;
    }

    static HttpTestHelperBuilder builder(Supplier<Class[]> configurationClassesSupplier) {
        return new HttpTestHelperBuilder(configurationClassesSupplier);
    }

    HttpTestHelper build() {
        return new HttpTestHelper();
    }

    class HttpTestHelper extends JerseyTest {
        private ApplicationContext context;

        @Override
        protected Application configure() {
            context = new AnnotationConfigApplicationContext(configurationClassesSupplier.get());
            return new JerseyConfig()
                .property(ServerProperties.PROVIDER_CLASSNAMES, JerseyMapperProvider.class.getCanonicalName())
                .property("contextConfig", context);
        }

        public ApplicationContext context() throws Exception {
            return context;
        }

        public ResponseAssertion assertThat(Response actual) {
            return ResponseAssertion.assertThat(actual);
        }
    }
}
