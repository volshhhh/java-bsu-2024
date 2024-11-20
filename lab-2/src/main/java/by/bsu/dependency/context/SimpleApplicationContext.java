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
public class SimpleApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        throw new IllegalStateException("not implemented");
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
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
