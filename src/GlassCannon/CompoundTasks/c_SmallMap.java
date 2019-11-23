package GlassCannon.CompoundTasks;

import GlassCannon.AbstractGameState;
import GlassCannon.Methods.m_EndGame;
import GlassCannon.Task;

import java.io.PrintWriter;
import java.util.LinkedList;

public class c_SmallMap extends CompoundTask {

    // how often each methods was used to decompose the task
    protected static int[] methodsUsed;

    // how often the method succeed
    protected static float[] methodsSucceeded;

    protected  static int selected;

    public c_SmallMap(){
        super();

        this.taskName = "c_SmallMap";

        this.methods.add(new m_EndGame());
        //this.methods.add(new m_PreventAttackAll());
        //this.methods.add(new m_MiddleGame());
        //this.methods.add(new m_Opening());

        methodsUsed = new int[this.methods.size()];
        methodsSucceeded = new float[this.methods.size()];
    }


    @Override
    public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks){
        AbstractGameState newGameState = super.Decompose(prevGameState,prevTasks);
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
