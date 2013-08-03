package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.player.EntityPlayer;
import mods.touhou_alice_dolls.EntityAliceDoll;
import net.minecraft.util.MathHelper;
import net.minecraft.util.DamageSource;

import mods.touhou_alice_dolls.THShotLibWrapper;

public class EntityDollAIAttackWithBullet extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private EntityLivingBase theTarget;
    private float speed;
    private int counter;

    public EntityDollAIAttackWithBullet(EntityAliceDoll doll)
    {
        super(doll);
        this.speed = 1.0F;
        this.pathfinder = doll.getNavigator();
        this.setMutexBits(3);
        counter = 0;
    }

    @Override
    public boolean shouldExecute()
    {
        if(!THShotLibWrapper.isEnable())
        {
            return false;
        }
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode() || theDoll.isRideonMode())
        {
            return false;
        }
        theTarget = theDoll.getAttackTarget();
        if(theTarget == null)
        {
            return false;
        }

        return true;
    }

    @Override
    public void startExecuting()
    {
    }

    @Override
    public boolean continueExecuting()
    {
        if(!THShotLibWrapper.isEnable())
        {
            return false;
        }
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode() || theDoll.isRideonMode())
        {
            return false;
        }
        if(this.theTarget == null)
        {
            return false;
        }
        if(!this.theTarget.isEntityAlive())
        {
            return false;
        }
        return true;
    }

    @Override
    public void resetTask()
    {
        this.theTarget = null;
        this.pathfinder.clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        if(this.theDoll.getEntitySenses().canSee(this.theTarget)
           && this.theTarget.isEntityAlive())
        {
            this.theDoll.getLookHelper().setLookPositionWithEntity(
                this.theTarget, 10.0F, (float)this.theDoll.getVerticalFaceSpeed());
        }

        if (--this.counter <= 0)
        {
            this.counter = 20;

            if(this.theDoll.getDistanceSqToEntity(this.theTarget) > 25f)
            {
                this.pathfinder.tryMoveToEntityLiving(
                    this.theTarget, this.speed);
            }
            if(this.theDoll.getEntitySenses().canSee(this.theTarget))
            {
                double xDistance = theTarget.posX - theDoll.posX;
    			double yDistance = theTarget.posY - (theDoll.posY+theDoll.getEyeHeight()/2);
    			double zDistance = theTarget.posZ - theDoll.posZ;
    			float angleXZ = 360F - ((float)Math.atan2(xDistance, zDistance)) / 3.141593F * 180F;
				float angleY  = 360F - (float)Math.atan2( yDistance, Math.sqrt(xDistance * xDistance + zDistance * zDistance)) / 3.141593F * 180F;

                if(THShotLibWrapper.getInstance().createWideShot01(
                       theDoll, angleXZ, angleY, 0.4D,
                       theDoll.getRNG().nextInt(8)+72, 5, 40))
                {
                    theDoll.playSound("random.bow", 2.0F, 0.8F);
                }
            }
        }
    }
}
