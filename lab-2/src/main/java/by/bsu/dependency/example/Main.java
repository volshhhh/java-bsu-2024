package by.bsu.dependency.example;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.SimpleApplicationContext;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new SimpleApplicationContext(FirstBean.class, NotBean.class, OtherBean.class,
                PrototypeBean.class);

        System.out.println(context.isRunning()); // false
        context.start();
        System.out.println(context.isRunning()); // false

        context.getBean(FirstBean.class).doSomething();

        System.out.println(context.containsBean("notBean")); // false
        System.out.println(context.containsBean("firstBean")); // true
        System.out.println(context.containsBean("otherBean")); // true

        var tt = context.getBean(PrototypeBean.class);
        tt.init();
        tt.doSomething();
        tt.doSomethingWithFirstBean();
        tt.doSomethingWithNotBean();

        var nn = context.getBean(FirstBean.class);
        nn.doSomething();

        var yy = context.getBean(OtherBean.class);
        yy.doSomething();
        yy.doSomethingWithFirst();

        try {
            context.getBean("NotBean");
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("Exception: Bean NotBean was not found");
        }

        System.out.println(context.isPrototype("prototypeBean"));
        System.out.println(context.isSingleton("prototypeBean"));

        try {
            System.out.println(context.isPrototype("randomBean"));
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("Exception: Bean randomBean was not found");
        }
    }
}
