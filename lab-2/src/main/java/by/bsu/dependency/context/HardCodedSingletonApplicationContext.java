package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.exceptions.CyclicDependencyException;

public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) throws CyclicDependencyException {
        for (Class<?> beanClass : beanClasses) {
            String beanName = beanClass.getAnnotation(Bean.class).name();
            this.beanDefinitions.put(beanName, beanClass);
        }

        if (checkCyclic()) {
            throw new CyclicDependencyException();
        }
    }

    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        IsStarted = ContextStatus.STARTED;
    }
}
