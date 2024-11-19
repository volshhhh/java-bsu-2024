package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "otherBean")
public class OtherBean {

    @Inject
    private FirstBean firstBean;

    public void doSomething() {
        System.out.println("Hi, I'm otherBean");
    }


    public void doSomethingWithFirst() {
        System.out.println("OtherBean trying to shake first bean...");
        firstBean.doSomething();
    }
}
