package mods.touhou_alice_dolls.AI;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import mods.touhou_alice_dolls.EntityAliceDoll;

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
