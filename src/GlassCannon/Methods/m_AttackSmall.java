package GlassCannon.Methods;

import GlassCannon.AbstractGameState;
import GlassCannon.PrimitiveTasks.p_AttackAllSmall;
import util.Helper;

public class m_AttackSmall extends GlassCannon.MethodsToTake {
    public m_AttackSmall(){
        super();

        this.methodName = "m_Attack";

        this.tasksToDecompose.add(new p_AttackAllSmall());
    }
    @Override
    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        if (Helper.MAX_MAP_DIST >=31){
            return false;
        }
        return false;
    }
}
