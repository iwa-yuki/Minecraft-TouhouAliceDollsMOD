package mods.touhou_alice_core.AI;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import mods.touhou_alice_core.EntityAliceDoll;

public class EntityDollAIBase extends EntityAIBase
{
    protected EntityAliceDoll theDoll;
    protected World theWorld;

    public EntityDollAIBase(EntityAliceDoll doll)
    {
        theDoll = doll;
        theWorld = doll.worldObj;
    }
    
    public boolean shouldExecute()
    {
        return false;
    }
}
