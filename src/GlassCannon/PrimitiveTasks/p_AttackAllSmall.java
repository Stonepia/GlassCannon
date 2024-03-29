package GlassCannon.PrimitiveTasks;

import GlassCannon.AbstractGameState;
import GlassCannon.Planner;
import GlassCannon.PrimitiveTask;
import GlassCannon.PrimitiveTasks.EvaluationFunctions.*;
import rts.GameState;
import util.Helper;

public class p_AttackAllSmall extends PrimitiveTask {

    public p_AttackAllSmall() {
        super();

        this.taskName = "p_AttackAllSmall";

        this.taskEF = new AttackAllEF();
    }


    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        boolean holds = true;

		if (Helper.PER_BASE_CLOSEST_RES_INFOS.isEmpty()) {
        return true;
    }

		if (!currentGameState.anyOppUnitReachable && !currentGameState.anyOppBuildingReachable) {
        holds = false;
        // if(Helper.DEBUG_PRECONDITIONS)
        // {
        // System.out.println(this.name + " does not hold: enemy is not reachable");
        // }
    }

		if (currentGameState.currentNumWorkers
				+ currentGameState.currentNumMelee < Helper.CURRENT_NUM_OPP_MOBILE_UNITS - 3
            && Helper.CURRENT_NUM_MELEE <= (Helper.CURRENT_NUM_OPP_MOBILE_UNITS
						- Helper.CURRENT_NUM_OPP_MILITARY_UNITS) / 2)

    {
        holds = false;
        // if(Helper.DEBUG_PRECONDITIONS)
        // {
        // System.out.println(this.name + " does not hold: i have less units");
        // }
    }

		return holds;
}

    public boolean IsReached(int maxplayer, int minplayer, GameState currentGameState) {
        boolean reached = false;

        if (currentGameState.gameover() && currentGameState.winner() == Planner.INSTANCE.player) {
            reached = true;
        }

        // if(reached && Helper.DEBUG_POSTCONDITIONS)
        // {
        // System.out.println(this.name + " reached");
        // }
        return reached;
    }

    @Override
    public AbstractGameState Simulate(AbstractGameState prevGameState) {
        AbstractGameState agsCopy = super.Simulate(prevGameState);

        agsCopy.anyOppBuildingReachable = false;

        return agsCopy;
    }
}
