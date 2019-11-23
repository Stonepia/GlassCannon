package GlassCannon;

import GlassCannon.CompoundTasks.CompoundTask;
import rts.units.UnitTypeTable;
import util.Helper;

public class OrderedPlanner extends Planner {
    public OrderedPlanner(UnitTypeTable utt) {
        super(utt);
        Planner.INSTANCE = this;
    }

    @Override
    public MethodsToTake SelectNextMethod(CompoundTask cTask, AbstractGameState toChangeGameState,
                                          int nextIndex, int[] methodsTried) {
        if (nextIndex >= cTask.methods.size()){
            if (Helper.DEBUG_METHOD_DECOMPOSITION) {
                System.out.println("Task " + cTask.taskName + " could not be decomposed");
            }
            return null;
        }
        for (int i = nextIndex + 1; i < cTask.methods.size(); ++i) {
            if (cTask.methods.get(i).CheckPreconditions(toChangeGameState)) {
                if (Helper.DEBUG_METHOD_DECOMPOSITION) {
                    System.out.println(
                            "Method " + cTask.methods.get(i).methodName + " was used to decompose task " + cTask.taskName);
                }
                return cTask.methods.get(i);
            }
        }

        if (Helper.DEBUG_METHOD_DECOMPOSITION) {
            System.out.println("Task " + cTask.taskName + " could not be decomposed");
        }
        return null;
    }
}
