package GlassCannon;

import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Harvest;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.ahtn.domain.Parameter;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.mcts.naivemcts.NaiveMCTS;
import org.junit.runners.model.InitializationError;
import rts.*;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Helper;
import util.Pair;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class GlassCannon extends NaiveMCTS {
    Deque<PrimitiveTask> currentPlan = null;
    PrimitiveTask currentTask = null;

    Planner planner = null;

    UnitTypeTable m_utt = null;

    int framesSinceLastUpdate = 0;

    int UPDATE_FREQUENCY = 10;
    static int TIME = 100;
    static int MAX_SIMULATION_TIME = 100;


    public GlassCannon(UnitTypeTable utt) {
        super(utt);
    }

    public GlassCannon(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth, float e_l, float discout_l, float e_g, float discout_g, float e_0, float discout_0, AI policy, EvaluationFunction a_ef, boolean fensa) {
        super(available_time, max_playouts, lookahead, max_depth, e_l, discout_l, e_g, discout_g, e_0, discout_0, policy, a_ef, fensa);
        Initialize(utt);
    }

    public GlassCannon(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth, float e_l, float e_g, float e_0, AI policy, EvaluationFunction a_ef, boolean fensa) {
        super(available_time, max_playouts, lookahead, max_depth, e_l, e_g, e_0, policy, a_ef, fensa);
        Initialize(utt);
    }

    public GlassCannon(UnitTypeTable utt, int available_time, int max_playouts, int lookahead, int max_depth, float e_l, float e_g, float e_0, int a_global_strategy, AI policy, EvaluationFunction a_ef, boolean fensa) {
        super(available_time, max_playouts, lookahead, max_depth, e_l, e_g, e_0, a_global_strategy, policy, a_ef, fensa);
        Initialize(utt);
    }


    private void Initialize(UnitTypeTable utt) {
        planner = new OrderedPlanner(utt); // TODO : Implement the Planner
        currentPlan = new LinkedList<PrimitiveTask>();

        m_utt = utt;

        Helper.BASE_TYPE = utt.getUnitType("Base");
        Helper.WORKER_TYPE = utt.getUnitType("Worker");
        Helper.BARRACKS_TYPE = utt.getUnitType("Barracks");
        Helper.HEAVY_TYPE = utt.getUnitType("Heavy");
        Helper.LIGHT_TYPE = utt.getUnitType("Light");
        Helper.RANGED_TYPE = utt.getUnitType("Ranged");
        Helper.RESOURCE_TYPE = utt.getUnitType("Resource");
    }

    public void reset() {
        currentPlan.clear();
        Helper.reset();
        super.reset();
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.getTime() == 0) {
            Planner.INSTANCE.player = player;
            Helper.reset();
        }

        //long time = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        planner.time = startTime;
        framesSinceLastUpdate++;

        // TODO : This place can distribute time between different Algorithms
        // TODO : Distribute time between HTN and MCTS
        // Original Function uses HTN and MCTS
        if (!gs.canExecuteAnyAction(player) && framesSinceLastUpdate >= UPDATE_FREQUENCY) {
            Helper.UpdateGameStateStatistics(gs);
            framesSinceLastUpdate = 0;
            return super.getAction(player, gs);
        }

        boolean decisionMade = false;
        boolean replan = false;

        AbstractGameState ags = new AbstractGameState(gs);

        if (currentTask != null) {
            if (currentTask.IsReached(player, 1 - player, gs)) {
                // DO nothing
            } else if (currentTask.CheckPreconditions(ags)) {
                decisionMade = true;
            } else {
                replan = true;
            }
        }

        // Replan
        while (!decisionMade) {
            long curTime = System.currentTimeMillis();

            if (curTime - startTime >= Helper.MAX_TIME_FOR_PLANNER) {
                if (Helper.DEBUG_ACTION_EXECUTION) {
                    System.out.println("No time for HTN - Try MCTS");
                }
                return super.getAction(player, gs);
            }

            // Create plan
            if (currentPlan.isEmpty() || replan) {
                currentPlan = planner.CreatePlan(player, gs);
            }

            // Get Next plan task
            if (!currentPlan.isEmpty()) {
                currentTask = currentPlan.removeFirst();

                boolean holds = currentTask.CheckPreconditions(ags);
                // It is applicable or we don't have enough time to re-plan
                if (holds || curTime - startTime >= Helper.MAX_TIME_FOR_PLANNER) {
                    this.setEvaluationFunction(currentTask.GetTaskEF());
                    if (Helper.DEBUG_ACTION_EXECUTION) {
                        System.out.println("Evaluation Function : " + this.ef.toString());
                    }
                    decisionMade = true;
                }
                if (!holds) {
                    if (Helper.DEBUG_ACTION_EXECUTION) {
                        System.out.println("Task : " + currentTask.taskName + "not holds, re planning");
                    }
                    currentPlan.clear();
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long timeSpent = (endTime - startTime);

        TIME_BUDGET = (int) Math.max(1, (TIME - timeSpent - 5));

        PlayerAction action = super.getAction(player, gs);

        if (Helper.CURRENT_NUM_WORKERS == 0
                && gs.canExecuteAnyAction(player) && Helper.CURRENT_NUM_MELEE == 0
                && gs.getPlayer(Planner.INSTANCE.player).getResources() != 0 && Helper.MY_BASES.isEmpty()
                && gs.getTime() <= 10) {
            Unit base = gs.getPhysicalGameState().getUnit(Helper.MY_BASES.keySet().iterator().next());

            if (action.getAction(base) == null) {
                PlayerAction alternativeAction = GetProducerAction(gs);
                if (alternativeAction != null) {
                    return alternativeAction;
                }
            }
        }
        return action;

    }

    public PlayerAction GetProducerAction(GameState gs) {
        Unit base = gs.getPhysicalGameState().getUnit(Helper.MY_BASES.keySet().iterator().next());
        List<PlayerAction> children = super.tree.actions;

        for (PlayerAction pa : children) {
            for (Pair<Unit, UnitAction> uaa : pa.getActions()) {
                if (uaa.m_a.getID() == base.getID() && uaa.m_b.getType() == UnitAction.TYPE_PRODUCE
                        && uaa.m_b.getUnitType() == Helper.WORKER_TYPE) {
                    return pa;
                }
            }
        }

        return null;
    }

    @Override
    public AI clone() {
        return new GlassCannon(m_utt, TIME_BUDGET, ITERATIONS_BUDGET, MAXSIMULATIONTIME, MAX_TREE_DEPTH, epsilon_l, discount_l,
                epsilon_g, discount_g, epsilon_0, discount_0, playoutPolicy, ef,
                forceExplorationOfNonSampledActions);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        return super.getParameters();
    }



}
