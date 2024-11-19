package by.bsu.dependency.context;

import org.junit.jupiter.api.Test;
import by.bsu.dependency.example.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;

import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

class SimpleApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(
                FirstBean.class,
                OtherBean.class,
                PrototypeBean.class,
                NotBean.class
        );
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("prototypeBean")).isTrue();
        assertThat(applicationContext.containsBean("notBean")).isTrue();
        assertThat(applicationContext.containsBean("NotBean")).isFalse();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
        assertThat(applicationContext.isSingleton("prototypeBean")).isFalse();
        assertThat(applicationContext.isSingleton("notBean")).isTrue();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
        assertThat(applicationContext.isPrototype("prototypeBean")).isTrue();
        assertThat(applicationContext.isPrototype("notBean")).isFalse();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testInjections() {
        applicationContext.start();

        PrototypeBean prototypeBean = (PrototypeBean) applicationContext.getBean("prototypeBean");
        assertNotNull(prototypeBean);
        prototypeBean.doSomething();
        prototypeBean.doSomethingWithFirstBean();
        prototypeBean.doSomethingWithNotBean();
    }

    @Test
    void testSingletonEquality() {
        applicationContext.start();

        FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean");
        FirstBean firstBean2 = (FirstBean) applicationContext.getBean("firstBean");

        assertThat(firstBean).isSameAs(firstBean2);
    }

    @Test
    void testPrototypeEquality() {
        applicationContext.start();

        PrototypeBean prototypeBean1 = (PrototypeBean) applicationContext.getBean("prototypeBean");
        PrototypeBean prototypeBean2 = (PrototypeBean) applicationContext.getBean("prototypeBean");

        assertNotSame(prototypeBean1, prototypeBean2);
    }

    @Test
    void testOtherBeanInteractionWithFirstBean() {
        applicationContext.start();

        OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");
        assertNotNull(otherBean);
        otherBean.doSomethingWithFirst();
    }

    @Test
    void testPrototypeBeanInitialization() {
        applicationContext.start();

        PrototypeBean prototypeBean = (PrototypeBean) applicationContext.getBean("prototypeBean");
        assertNotNull(prototypeBean);
        prototypeBean.doSomething();
    }
}