package by.bsu.dependency.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.CyclicDependencyException;

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
public class AutoScanApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        throw new IllegalStateException("not implemented");
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


        beans.forEach((name, instance) -> {
            Class<?> clazz = beanDefinitions.get(name);
            Method[] methods = clazz.getDeclaredMethods();

            // for @PostConstruct annotation
            for (Method method : methods) {
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
}
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isRunning() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean containsBean(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object getBean(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isPrototype(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isSingleton(String name) {
        throw new IllegalStateException("not implemented");
    }
}
