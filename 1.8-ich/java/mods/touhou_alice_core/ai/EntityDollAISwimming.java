package mods.touhou_alice_core.ai;

import net.minecraft.pathfinding.PathNavigateGround;
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
        ((PathNavigateGround)doll.getNavigator()).func_179693_d(true);
    }

    @Override
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

    @Override
    public void startExecuting()
    {
    	super.startExecuting();
    	
        if(theDoll.isStandbyMode())
        {
            theDoll.setPatrolMode();
        }
    }

    @Override
    public void updateTask()
    {
        if (this.theDoll.getRNG().nextFloat() < 0.8F)
        {
            this.theDoll.getJumpHelper().setJumping();
        }
    }
}
