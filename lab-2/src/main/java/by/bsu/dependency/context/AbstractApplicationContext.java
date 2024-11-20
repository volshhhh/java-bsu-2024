package by.bsu.dependency.context;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected final Map<String, Class<?>> beanDefinitions = new HashMap<>();
    protected final Map<String, Object> beans = new HashMap<>();
    protected ContextStatus IsStarted = ContextStatus.NOT_STARTED;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected <T> T instantiateBean(Class<T> beanClass) {
        try {
            beanClass.getConstructor().setAccessible(true);
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    protected String getName(Class<?> clazz) {
        String tmp_name = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(Bean.class)) {
            Bean beanAnnotation = clazz.getAnnotation(Bean.class);
            String name;
            name = beanAnnotation.name();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return Character.toLowerCase(tmp_name.charAt(0)) + tmp_name.substring(1);
    }

    protected BeanScope getScope(Class<?> clazz) throws NoSuchBeanDefinitionException {
        if (!beanDefinitions.containsKey(getName(clazz))) {
            throw new NoSuchBeanDefinitionException(getName(clazz));
        }
        BeanScope scope = BeanScope.SINGLETON;
        if (clazz.isAnnotationPresent(Bean.class)) {
            Bean beanAnnotation = clazz.getAnnotation(Bean.class);
            scope = beanAnnotation.scope();
        }

        return scope;
    }

    protected BeanScope getScope(String name) throws NoSuchBeanDefinitionException {
        Class<?> clazz = beanDefinitions.get(name);
        return getScope(clazz);
    }

    @Override
    public boolean isRunning() {
        return IsStarted == ContextStatus.STARTED;
    }

    @Override
    public boolean containsBean(String name) throws ApplicationContextNotStartedException {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return beanDefinitions.containsKey(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return getScope(name) == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return getScope(name) == BeanScope.SINGLETON;
    }

    @Override
    public Object getBean(String name) throws RuntimeException {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }

        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }

        if (getScope(name) == BeanScope.PROTOTYPE) {
            var clazz = beanDefinitions.get(name);
            Object instance = instantiateBean(clazz);

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    try {
                        String tmp = field.getType().getSimpleName();
                        String curr_name = Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);

                        if (field.isAnnotationPresent(Bean.class)) {
                            curr_name = field.getAnnotation(Bean.class).name();
                        }

                        Object dependency = getBean(curr_name);
                        field.set(instance, dependency);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to inject dependency", e);
                    }
                }
            }
            return instance;
        }

        return beans.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) throws RuntimeException {

        String name = getName(clazz);
        return (T) getBean(name);
    }

    /*
     * Весь код ниже будет посвящен
     * нахождению циклических зависимостей
     * между классами из beanDefintions
     * только циклы из прототипов
     */

    protected boolean checkCyclic() {

        GraphCycleDetector detector = new GraphCycleDetector();
        return detector.hasCycle();
    }

    private HashMap<String, HashSet<String>> getGraph() {
        HashMap<String, HashSet<String>> graph = new HashMap<>();

        beanDefinitions.forEach((name, clazz) -> {
            Field[] fields = clazz.getDeclaredFields();
            graph.putIfAbsent(name, new HashSet<>());

            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) { // только для полей, что имеют аннотацию @Inject
                    Class<?> cl = field.getType();
                    String className = getName(cl);

                    if (beanDefinitions.containsKey(className)) {
                        graph.get(name).add(className);
                    }
                }
            }
        });

        return graph;
    }

    private class GraphCycleDetector {

        private HashMap<String, HashSet<String>> graph = getGraph();

        public boolean hasCycle() {
            HashSet<String> visited = new HashSet<>();
            HashSet<String> recursionStack = new HashSet<>();

            for (String node : graph.keySet()) {
                if (getScope(node) == BeanScope.PROTOTYPE && dfs(node, visited, recursionStack)) {
                    return true;
                }
            }
            return false;
        }

        private boolean dfs(String node, HashSet<String> visited, HashSet<String> recursionStack) {
            if (recursionStack.contains(node)) {
                return true;
            }
            if (visited.contains(node)) {
                return false;
            }

            visited.add(node);
            recursionStack.add(node);

            for (String neighbor : graph.getOrDefault(node, new HashSet<>())) {
                if (getScope(neighbor) == BeanScope.PROTOTYPE && dfs(neighbor, visited, recursionStack)) {
                    return true;
                }
            }

            recursionStack.remove(node);
            return false;
        }
    }
}
