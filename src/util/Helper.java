package util;

import GlassCannon.Planner;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitType;

import java.util.ArrayList;
import java.util.HashMap;

public class Helper {

    // ********************************
    // DECISION HELPERS
    // ********************************
    public static  int DESIRED_NUM_WORKERS = 0;
    public static  float EPSILON = (float)0.1;

    // ********************************
    // GAMESTATE UNITS
    // ********************************
    public static HashMap<Long, Pair<Integer, Integer>> MY_BASES = new HashMap<Long, Pair<Integer, Integer>>();
    public static long MAX_TIME_FOR_PLANNER = 80; // TODO : Set for another number?


    public static  ArrayList<Unit> OPP_BUILDINGS = new ArrayList<Unit>();
    public static ArrayList<Unit> OPP_MOBILE_UNITS = new ArrayList<Unit> ();
    // ********************************
    // MAP INFORMATION
    // ********************************
    public static int MAX_MAP_DIST = -1;
    public static int WIDTH = -1;
    public static int HEIGHT = -1;
    public static int MAP_SIZE = -1;

    public static ArrayList<Unit> RESOURCES =new  ArrayList<Unit>();
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

    public static boolean DEBUG_TIMEOUT = false;
    public static boolean DEBUG_TASK_INCREASE_STATISTICS = false;
    public static boolean DEBUG_ACTION_EXECUTION = false;

    private static int DEBUG_INITIALISATION = 0;
    public static  boolean DEBUG_UCB_STATISTICS = false;


    // *******************************
    // AbstractGameState Variables
    // *******************************

    public static boolean OPP_BUILDING_REACHABLE = false;
    public static boolean OPP_UNIT_REACHABLE = false;

    public static int CURRENT_NUM_RESOURCES = 0;
    public static int CURRENT_NUM_WORKERS = 0;
    public static int CURRENT_NUM_BASES = 0;
    public static int CURRENT_NUM_BARRACKS = 0;
    public static int CURRENT_NUM_MELEE = 0;

    public static int CURRENT_NUM_OPP_MOBILE_UNITS=0;
    public static int CURRENT_NUM_OPP_MILITARY_UNITS = 0;
    // ********************************
    // GAMESTATE VARIABLES
    // ********************************
    private static int DESIRED_NUM_RESOURCES = 0;
    private static int DESIRED_NUM_BARRACKS = 0;
    private static int DESIRED_NUM_MELEE = 0;
    private static int DESIRED_NUM_BASES = 0;

    //TODO : Lanchester requires opponent's HP and status, where to implement?
    //public static final int CURRENT_NUM_OPP_MOBILE_UNITS = 0;
    //public static final int CURRENT_NUM_OPP = 0;

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
        PhysicalGameState pgs = gs.getPhysicalGameState();

        CURRENT_NUM_RESOURCES = 0;
        CURRENT_NUM_WORKERS = 0;
        CURRENT_NUM_BASES = 0;
        CURRENT_NUM_BARRACKS = 0;
        CURRENT_NUM_MELEE = 0;

        CURRENT_NUM_OPP_MILITARY_UNITS=0;
        CURRENT_NUM_OPP_MOBILE_UNITS=0;

        OPP_BUILDINGS.clear();
        OPP_MOBILE_UNITS.clear();
        RESOURCES.clear();

        // TODO : PATH FINDING HERE
        FloodFillPathFinding pathFinding;
        pathFinding = new FloodFillPathFinding();

        for (Unit u : pgs.getUnits()){
            UnitType uType = u.getType();
            boolean myUnit = (u.getPlayer() == Planner.INSTANCE.player);

            // TODO : BASE BEHAVIOUR
            if (uType == BASE_TYPE){
                if (myUnit){
                    if (!MY_BASES.containsKey(u.getID())) {

                    }
                }
            }
        }


    }

    public static void ComputeDesiredUnitNumbers(GameState gameState) {
       // check the map size, available units & available resources
        PhysicalGameState pgs = gameState.getPhysicalGameState();

        WIDTH = pgs.getWidth();
        HEIGHT = pgs.getHeight();
        MAX_MAP_DIST = WIDTH + HEIGHT;

        MAP_SIZE = MAX_MAP_DIST < 32 ? 0 : (MAX_MAP_DIST<48? 1:2);

        int playerRes = gameState.getPlayer(Planner.INSTANCE.player).getResources();
        int freeRes = 0;
        for (Unit u : pgs.getUnits()){
            if (u.getType() == Helper.RESOURCE_TYPE){
                freeRes += u.getResources();
            }
        }
        int maxResAvailable = playerRes + freeRes;

        if (MAX_MAP_DIST <31){
            // TODO : Reschedule this!
            Helper.DESIRED_NUM_BARRACKS = 0;
            Helper.DESIRED_NUM_BASES = 1;
            Helper.DESIRED_NUM_WORKERS = 2;
            Helper.DESIRED_NUM_MELEE = 3;
        }
        else if (MAX_MAP_DIST <48)  {
            // 16 - 23
            Helper.DESIRED_NUM_BARRACKS = 1;
            Helper.DESIRED_NUM_BASES = 1;
            Helper.DESIRED_NUM_WORKERS = 1;
            Helper.DESIRED_NUM_MELEE = 4;
        }
        else {
            Helper.DESIRED_NUM_BARRACKS = 1;
            Helper.DESIRED_NUM_BASES = 1;
            Helper.DESIRED_NUM_WORKERS = 1;
            Helper.DESIRED_NUM_MELEE = 5;
        }

        Helper.DESIRED_NUM_RESOURCES = Math.max(Helper.BARRACKS_TYPE.cost * Helper.DESIRED_NUM_BARRACKS, playerRes);

        if (Helper.DEBUG_INITIALISATION >= 1) {
            System.out.println("#bases=" + Helper.DESIRED_NUM_BASES + ", #barracks=" + Helper.DESIRED_NUM_BARRACKS
                    + ", #workers=" + Helper.DESIRED_NUM_WORKERS + ", #military= " + Helper.DESIRED_NUM_MELEE
                    + ", #res = " + Helper.DESIRED_NUM_RESOURCES);
        }

    }


}
