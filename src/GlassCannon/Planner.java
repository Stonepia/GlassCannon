package GlassCannon;

import rts.GameState;
import util.Helper;

import java.util.Deque;

public class Planner {
    public static Planner INSTANCE;
    public Deque<PrimitiveTask> plan;
    public int player;
    public long time;

    public void AddPlanStep(PrimitiveTask task) {
        if (Helper.DEBUG_PLANNER) {
            System.out.println("Task " + task.taskName + "added to the plan");
        }
        Planner.INSTANCE.plan.addLast(task);
    }

    public Deque<PrimitiveTask> CreatePlan(int player, GameState gs) {
        // TODO : Implement this for plan switching
        return null;
    }
}
