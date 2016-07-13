package net.madicorp.smartinvestplus.test;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A class rule that starts jersey test server, set up annotated beans that must be injected and finally stops server
 * at teardown
 * User: sennen
 * Date: 13/07/2016
 * Time: 08:21
 */
public class HttpTestRule extends ExternalResource {
    private HttpTestHelperBuilder.HttpTestHelper httpTestHelper;
    private Description description;

    @Override
    public Statement apply(Statement base, Description description) {
        this.description = description;
        return super.apply(base, description);
    }

    @Override
    protected void before() throws Throwable {
        checkThatIAmAClassRule();
        httpTestHelper = getHttpTestHelper();
        httpTestHelper.setUp();
        initBeans(description);
    }

    private HttpTestHelperBuilder.HttpTestHelper getHttpTestHelper() {
        HttpTestConfig testConfigAnnotation = description.getAnnotation(HttpTestConfig.class);
        if(testConfigAnnotation == null) {
            throw new HttpTestException("No HttpTestConfig has been provided");
        }
        return HttpTestHelperBuilder.builder(testConfigAnnotation::value).build();
    }

    private void checkThatIAmAClassRule() {
        for (Field field : description.getTestClass().getFields()) {
            if(!HttpTestRule.class.equals(field.getType())) {
                continue;
            }
            if (field.getAnnotation(ClassRule.class) == null) {
                throw new HttpTestException("HttpTestRule must be used as class rule");
            }
        }
    }

    private void initBeans(Description description) throws Exception {
        Class testClass = description.getTestClass();
        for (Field field : testClass.getDeclaredFields()) {
            HttpTestInjectBean injectBeanAnnotation = field.getAnnotation(HttpTestInjectBean.class);
            if (injectBeanAnnotation == null) {
                continue;
            }

            Object bean = httpTestHelper.context().getBean(field.getType());
            if(!Modifier.isStatic(field.getModifiers())) {
                throw new HttpTestException("HttpTestInjectBean must be used on static fields");
            }
            field.setAccessible(true);
            field.set(testClass, bean);
        }
    }

    @Override
    protected void after() {
        try {
            httpTestHelper.tearDown();
        } catch (Exception e) {
            throw new TearDownException(e);
        }
    }

    public WebTarget target(String path) {
        return httpTestHelper.target(path);
    }
}
