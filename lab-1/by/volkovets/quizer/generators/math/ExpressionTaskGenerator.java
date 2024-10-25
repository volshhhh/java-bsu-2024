package by.volkovets.quizer.generators.math;

import by.volkovets.quizer.exceptions.MinIsBiggerThanMaxException;
import by.volkovets.quizer.tasks.math.ExpressionTask;
import by.volkovets.quizer.tasks.math.MathTask;
import java.util.Random;

public class ExpressionTaskGenerator extends AbsrtactMathTaskGenerator<ExpressionTask> {
    public ExpressionTaskGenerator(int Min, int Max) {
        super(Min, Max);
    }

    @Override
    public ExpressionTask generate() throws MinIsBiggerThanMaxException {
        if (this.min > this.max) {
            throw new MinIsBiggerThanMaxException();
        }
        Random random = new Random();
        int first = random.nextInt(this.max - this.min + 1) + this.min;
        int second = random.nextInt(this.max - this.min + 1) + this.min;

        MathTask.Operation operation;
        int oper_index = random.nextInt(4);

        switch (oper_index) {
            case 0:
                operation = MathTask.Operation.Difference;
                break;
            case 1:
                operation = MathTask.Operation.Division;
                break;
            case 2:
                operation = MathTask.Operation.Multiplication;
                break;
            default:
                operation = MathTask.Operation.Sum;
                break;
        }

        ExpressionTask task = new ExpressionTask(operation, first, second);

        return task;
    }
}
