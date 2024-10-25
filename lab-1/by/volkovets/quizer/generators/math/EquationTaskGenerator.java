package by.volkovets.quizer.generators.math;

import by.volkovets.quizer.exceptions.MinIsBiggerThanMaxException;
import by.volkovets.quizer.tasks.math.EquationTask;
import by.volkovets.quizer.tasks.math.MathTask;
import java.util.Random;

public class EquationTaskGenerator extends AbsrtactMathTaskGenerator<EquationTask> {
    public EquationTaskGenerator(int Min, int Max) {
        super(Min, Max);
    }

    @Override
    public EquationTask generate() throws MinIsBiggerThanMaxException {
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
                if (second == 0) {
                    second = 1;
                }
                break;
            case 2:
                operation = MathTask.Operation.Multiplication;
                if (first == 0) {
                    second = 0;
                }
                break;
            default:
                operation = MathTask.Operation.Sum;
                break;
        }

        int X_Pos = random.nextInt(2);

        EquationTask task = new EquationTask(X_Pos, operation, first, second);

        return task;
    }
}
