package GlassCannon;

//import org.jetbrains.annotations.Contract;
import rts.GameState;
import util.Helper;

public class AbstractGameState {
    public GameState gameState;
    public boolean oppBuildingReachable;
    public boolean oppUnitReachable;

    public int currentNumResources = 0;
    public int currentNumWorkers = 0;
    public int currentNumBases = 0;
    public int currentNumBarracks = 0;
    public int currentNumMelee = 0;

    public AbstractGameState(GameState gs) {
        gameState = gs;

        oppBuildingReachable = Helper.OPP_BUILDING_REACHABLE;
        oppUnitReachable = Helper.OPP_UNIT_REACHABLE;

        currentNumResources = Helper.CURRENT_NUM_RESOURCES;
        currentNumWorkers = Helper.CURRENT_NUM_WORKERS;
        currentNumBases = Helper.CURRENT_NUM_BASES;
        currentNumBarracks = Helper.CURRENT_NUM_BARRACKS;
        currentNumMelee = Helper.CURRENT_NUM_MELEE;
    }

    public AbstractGameState(GameState gs, int numResources, int numWorkers, int numBases,
                             int numBarracks, int numMelee, boolean oppBuildingReach, boolean oppUnitReach) {
        gameState = gs;

        oppBuildingReachable = oppBuildingReach;
        oppUnitReachable = oppUnitReach;

        currentNumResources = numResources;
        currentNumWorkers = numWorkers;
        currentNumBases = numBases;
        currentNumBarracks = numBarracks;
        currentNumMelee = numMelee;
    }

    public AbstractGameState clone() {
        return new AbstractGameState(gameState, currentNumResources, currentNumWorkers, currentNumBases, currentNumBarracks, currentNumMelee,
                oppBuildingReachable, oppUnitReachable);
    }
}
