package GlassCannon.CompoundTasks;

import GlassCannon.AbstractGameState;
import GlassCannon.Methods.m_handleBigMap;
import GlassCannon.Planner;
import GlassCannon.Task;
import GlassCannon.Methods.m_handleMidsizeMap;
import GlassCannon.Methods.m_handleSmallMap;
import util.Helper;

import java.io.PrintWriter;
import java.util.LinkedList;

public class c_Act extends CompoundTask {
    // how often each method was used to decompose this task
    protected static int[] methodsUsed;

    // how often each method was used to decompose this task
    protected static float[] methodsSucceeded;

    protected static int selected;
    protected static float succeeded;
    protected static float maxReward = Helper.EPSILON;

    public c_Act(){
        super();

        this.taskName = "c_Act";

        this.methods.add(new m_handleBigMap());
        this.methods.add(new m_handleMidsizeMap());
        this.methods.add(new m_handleSmallMap());

        c_Act.methodsUsed = new int[this.methods.size()];
        c_Act.methodsSucceeded = new float[this.methods.size()];
    }

    @Override
    public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks){
        AbstractGameState newGameState = super.Decompose(prevGameState, prevTasks);

        if (newGameState == null || Planner.INSTANCE.GetCurrentplanLength()==0){
            newGameState = this.methods.get(this.methods.size()- 1).DecomposeTask(prevGameState,prevTasks);
        }

        return newGameState;
    }

    @Override
    public void SetUCBValues(String[] values) {

    }

    @Override
    public void IncreaseSuccessStatistics(float currentReward) {

    }

    @Override
    public void PrintAllUCBValuesOfTask(PrintWriter pw) {

    }
}
