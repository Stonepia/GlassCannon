package GlassCannon.Methods;

import GlassCannon.AbstractGameState;
import GlassCannon.CompoundTasks.c_EndGame;
import GlassCannon.MethodsToTake;
import util.Helper;


public class m_EndGame extends MethodsToTake {
    protected static int[] methodsUsed;
    protected static float[] methodsSucceeded;

    protected static int selected;


    // This is used to control behavior
    @Override
    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        boolean holds = false;

        // If small map and have mobile units
        if (currentGameState.gameState.getPhysicalGameState().getWidth() < 16
                && currentGameState.currentNumWorkers >= Helper.DESIRED_NUM_WORKERS)
            return true;

        // If big map and enough military units and at least bases/barracks
        if (currentGameState.currentNumBases >= Helper.DESIRED_NUM_BASES
            && currentGameState.currentNumBarracks >= Helper.DESIRED_NUM_BARRACKS
            && currentGameState.currentNumMelee >= Helper.DESIRED_NUM_MELEE
            && currentGameState.anyOppBuildingReachable){
            return true;
        }

        // Cannot build anymore  - try attacking as last option
        if (currentGameState.currentNumBases ==0 ||
                (currentGameState.currentNumBases < 1 && currentGameState.currentNumWorkers<1)){
            return true;
        }

        if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty()){
            return true;
        }

        return holds;
    }

    public m_EndGame(){
        super();

        this.methodName = "m_Attack";
        this.tasksToDecompose.add(new c_EndGame());
    }



}
