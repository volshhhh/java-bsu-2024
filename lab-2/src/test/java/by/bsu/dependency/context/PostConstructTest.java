package by.bsu.dependency.context;


import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostConstructTest {

    @Bean(name = "testBean")
    public static class TestBean {

        @Inject
        private DependencyBean dependency;

        private String message;

        @PostConstruct
        public void init() {
            message = "Initialized with: " + dependency.getMessage();
        }

        public String getMessage() {
            return message;
        }
    }

    @Bean
    public static class DependencyBean {
        public String getMessage() {
            return "Hello from DependencyBean!";
        }
    }

    @Test
    public void testPostConstruct() {
        SimpleApplicationContext context = new SimpleApplicationContext(TestBean.class, DependencyBean.class);
        context.start();

        var testBean = context.getBean(TestBean.class);
        assertEquals("Initialized with: Hello from DependencyBean!", testBean.getMessage());
    }
}