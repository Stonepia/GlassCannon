package util;

import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitType;

import java.util.HashMap;

public class Helper {

    // ********************************
    // GAMESTATE UNITS
    // ********************************


    public static HashMap<Long, Pair<Integer, Integer>> MY_BASES = new HashMap<Long, Pair<Integer, Integer>>();
    // ********************************
    // GAMESTATE VARIABLES
    // ********************************
    private static int DESIRED_NUM_RESOURCES = 0;
    private static int DESIRED_NUM_BARRACKS = 0;

    public static long MAX_TIME_FOR_PLANNER = 80; // TODO : Set for another number?
    // ********************************
    // MAP INFORMATION
    // ********************************
    private static int MAX_MAP_DIST = 0;
    // ********************************
    // UNIT TYPES
    // ********************************
    public static UnitType BASE_TYPE;
    public static UnitType WORKER_TYPE;

    public static UnitType BARRACKS_TYPE;
    public static UnitType LIGHT_TYPE;
    public static UnitType HEAVY_TYPE;
    public static UnitType RANGED_TYPE;
    public static UnitType RESOURCE_TYPE;
    // ********************************
    //  DEBUG VARIABLE
    // ********************************
    public static boolean DEBUG_METHOD_DECOMPOSITION = false;
    public static boolean DEBUG_PLANNER = false;

    public static boolean DEBUG_TASK_INCREASE_STATISTICS = false;
    public static boolean DEBUG_ACTION_EXECUTION = false;

    // AbstractGameState Variables
    public static final boolean OPP_BUILDING_REACHABLE = false;
    public static boolean OPP_UNIT_REACHABLE = false;

    public static final int CURRENT_NUM_RESOURCES = 0;
    public static final int CURRENT_NUM_WORKDERS = 0;
    public static final int CURRENT_NUM_BASES = 0;
    public static final int CURRENT_NUM_BARRACKS = 0;
    public static final int CURRENT_NUM_MELEE = 0;

    // ********************************
    //  EXECUTION VARIABLE
    // ********************************
    public static final boolean REWARD_FROM_EXECUTION = true;

    public static void reset() {
        // TODO : Implement the reset
    }

    public static void UpdateGameStateStatistics(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();


        // TODO : Use a more clever way to judge the distance
        Helper.MAX_MAP_DIST = pgs.getWidth() + pgs.getHeight();

        UpdateAllUnitsInfos(gs);

        AssignOpponetBuildings(gs);
        AssignOppMobileUnits(gs);
        AssignResources(gs);

        if (!OPP_BUILDING_REACHABLE && !OPP_UNIT_REACHABLE) {
            DESIRED_NUM_BARRACKS = Math.max(DESIRED_NUM_BARRACKS, 1);
            DESIRED_NUM_RESOURCES = Math.max(BARRACKS_TYPE.cost * DESIRED_NUM_BARRACKS, DESIRED_NUM_RESOURCES);
        }
    }

    // TODO : Implement THESE!
    private static void AssignResources(GameState gs) {

    }

    private static void AssignOppMobileUnits(GameState gs) {

    }

    private static void AssignOpponetBuildings(GameState gs) {

    }

    private static void UpdateAllUnitsInfos(GameState gs) {
    }
}
