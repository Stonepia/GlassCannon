package GlassCannon;

import ai.evaluation.EvaluationFunction;
import rts.GameState;
import rts.PlayerAction;
import util.Helper;

import java.util.LinkedList;

public class PrimitiveTask extends Task {


    @Override
    public AbstractGameState Decompose(AbstractGameState gameState, LinkedList<Task> prevTasks) {
        this.linkedTasks = (LinkedList<Task>) prevTasks.clone();
        this.linkedTasks.add(this);

        if (!this.CheckPreconditions(prevGameState)) {
            if (Helper.DEBUG_METHOD_DECOMPOSITION) {
                System.out.println("Primitive Task Name is :" + this.taskName + "could not be applied");
            }
            return null;
        }
        return Simulate(prevGameState);
    }

    private AbstractGameState Simulate(AbstractGameState prevGameState) {
        AbstractGameState toChangeGameState = prevGameState.clone();
        Planner.INSTANCE.AddPlanStep(this);
        return toChangeGameState;
    }

    @Override
    public void IncreaseSuccessStatistics(float currentReward) {
        float totalBonus = 0;

        for (int i = this.linkedTasks.size() - 2; i >= 0; --i) {
            float bonus = i < this.linkedTasks.size() - 2 ? this.linkedTasks.get(i + 1).GetBonus() : 0;

            totalBonus += bonus;
            this.linkedTasks.get(i).AddBonus(totalBonus);
            this.linkedTasks.get(i).IncreaseSuccessStatistics(currentReward);
            if (Helper.REWARD_FROM_EXECUTION) {
                this.linkedTasks.get(i).FlushMethodRewards();
            }
        }

        if (Helper.DEBUG_TASK_INCREASE_STATISTICS) {
            System.out.println("Primitive task : " + this.taskName + "current reward = " + currentReward);
        }

    }

    @Override
    public void FlushMethodRewards() {
        // TODO : Flush the Reward
    }

    @Override
    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        // TODO : Where is this implemented??
        System.out.println("HERE NOT IMPLEMENTED!!!");
        return true;
    }

    @Override
    public boolean IsReached(int player, int i, GameState gs) {
        // TODO : Implement this
        return false;
    }

    @Override
    public EvaluationFunction GetTaskEF() {
        // TODO : Implement Evaluation Function
        return null;
    }


}
