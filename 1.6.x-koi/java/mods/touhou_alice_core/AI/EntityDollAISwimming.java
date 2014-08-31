package mods.touhou_alice_core.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_core.EntityAliceDoll;

/**
 * 泳ぐ
 */
public class EntityDollAISwimming extends EntityDollAIBase
{
    public EntityDollAISwimming(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(8);
        doll.getNavigator().setCanSwim(true);
    }

    public boolean shouldExecute()
    {
        if(theDoll.isRideonMode())
        {
            return false;
        }
        if(theDoll.isInWater() || this.theDoll.handleLavaMovement())
        {
            return true;
        }
        return false;
    }

    public void startExecuting()
    {
        if(theDoll.isStandbyMode())
        {
            theDoll.setPatrolMode();
        }
    }

    public void updateTask()
    {
        if (this.theDoll.getRNG().nextFloat() < 0.8F)
        {
            this.theDoll.getJumpHelper().setJumping();
        }
    }
}
