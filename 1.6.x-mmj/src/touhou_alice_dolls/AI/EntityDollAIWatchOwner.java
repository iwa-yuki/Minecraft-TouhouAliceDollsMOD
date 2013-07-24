package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import mods.touhou_alice_dolls.EntityAliceDoll;

public class EntityDollAIWatchOwner extends EntityDollAIBase
{
    private EntityPlayer owner;
    private float range;
    private int lookTime;
    private float probability;
    private Class watchedClass;

    public EntityDollAIWatchOwner(EntityAliceDoll doll)
    {
        super(doll);

        this.range = 8.0F;
        this.probability = 0.02F;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.theDoll.getRNG().nextFloat() >= this.probability)
        {
            return false;
        }
        else
        {
            owner = theDoll.getOwnerEntity();
            if(owner !=null && !theDoll.isRideonMode()
               && this.theDoll.getDistanceSqToEntity(owner) < (double)(this.range * this.range))
            {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean continueExecuting()
    {
        if(owner.isEntityAlive())
        {
            if(this.theDoll.getDistanceSqToEntity(owner) < (double)(this.range * this.range))
            {
                if(!this.theDoll.isRideonMode())
                {
                    if(this.lookTime > 0)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void startExecuting()
    {
        this.lookTime = 40 + this.theDoll.getRNG().nextInt(40);
    }

    @Override
    public void resetTask()
    {
        owner = null;
    }

    @Override
    public void updateTask()
    {
        float offset = 0.0f;
        if(theDoll.isRideonMode())
        {
            offset = -1.1f;
        }

        this.theDoll.getLookHelper().setLookPosition(
            owner.posX,
            owner.posY + (double)owner.getEyeHeight() + offset,
            owner.posZ,
            10.0F, (float)this.theDoll.getVerticalFaceSpeed());
        --this.lookTime;
    }

}
