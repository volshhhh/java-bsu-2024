package by.bsu.dependency.context;

import java.lang.reflect.Field;
import java.util.Set;

import org.reflections.Reflections;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.CyclicDependencyException;

// в нем нет поддержки аннотаций @PostConstruct

public class AutoScanApplicationContext extends AbstractApplicationContext {
    public AutoScanApplicationContext(String packageName) throws CyclicDependencyException {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> beanClasses = reflections.getTypesAnnotatedWith(Bean.class);

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
            if (getScope(name) == BeanScope.SINGLETON) {
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
        });
    }
}