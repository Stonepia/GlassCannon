package GlassCannon;

import GlassCannon.CompoundTasks.CompoundTask;
import GlassCannon.CompoundTasks.c_Act;
import rts.GameState;
import rts.units.UnitTypeTable;
import util.Helper;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class Planner {
    public static Planner INSTANCE;
    public Deque<PrimitiveTask> plan;

    protected Task goalTask;

    protected  UnitTypeTable utt = null;

    public int player;
    public long time;

    public Planner(UnitTypeTable utt) {
       Planner.INSTANCE = this;
       this.utt = utt;
       this.plan = new LinkedList<PrimitiveTask>();
    }

    public void AddPlanStep(PrimitiveTask task) {
        if (Helper.DEBUG_PLANNER) {
            System.out.println("Task " + task.taskName + "added to the plan");
        }
        Planner.INSTANCE.plan.addLast(task);
    }

    public Deque<PrimitiveTask> CreatePlan(int player, GameState gameState) {
        // TODO : What Plan should we choose? USING HTN Planner?
        // NOTE : This Set the Initial Plan
        if(this.goalTask == null){
            this.goalTask = new c_Act();
        }

        GameState gs2= gameState.clone();
        AbstractGameState ags = new AbstractGameState(gs2);
        this.player = player;

        if (Helper.DESIRED_NUM_WORKERS ==0){
            Helper.ComputeDesiredUnitNumbers(gameState);
        }
        this.plan.clear();

        LinkedList<Task> linkedTask = new LinkedList<Task>();

        // NOTE : Search for a plan
        this.goalTask.Decompose(ags,linkedTask);

        if (this.plan.size()==0){
            if (Helper.DEBUG_ACTION_EXECUTION) {
                System.out.println("No plan could be found");
            }
        }
        else {
            String pl = " new plan :";
            for (Iterator<PrimitiveTask> i = this.plan.iterator(); i.hasNext();){
                pl += i.next().taskName;
                pl += ", ";
            }
            if (Helper.DEBUG_ACTION_EXECUTION){
                System.out.println("Created Plan : "+pl);
            }
        }
        return this.plan;
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
    // TODO : This has been deleted , is it OK?
    //public void AddPlanStep(PrimitiveTask task){
     //   if (Helper.DEBUG_PLANNER){
      //      System.out.println("Task "+ task.taskName + "added to the plan");
       // }
       // Planner.INSTANCE.plan.addLast(task);
   // }


    public abstract MethodsToTake SelectNextMethod(CompoundTask compoundTask, AbstractGameState toChangeGameState, int nextIndex, int[] methodsTried) ;
}
