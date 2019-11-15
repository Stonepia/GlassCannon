package GlassCannon;

import GlassCannon.CompoundTasks.CompoundTask;
import GlassCannon.CompoundTasks.c_Act;
import rts.GameState;
import util.Helper;

import java.util.Deque;

public abstract class Planner {
    public static Planner INSTANCE;
    public Deque<PrimitiveTask> plan;

    protected Task goalTask;

    public int player;
    public long time;

    public void AddPlanStep(PrimitiveTask task) {
        if (Helper.DEBUG_PLANNER) {
            System.out.println("Task " + task.taskName + "added to the plan");
        }
        Planner.INSTANCE.plan.addLast(task);
    }

    public Deque<PrimitiveTask> CreatePlan(int player, GameState gameState) {
        // TODO : What Plan should we choose? USING HTN Planner?
        if(this.goalTask == null){
            this.goalTask = new c_Act();
        }

        GameState gs2= gameState.clone();
        AbstractGameState ags = new AbstractGameState(gs2);
        this.player = player;

        if (Helper.DESIRED_NUM_WORKERS ==0){
            Helper.ComputeDesiredUnitNumbers(gameState);
        }


    }

    public int GetCurrentplanLength() {
        return Planner.INSTANCE.plan.size();
    }

    public void RemoveLastPlanStep() {
       PrimitiveTask removed = Planner.INSTANCE.plan.removeLast();
       if (Helper.DEBUG_PLANNER){
           System.out.println("Task"+removed.taskName+ "removed from the plan.");
       }
    }

    public abstract MethodstoTake SelectNextMethod(CompoundTask compoundTask, AbstractGameState toChangeGameState, int methodIndex, int[] methodsTried) ;
}
