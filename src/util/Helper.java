package util;

import GlassCannon.Planner;
import PathFinding.SimpleFloodFill;
import rts.GameState;
import rts.PhysicalGameState;
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

    public static HashMap<Long, Long> MOBILE_UNIT_BASE_MAP = new HashMap<Long, Long>();
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
    public static  boolean DEBUG_EF_VALUES = false;
    public static boolean DEBUG_METHOD_DECOMPOSITION = false;
    public static boolean DEBUG_PLANNER = false;

    public static boolean DEBUG_TIMEOUT = false;
    public static boolean DEBUG_TASK_INCREASE_STATISTICS = false;
    public static boolean DEBUG_ACTION_EXECUTION = true;
    public static boolean DEBUG_POSTCONDITIONS = false;

    public static int DEBUG_INITIALISATION = 0;
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

    public static int CURRENT_NUM_RANGED = 0;
    public static int CURRENT_NUM_OPP_MOBILE_UNITS=0;
    public static int CURRENT_NUM_OPP_MILITARY_UNITS = 0;
    // ********************************
    // GAMESTATE VARIABLES
    // ********************************
    public static int OBSERVABLE = 0;

    public static int DESIRED_NUM_RESOURCES = 0;
    public static int DESIRED_NUM_BARRACKS = 0;
    public static int DESIRED_NUM_MELEE = 0;
    public static int DESIRED_NUM_BASES = 0;

    public static HashMap<Long, int[][]> MY_BASES_FLOODFILL = new HashMap<Long,int[][]>();
    public static HashMap<Long, Pair<Long, int[][]>> PER_BASE_CLOSEST_OPP_BUILDING_INFOS = new HashMap<Long,Pair<Long, int[][]>>();
    public static HashMap<Long, Unit> PER_BASE_CLOSEST_OPP_INFOS = new HashMap<Long, Unit>();
    public static HashMap<Long, Pair<Long, int[][]>> PER_BASE_CLOSEST_RES_INFOS = new HashMap<Long, Pair<Long, int[][]>>();

    //TODO : Lanchester requires opponent's HP and status, where to implement?
    //public static final int CURRENT_NUM_OPP_MOBILE_UNITS = 0;
    //public static final int CURRENT_NUM_OPP = 0;

    // ********************************
    //  EXECUTION VARIABLE
    // ********************************
    public static final boolean REWARD_FROM_EXECUTION = true;

    public static void reset() {
        OBSERVABLE = 0;
        MY_BASES = new HashMap<Long, Pair<Integer, Integer>>();
        MY_BASES_FLOODFILL = new HashMap<Long, int[][]>();
        MOBILE_UNIT_BASE_MAP = new HashMap<Long, Long>();
        OPP_BUILDINGS = new ArrayList<Unit>();
        PER_BASE_CLOSEST_OPP_BUILDING_INFOS = new HashMap<Long, Pair<Long, int[][]>>();
        OPP_MOBILE_UNITS = new ArrayList<Unit>();
        PER_BASE_CLOSEST_OPP_INFOS = new HashMap<Long, Unit>();
        RESOURCES = new ArrayList<Unit>();
        PER_BASE_CLOSEST_RES_INFOS = new HashMap<Long, Pair<Long, int[][]>>();

        DESIRED_NUM_BARRACKS = 0;
        DESIRED_NUM_MELEE = 0;
        DESIRED_NUM_WORKERS = 0;
        DESIRED_NUM_BASES = 0;
        DESIRED_NUM_RESOURCES = 0;

        CURRENT_NUM_BARRACKS = 0;
        CURRENT_NUM_MELEE = 0;
        CURRENT_NUM_RANGED = 0;
        CURRENT_NUM_WORKERS = 0;
        CURRENT_NUM_BASES = 0;
        CURRENT_NUM_OPP_MOBILE_UNITS = 0;
        CURRENT_NUM_OPP_MILITARY_UNITS = 0;

        OPP_BUILDING_REACHABLE = false;
        OPP_UNIT_REACHABLE = false;
    }

    public static void UpdateGameStateStatistics(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();


        // TODO : Use a more clever way to judge the distance
        Helper.MAX_MAP_DIST = pgs.getWidth() + pgs.getHeight();
        if (OBSERVABLE == 0) {
            CheckObservability(gs);
        }

        UpdateAllUnitsInfos(gs);

        AssignOpponentBuildings(gs);
        AssignOppMobileUnits(gs);
        AssignResources(gs);

        if (!OPP_BUILDING_REACHABLE && !OPP_UNIT_REACHABLE) {
            DESIRED_NUM_BARRACKS = Math.max(DESIRED_NUM_BARRACKS, 1);
            DESIRED_NUM_RESOURCES = Math.max(BARRACKS_TYPE.cost * DESIRED_NUM_BARRACKS, DESIRED_NUM_RESOURCES);
        }
    }
    public static void CheckObservability(GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        int width = pgs.getWidth();
        int height = pgs.getHeight();
        Helper.MAX_MAP_DIST = width + height;

        boolean observable = gs.observable(0, 0) && gs.observable(0, height - 1) && gs.observable(width - 1, 0)
                && gs.observable(width - 1, height - 1);
        Helper.OBSERVABLE = observable ? 1 : -1;
    }

    private static void AssignResources(GameState gs) {
        if (RESOURCES.isEmpty()) {
            PER_BASE_CLOSEST_RES_INFOS.clear();
            return;
        }

        PhysicalGameState pgs = gs.getPhysicalGameState();

        HashMap<Long, Pair<Long, int[][]>> tempInfos = (HashMap<Long, Pair<Long, int[][]>>) PER_BASE_CLOSEST_RES_INFOS
                .clone();

        for (long baseID : tempInfos.keySet()) {
            Unit base = pgs.getUnit(baseID);
            if (base == null) {
                PER_BASE_CLOSEST_RES_INFOS.remove(baseID);
                continue;
            }

            Pair<Long, int[][]> resInfo = PER_BASE_CLOSEST_RES_INFOS.get(baseID);
            if (resInfo == null || pgs.getUnit(resInfo.m_a) == null) {
                PER_BASE_CLOSEST_RES_INFOS.remove(baseID);
                FindClosestReachableResourceForBase(base, gs);
            }
        }
    }

    private static void FindClosestReachableResourceForBase(Unit base, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();

        SimpleFloodFill pathFinding;
        pathFinding = new SimpleFloodFill();

        int[][] closestUnitDistances = null;
        Unit closestUnit = null;

        int tempDist = Integer.MAX_VALUE;

        for (int b = 0; b < RESOURCES.size(); b++) {
            Unit u = RESOURCES.get(b);

            int[][] distances = pathFinding.calculateDistancesWithoutTarget(u.getX(), u.getY(), 0, gs,
                    gs.getResourceUsage());

            if (distances != null) {
                int currentDist = GetMinDist(distances, base.getX(), base.getY());
                if (currentDist < tempDist) {
                    tempDist = currentDist;
                    closestUnitDistances = distances;
                    closestUnit = u;
                }
            }
        }

        if (closestUnit != null) {
            PER_BASE_CLOSEST_RES_INFOS.put(base.getID(), new Pair(closestUnit.getID(), closestUnitDistances));
        }
    }

    private static void AssignOppMobileUnits(GameState gs) {
        if (OPP_MOBILE_UNITS.isEmpty()) {
            PER_BASE_CLOSEST_OPP_INFOS.clear();
            OPP_UNIT_REACHABLE = false;
            return;
        }

        PhysicalGameState pgs = gs.getPhysicalGameState();
        HashMap<Long, Pair<Long, int[][]>> tempInfos = (HashMap<Long, Pair<Long, int[][]>>) PER_BASE_CLOSEST_OPP_INFOS
                .clone();
        OPP_UNIT_REACHABLE = false;

        for (long baseID : tempInfos.keySet()) {
            Unit base = pgs.getUnit(baseID);
            if (base == null) {
                PER_BASE_CLOSEST_OPP_INFOS.remove(baseID);
                continue;
            }

            PER_BASE_CLOSEST_OPP_INFOS.remove(baseID);
            FindClosestReachableOppUnitForBase(base, gs, true);
        }
    }

    public static Long GetBaseIDForUnit(Unit unit, GameState gs, boolean shouldAssign) {

        Long baseID = MOBILE_UNIT_BASE_MAP.get(unit.getID());

        if (baseID != null && MY_BASES.containsKey(baseID)) {
            return baseID;
        }

        Long prevBaseId = baseID;

        // Base was destroyed
        if (baseID != null) {
            MOBILE_UNIT_BASE_MAP.remove(unit.getID());
        }

        baseID = AssignToOrFindClosestBase(unit, gs, shouldAssign);

        // could not be assigned to another base, keep prev info for ref
        if (baseID == null && prevBaseId != null) {
            baseID = prevBaseId;
            MOBILE_UNIT_BASE_MAP.put(unit.getID(), baseID);
        }

        if (baseID == null) {
            int a = 0;
        }

        return baseID;
    }

    public static Unit FindClosestReachableOppUnitForBase(Unit base, GameState gs, boolean shouldAssign) {
        PhysicalGameState pgs = gs.getPhysicalGameState();

        SimpleFloodFill pathFinding;
        pathFinding = new SimpleFloodFill();

        int[][] closestUnitDistances = null;
        Unit closestUnit = null;

        int tempDist = Integer.MAX_VALUE;

        for (int b = 0; b < OPP_MOBILE_UNITS.size(); b++) {
            Unit u = OPP_MOBILE_UNITS.get(b);

            int[][] distances = pathFinding.pathExistsDist(base.getX(), base.getY(), u.getPosition(pgs), gs,
                    gs.getResourceUsage());

            if (distances != null) {
                int currentDist = GetMinDist(distances, base.getX(), base.getY());
                if (currentDist < tempDist) {
                    tempDist = currentDist;
                    closestUnitDistances = distances;
                    closestUnit = u;
                }
            }
        }

        if (closestUnit != null) {
            if (shouldAssign) {
                PER_BASE_CLOSEST_OPP_INFOS.put(base.getID(), closestUnit);
                OPP_UNIT_REACHABLE = true;

                // if(DEBUG_INITIALISATION >= 2)
                // {
                // System.out.println("Opp at " + closestUnit.getX() + "/" + closestUnit.getY()
                // + " assigned to " + base.getX() + "/" + base.getY());
                // }
                return closestUnit;
            }
        }
        return null;
    }

    private static int GetMinDist(int[][] distances, int x, int y) {
        int tempDist = Integer.MAX_VALUE;

        if (x > 0) // left
        {
            if (distances[x - 1][y] < tempDist) {
                tempDist = distances[x - 1][y];
            }
        }

        if (x < WIDTH - 1)// right
        {
            if (distances[x + 1][y] < tempDist) {
                tempDist = distances[x + 1][y];
            }
        }

        if (y < HEIGHT - 1) // up
        {
            if (distances[x][y + 1] < tempDist) {
                tempDist = distances[x][y + 1];
            }
        }

        if (y > 0) // down
        {
            if (distances[x][y - 1] < tempDist) {
                tempDist = distances[x][y - 1];
            }
        }

        return tempDist;
    }

    private static void AssignOpponentBuildings(GameState gs) {
       if (OPP_BUILDINGS.isEmpty()) {
           PER_BASE_CLOSEST_OPP_INFOS.clear();
           OPP_BUILDING_REACHABLE = false;
           return;
       }
        HashMap<Long, Pair<Long, int[][]>> tempInfos = (HashMap<Long, Pair<Long, int[][]>>) PER_BASE_CLOSEST_OPP_BUILDING_INFOS
                .clone();

       PhysicalGameState pgs = gs.getPhysicalGameState();

       OPP_BUILDING_REACHABLE = false;

       for (long baseID : tempInfos.keySet()){
           Unit base = pgs.getUnit(baseID);
           if (base == null){
               PER_BASE_CLOSEST_OPP_INFOS.remove(baseID);
               continue;
           }

           Pair<Long, int[][]> oppBuildingInfo = PER_BASE_CLOSEST_OPP_BUILDING_INFOS.get(baseID);
           if (oppBuildingInfo == null || pgs.getUnit(oppBuildingInfo.m_a) == null) {
               PER_BASE_CLOSEST_OPP_BUILDING_INFOS.remove(baseID);
               FindClosestReachableOppBuildingForBase(base, gs, true);
           }
       }

       if (!PER_BASE_CLOSEST_OPP_INFOS.isEmpty()){
           OPP_BUILDING_REACHABLE = true;
       }
    }

    public static Unit FindClosestReachableOppBuildingForBase(Unit base, GameState gs, boolean shouldAssign) {
        //TODO : If change the pathfinding alg, should modify here as well

        PhysicalGameState pgs = gs.getPhysicalGameState();

        SimpleFloodFill pathFinding;
        pathFinding = new SimpleFloodFill();

        int [][] closestUnitDistances = null;
        Unit closestUnit = null;

        int tempDist = Integer.MAX_VALUE;

        for (int b = 0; b < OPP_BUILDINGS.size(); b++){
            Unit u = OPP_BUILDINGS.get(b);

            int[][] distances = pathFinding.calculateDistancesWithoutTarget(u.getX(), u.getY(), 0, gs,
                    gs.getResourceUsage());

            if (distances != null) {
                int currentDist = GetMinDist(distances, base.getX(), base.getY());
                if (currentDist < tempDist) {
                    tempDist = currentDist;
                    closestUnitDistances = distances;
                    closestUnit = u;
                }
            }
        }
        if (closestUnit != null) {
            if (shouldAssign) {
                PER_BASE_CLOSEST_OPP_BUILDING_INFOS.put(base.getID(),
                        new Pair(closestUnit.getID(), closestUnitDistances));

                OPP_BUILDING_REACHABLE = true;
                // if(DEBUG_INITIALISATION >= 2)
                // {
                // System.out.println("Opp building at " + closestUnit.getX() + "/" +
                // closestUnit.getY() + " assigned to " + base.getX() + "/" + base.getY());
                // }
            }
            return closestUnit;
        }
        return null;
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
        SimpleFloodFill pathFinding;
        pathFinding = new SimpleFloodFill();

        for (Unit u : pgs.getUnits()){
            UnitType uType = u.getType();
            boolean myUnit = (u.getPlayer() == Planner.INSTANCE.player);

            // TODO : Edit BEHAVIOR HERE
            if (uType == BASE_TYPE){
                if (myUnit){
                    if (!MY_BASES.containsKey(u.getID())) {
                        MY_BASES.put(u.getID(),new Pair(u.getX(),u.getY()));

                        int [][] distance = pathFinding.calculateDistancesWithoutTarget(u.getX(),u.getY(),0,gs,
                                gs.getResourceUsage());
                        MY_BASES_FLOODFILL.put(u.getID(), distance);
                    }
                    CURRENT_NUM_BASES++;

                    long baseID = u.getID();

                    if (!PER_BASE_CLOSEST_OPP_BUILDING_INFOS.containsKey(baseID)){
                        PER_BASE_CLOSEST_OPP_BUILDING_INFOS.put(baseID, null);
                    }
                    if (!PER_BASE_CLOSEST_OPP_INFOS.containsKey(baseID)){
                        PER_BASE_CLOSEST_OPP_INFOS.put(baseID, null);
                    }
                    if (!PER_BASE_CLOSEST_RES_INFOS.containsKey(baseID)) {
                        PER_BASE_CLOSEST_RES_INFOS.put(baseID, null);
                    }
                }
                else{
                    OPP_BUILDINGS.add(u);
                }
                continue;
            }

            //TODO : BARRACKS BEHAVIOR
            if (uType==BARRACKS_TYPE){
                if(myUnit){
                    CURRENT_NUM_BARRACKS ++ ;
                }
                else{
                    OPP_BUILDINGS.add(u);
                }
                continue;
            }

            if (uType == LIGHT_TYPE || uType == RANGED_TYPE || uType == HEAVY_TYPE){
                if (myUnit){
                    CURRENT_NUM_MELEE ++;

                    if (uType == RANGED_TYPE){
                        CURRENT_NUM_RANGED++;
                    }
                    Long baseID = MOBILE_UNIT_BASE_MAP.get(u.getID());
                    if(baseID == null){
                        AssignToOrFindClosestBase(u,gs, true);
                    }
                }
                else{
                    CURRENT_NUM_OPP_MILITARY_UNITS ++;
                    CURRENT_NUM_OPP_MOBILE_UNITS++;
                    OPP_MOBILE_UNITS.add(u);
                }
                continue;

            }
            if (uType == WORKER_TYPE) {
                if (myUnit) {
                    CURRENT_NUM_WORKERS++;

                    Long baseID = MOBILE_UNIT_BASE_MAP.get(u.getID());
                    if (baseID == null) {
                        AssignToOrFindClosestBase(u, gs, true);
                    }
                } else {
                    CURRENT_NUM_OPP_MOBILE_UNITS++;
                    OPP_MOBILE_UNITS.add(u);
                }
                continue;
            }

            if (uType == RESOURCE_TYPE) {
                RESOURCES.add(u);
            }
        }


    }

    private static Long AssignToOrFindClosestBase(Unit unit, GameState gs, boolean shouldAssign) {
        if (MY_BASES.isEmpty()){
            return null;
        }

        PhysicalGameState pgs = gs.getPhysicalGameState();

        int tempDist = Integer.MAX_VALUE;
        Long tempBaseID = null;
        int tempX = -1;
        int tempY = -1;

        HashMap<Long, Pair<Integer, Integer>> myBaseCopy = (HashMap<Long, Pair<Integer, Integer>>) MY_BASES.clone();
        for (Long baseId : MY_BASES.keySet()){
            Unit base = pgs.getUnit(baseId);
            int baseX = MY_BASES.get(baseId).m_a;
            int baseY = MY_BASES.get(baseId).m_b;

            int dist = Math.abs(unit.getX() - baseX) + Math.abs(unit.getY() - baseY);

            if (dist < tempDist) {
                tempDist = dist;
                tempBaseID = baseId;
                tempX = baseX;
                tempY = baseY;
                // tempBase = base;
            }

            if (base == null) {
                if (shouldAssign) {
                    // MY_BASES.remove(baseId);
                    myBaseCopy.remove(baseId);
                }
            }
        }
        MY_BASES = myBaseCopy;

        if (shouldAssign) {
            MOBILE_UNIT_BASE_MAP.put(unit.getID(), tempBaseID);
        }
        // if(DEBUG_INITIALISATION >= 2)
        // {
        // System.out.println("Unit at " + unit.getX() + "/" + unit.getY() + " assigned
        // to " + tempX + "/" + tempY);
        // }
        return tempBaseID;
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
