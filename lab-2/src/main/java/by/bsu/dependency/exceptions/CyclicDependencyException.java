package by.bsu.dependency.exceptions;

public class CyclicDependencyException extends RuntimeException{
    public CyclicDependencyException() {
        super("You have cyclic dependency with prototypes");
    }
}
