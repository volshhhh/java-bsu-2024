package by.volkovets.quizer.generators.math;

import by.volkovets.quizer.tasks.math.MathTask;

public abstract class AbsrtactMathTaskGenerator<T extends MathTask> implements MathTaskGenerator<T> {

    protected int min;
    protected int max;

    public AbsrtactMathTaskGenerator(int Min, int Max) {
        this.min = Min;
        this.max = Max;
    }

    
    public int getMinNumber() {
        return this.min;
    }

    
    public int getMaxNumber() {
        return this.max;
    }
}
