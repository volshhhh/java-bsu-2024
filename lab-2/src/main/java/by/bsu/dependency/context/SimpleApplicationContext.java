package by.bsu.dependency.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.CyclicDependencyException;

// есть поддержка аннотаций @PostConstruct

public class SimpleApplicationContext extends AbstractApplicationContext {
    public SimpleApplicationContext(Class<?>... beanClasses) throws CyclicDependencyException {
        for (var cl : beanClasses) {
            String name = getName(cl);
            this.beanDefinitions.put(name, cl);
        }

        if (checkCyclic()) {
            throw new CyclicDependencyException();
        }
    }

    @Override
    public void start() {
        this.IsStarted = ContextStatus.STARTED;

        beanDefinitions.forEach((name, clazz) -> {
            if (getScope(clazz) == BeanScope.SINGLETON) {
                beans.put(name, instantiateBean(clazz));
            }
        });

        beans.forEach((name, instance) -> {
            Class<?> clazz = beanDefinitions.get(name);
            Field[] fields = clazz.getDeclaredFields();

            // for @Inject annotation
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    try {
                        Object dependency = getBean(field.getType());
                        field.set(instance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to inject dependency", e);
                    }
                }
            }

            // for @PostConstruct annotation
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to invoke @PostConstruct method", e);
                    }
                }
            }
        });
    }

    // public void getter() {
    // beanDefinitions.forEach((beanName, beanClass) -> {
    // System.out.println("Bean Name: " + beanName + ", Bean Class: " +
    // beanClass.getName());
    // });
    // }
}