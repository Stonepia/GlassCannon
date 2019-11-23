package GlassCannon.CompoundTasks;

import GlassCannon.AbstractGameState;
import GlassCannon.Task;
import GlassCannon.Methods.m_AttackSmall;
import util.Helper;

import java.io.PrintWriter;
import java.util.LinkedList;

public class c_EndGame extends CompoundTask {

    // how often each method was used to decompose this task
    protected static int[] methodsUsed;

    // how often each method was used to decompose this task
    protected static float[] methodsSucceeded;

    protected static int selected;

    protected static float succeeded;

    protected static float maxReward = Helper.EPSILON;

    public c_EndGame() {
        super();

        this.taskName = "c_EndGame";

        this.methods.add(new m_AttackSmall());

        //this.methods.add(new m_AttackMidsize());

        //this.methods.add(new m_AttackMilitaryBig());
        //this.methods.add(new m_AttackAllBig());

        c_EndGame.methodsUsed = new int[this.methods.size()];
        c_EndGame.methodsSucceeded = new float[this.methods.size()];
    }

    @Override
    public AbstractGameState Decompose(AbstractGameState prevGameState, LinkedList<Task> prevTasks) {
        AbstractGameState newGameState = super.Decompose(prevGameState, prevTasks);
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
