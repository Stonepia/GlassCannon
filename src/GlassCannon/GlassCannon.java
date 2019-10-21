package GlassCannon;

import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Harvest;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GlassCannon extends AbstractionLayerAI {
    protected UnitTypeTable utt;
    UnitType workerType;
    UnitType baseType;
    UnitType barracksType;
    UnitType lightType;
    UnitType rangedType;

    public GlassCannon(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
    }

    public GlassCannon(UnitTypeTable a_utt, PathFinding a_pf) {
        super(a_pf);
        reset(a_utt);
    }

    public void reset() {
        super.reset();
    }

    public void reset(UnitTypeTable a_utt) {
        utt = a_utt;
        workerType = utt.getUnitType("Worker");
        baseType = utt.getUnitType("Base");
        barracksType = utt.getUnitType("Barracks");
        lightType = utt.getUnitType("Light");
        rangedType = utt.getUnitType("Ranged");
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Player p = gs.getPlayer(player);

        //behaviour of bases:
        for (Unit u : pgs.getUnits()) {
            if (u.getType() == baseType &&
                    u.getPlayer() == player &&
                    gs.getActionAssignment(u) == null)
                baseBehavior(u, p, pgs);
        }

        //behaviour of workers:
        List<Unit> workers = new LinkedList<Unit>();
        for (Unit u : pgs.getUnits()){
            if (u.getType().canHarvest && u.getPlayer()==player){
                workers.add(u);
            }
        }

        workersBehavior(workers,p,pgs,gs);
        //behaviour of melee units:
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest
                    && u.getPlayer() == player
                    && gs.getActionAssignment(u) == null)
                meleeUnitBehavior(u, p, gs);
        }


        //behaviour of barracks:
        for (Unit u : pgs.getUnits()) {
            if (u.getType() == barracksType &&
                    u.getPlayer() == player &&
                    gs.getActionAssignment(u) == null)
                barracksBehavior(u, p, pgs);
        }
        // This method takes all the unit actions executed so far, and packages them into a PlayerAction
        return translateActions(player,gs);
    }

    private void meleeUnitBehavior(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        int mybase = 0;
        for (Unit u2: pgs.getUnits()){
            if (u2.getPlayer() >=0 && u2.getPlayer() != p.getID()){
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY()); // TODO: Maybe use another distance measurement?
                if (closestEnemy == null || d< closestDistance){
                    closestDistance = d;
                    closestEnemy = u2;
                }
            }
            else if (u2.getPlayer() == p.getID() && u2.getType() ==baseType)
                mybase = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());

        }
        // TODO : Instead of attacking closest enemy, attack the ranged enemy
        if (closestEnemy !=null && (closestDistance < pgs.getHeight() /2 || mybase < pgs.getHeight()/2)){
            attack(u,closestEnemy);
        }
        else{
            attack(u,null);
        }
    }

    private void baseBehavior(Unit u, Player p, PhysicalGameState pgs) {
        int nworkers = 0;
        for (Unit u2 : pgs.getUnits()){
            if(u2.getType() == workerType
                    && u2.getPlayer() == p.getID()){
                nworkers++;
            }
        }
        // TODO : If the enemy has more worker, train
        if (nworkers < 5 && p.getResources() >= workerType.cost){
            train(u,workerType);
        }
    }
    private void barracksBehavior(Unit u, Player p, PhysicalGameState pgs) {
        if (p.getResources() >= rangedType.cost)
            train(u,rangedType);

    }
    private void workersBehavior(List<Unit> workers, Player p, PhysicalGameState pgs, GameState gs) {
        int nbases = 0;
        int nbarracks = 0;

        int resourcesUsed = 0;

        //Unit harvestWorker = null;
        List<Unit> harvestWorkers = new LinkedList<>();

        List<Unit> freeWorkers = new LinkedList<Unit>();
        freeWorkers.addAll(workers);

        if (workers.isEmpty())
            return;

        for (Unit u2 : pgs.getUnits()){
            if (u2.getType() == baseType && u2.getPlayer()==p.getID())
            {
                nbases++;
            }
            if (u2.getType() == barracksType && u2.getPlayer() ==p.getID())
            {
                nbarracks++;
            }
        }

        List<Integer> reservedPositions = new LinkedList<Integer>();
        if (nbases == 0 && !freeWorkers.isEmpty()){
            if (p.getResources() >=baseType.cost + resourcesUsed){
                Unit u = freeWorkers.remove(0);
                buildIfNotAlreadyBuilding(u,baseType,u.getX()+1,u.getY()+1, reservedPositions,p,pgs);
                resourcesUsed += baseType.cost;
            }
        }


        // Harvest
        // TODO : Now is harvest with all the free workers, maybe harvest with some worker, and others to defend?

        if (freeWorkers.size() > 0)
        {
            harvestWorkers.add(freeWorkers.remove(0));
        }
        if (freeWorkers.size() > 0 && harvestWorkers.size() <2 && nbarracks>0){
            harvestWorkers.add(freeWorkers.remove(0));
        }

        for (Unit u : harvestWorkers){
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isResource) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestBase == null || d < closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource != null && closestBase != null) {
                AbstractAction aa = getAbstractAction(u);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest)aa;
                    if (h_aa.getTarget() != closestResource || h_aa.getBase()!=closestBase) harvest(u, closestResource, closestBase);
                } else {
                    harvest(u, closestResource, closestBase);
                }
            }
        }

        if (nbarracks == 0 && freeWorkers.size() > 3){
            // TODO : when to build?
            // build a barracks
            if ((p.getResources() >= barracksType.cost + resourcesUsed )
                    && freeWorkers.size() > 0){
                Unit u = freeWorkers.remove(freeWorkers.size()-1);
                buildIfNotAlreadyBuilding(u,barracksType,u.getX(),u.getY(), reservedPositions,p,pgs);
                resourcesUsed += barracksType.cost;

            }
        }
        // System.out.println(freeWorkers.size());
        for (Unit u : freeWorkers) meleeUnitBehavior(u,p,gs);

    }

    @Override
    public AI clone() {
        return new GlassCannon(utt, pf);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));
        return parameters;
    }
}
