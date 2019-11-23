package GlassCannon.CompoundTasks;

import GlassCannon.AbstractGameState;
import GlassCannon.Planner;
import GlassCannon.Task;
import GlassCannon.MethodsToTake;
import ai.evaluation.EvaluationFunction;
import rts.GameState;
import util.Helper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class CompoundTask extends Task {

    public ArrayList<MethodsToTake> methods;

    protected int indMethodSelected;
    protected float prevMethodsReward;
    protected float tempReward;
    protected float[] lastMethodsValues;

    private int prevPlanLength = 0;

    public CompoundTask() {this.methods = new ArrayList<MethodsToTake>();}

    public abstract  void SetUCBValues(String[] values);

    public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
        this.prevGameState = prevGameState;

        // save the whole tree path of tasks that lead to a primitive task
        prevTasks.add(this);
        this.linkedTasks = (LinkedList<Task>) prevTasks.clone();

        AbstractGameState toChangeGameState = prevGameState.clone();

        AbstractGameState newGameState = null;

        //select a method depending on preconditions and selection mechanism
        int methodIndex = -1;
        int[] methodsTried = new int[this.methods.size()];
        MethodsToTake toDecompose = null;

        toDecompose = Planner.INSTANCE.SelectNextMethod(this,toChangeGameState,methodIndex,methodsTried);

        boolean decompositionNeeded = toDecompose ==null?false:true;

        while(decompositionNeeded){
            long currTime = System.currentTimeMillis();
            if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER){
                if (Helper.DEBUG_TIMEOUT){
                    System.out.println("Task "+ this.taskName + ":no computation time left");
                }
                return null;
            }

            this.indMethodSelected = this.methods.indexOf(toDecompose);
            this.tempReward = 0;
            this.lastMethodsValues = null;
            this.prevMethodsReward = 0;

            newGameState = toDecompose.DecomposeTask(toChangeGameState,this.linkedTasks);

            methodsTried [this.indMethodSelected] = 1;

            if (newGameState != null){
                decompositionNeeded = false;
            }
            else{
                if (Helper.DEBUG_METHOD_DECOMPOSITION){
                    System.out.println("Decomposing Task "+ this.taskName + "failed, trying another one");
                }

                currTime = System.currentTimeMillis();
                if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER){
                    if (Helper.DEBUG_TIMEOUT){
                        System.out.println("Task "+ this.taskName + "no computation time");
                    }
                    return null;
                }
                methodIndex = this.indMethodSelected;
                toDecompose = Planner.INSTANCE.SelectNextMethod(this, toChangeGameState,methodIndex,methodsTried);
                decompositionNeeded = toDecompose == null ? false: true;
            }
        }
        return newGameState;
    }
// TODO : Is this still needed?
    public abstract void IncreaseSuccessStatistics(float currentReward);

    public void IncreaseSuccessStatistics(float[] methodsValues, int methodsIndex, float reward, float succeeded){
        if (this.lastMethodsValues == null){
            this.lastMethodsValues = methodsValues;
            this.prevMethodsReward = methodsValues[methodsIndex];
        }

        float successPortion = (float) reward;

        succeeded +=reward;
        tempReward += successPortion;
    }





    @Override
    public void FlushMethodRewards() {
        if(this.lastMethodsValues != null){
            this.lastMethodsValues[this.indMethodSelected] +=tempReward;
        }
        if (Helper.DEBUG_UCB_STATISTICS) {
            System.out.println("total reward of " + tempReward + " added to method "
                    + this.methods.get(this.indMethodSelected).methodName + " in " + this.taskName);
        }
    }
    public abstract void PrintAllUCBValuesOfTask(PrintWriter pw);

    @Override
    public boolean IsReached(int player, int i, GameState gs) {
        return false;
    }

    @Override
    public EvaluationFunction GetTaskEF() {
        return null;
    }

    @Override
    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        return false;
    }
}
