package by.bsu.dependency.context;


import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.CyclicDependencyException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CyclicDependencyTest {

@Bean(name = "fBean", scope = BeanScope.PROTOTYPE)
    public static class FBean {

        @Inject
        private SBean dependency;

        public String getMessage() {
            return dependency.getMessage();
        }
    }

    @Bean(name = "sBean", scope = BeanScope.PROTOTYPE)
    public static class SBean {

        @Inject
        private ThBean testB;

        public String getMessage() {
            return testB.getMessage();
        }
        
    }

    @Bean(name = "thBean", scope = BeanScope.PROTOTYPE)
    public static class ThBean {

        @Inject
        private FBean testB;

        public String getMessage() {
            return testB.getMessage();
        }

        
    }
    @Test
    public void testCyclic() {
        assertThrows(CyclicDependencyException.class, () -> {
            new SimpleApplicationContext(
                FBean.class,
                SBean.class,
                ThBean.class
            );
        });
    }
}
