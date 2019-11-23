package GlassCannon.Methods;

import GlassCannon.AbstractGameState;
import GlassCannon.CompoundTasks.c_SmallMap;
import GlassCannon.MethodsToTake;

public class m_handleSmallMap extends MethodsToTake {
    public  m_handleSmallMap(){
        super();

        this.methodName = "m_handleSmallMap";

        this.tasksToDecompose.add(new c_SmallMap());
    }
    @Override
    public boolean CheckPreconditions(AbstractGameState currentGameState) {
        return false;
    }
}
