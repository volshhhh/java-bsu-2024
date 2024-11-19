package by.bsu.dependency.exceptions;

public class NoSuchBeanDefinitionException extends RuntimeException {
    public NoSuchBeanDefinitionException(String beanName) {
        super("No Such Bean " + beanName);
    }
}