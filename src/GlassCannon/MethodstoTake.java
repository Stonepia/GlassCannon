package GlassCannon;

import GlassCannon.CompoundTasks.CompoundTask;
import rts.PlayerAction;
import util.Helper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class MethodstoTake {
    public PlayerAction myAct;
    public String methodName;
    protected ArrayList<Task> tasksToDecompose;

    protected int prevPlanSize = 0;

    public abstract boolean CheckPreconditions(AbstractGameState currentGameState);

    public MethodstoTake() {tasksToDecompose = new ArrayList<Task>();}

    public void PrintAllUCBValuesOfMethod(PrintWriter pw){
        if (this.tasksToDecompose.size()==0){
            return;
        }
       //TODO : Use Iterator
        for (int i =0; i<this.tasksToDecompose.size();++i){
            if (this.tasksToDecompose.get(i).getClass().getSimpleName().contains("p_")){
                continue;
            }
            ((CompoundTask) this.tasksToDecompose.get(i)).PrintAllUCBValuesOfTask(pw);
        }
    }

    public AbstractGameState DecomposeTask(AbstractGameState gameState, LinkedList<Task> prevTasks){
        AbstractGameState prevGameState = gameState;
        AbstractGameState toChangeGameState = prevGameState.clone();

        prevPlanSize = Planner.INSTANCE.GetCurrentplanLength();

        if (this.tasksToDecompose.size()==0){
            if (Helper.DEBUG_METHOD_DECOMPOSITION){
                System.out.println("No tasks defined for method " + this.methodName);
            }
            return toChangeGameState;
        }

        long currTime;

        for (int i=0;i<this.tasksToDecompose.size();++i){
            currTime = System.currentTimeMillis();
            if (currTime - Planner.INSTANCE.time >= Helper.MAX_TIME_FOR_PLANNER){
                if (Helper.DEBUG_TIMEOUT){
                    System.out.println("Method "+ this.methodName + ": No computation time left");
                }
                return null;

            }

            toChangeGameState = this.tasksToDecompose.get(i).Decompose(toChangeGameState,
                    (LinkedList<Task>)prevTasks.clone());

            // If one of the tasks could not be decomposed by any methods
            if (toChangeGameState == null){
                currTime = System.currentTimeMillis();

                //if Previous tasks were added by this method, remove theme again
                int diff = Planner.INSTANCE.GetCurrentplanLength() - prevPlanSize;
                if (diff != 0 && currTime - Planner.INSTANCE.time < Helper.MAX_TIME_FOR_PLANNER){
                    for (int j=0;j<diff;++j){
                        Planner.INSTANCE.RemoveLastPlanStep();
                    }
                }
                break;
            }
        }
        return toChangeGameState;
    }


}
