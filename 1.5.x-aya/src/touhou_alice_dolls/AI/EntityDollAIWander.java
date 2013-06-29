package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_dolls.EntityAliceDoll;

public class EntityDollAIWander extends EntityDollAIBase
{
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;

    public EntityDollAIWander(EntityAliceDoll doll)
    {
        super(doll);
        this.speed = doll.getSpeed();
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (this.theDoll.getRNG().nextInt(100) != 0)
        {
            return false;
        }
        if(!this.theDoll.isPatrolMode())
        {
            return false;
        }

        this.xPosition = theDoll.posX + theDoll.getRNG().nextInt(2 * 10) - 10;
        this.yPosition = theDoll.posY + theDoll.getRNG().nextInt(2 * 7) - 7;
        this.zPosition = theDoll.posZ + theDoll.getRNG().nextInt(2 * 10) - 10;
        return true;
    }

    public boolean continueExecuting()
    {
        if(this.theDoll.getNavigator().noPath())
        {
            return false;
        }
        if(!this.theDoll.isPatrolMode())
        {
            return false;
        }
        return true;
    }

    public void startExecuting()
    {
        this.theDoll.getNavigator().tryMoveToXYZ(
            this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    @Override
    public void resetTask()
    {
        this.theDoll.getNavigator().clearPathEntity();
    }
}
