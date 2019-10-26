package GlassCannon;

import ai.evaluation.EvaluationFunction;
import rts.GameState;

import java.util.LinkedList;

public abstract class Task {
    public String taskName;
    public AbstractGameState prevGameState;
    protected LinkedList<Task> linkedTasks;

    public abstract AbstractGameState Decompose(AbstractGameState gameState, LinkedList<Task> prevTasks);

    public abstract void IncreaseSuccessStatistics(float currentReward);

    public float GetBonus() {
        return 0;
    }

    public void AddBonus(float bonus) {
    }

    public abstract void FlushMethodRewards();

    public abstract boolean CheckPreconditions(AbstractGameState currentGameState);

    public abstract boolean IsReached(int player, int i, GameState gs);

    public abstract EvaluationFunction GetTaskEF();
}
