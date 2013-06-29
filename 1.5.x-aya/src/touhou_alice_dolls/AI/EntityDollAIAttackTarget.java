package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.player.EntityPlayer;
import mods.touhou_alice_dolls.EntityAliceDoll;
import mods.touhou_alice_dolls.DollRegistry;
import net.minecraft.util.MathHelper;
import net.minecraft.util.DamageSource;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIAttackTarget extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private EntityLiving theTarget;
    private float speed;
    private int counter;
    private boolean avoidsWater;
    public static String targetEntityRegex;
    public static int attackStrength;

    public EntityDollAIAttackTarget(EntityAliceDoll doll)
    {
        super(doll);
        this.speed = doll.getSpeed();
        this.pathfinder = doll.getNavigator();
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode() || theDoll.isRideonMode())
        {
            return false;
        }
        if(theDoll.getDollID() != DollRegistry.getDollID("Shanghai"))
        {
            return false;
        }
        theTarget = theDoll.getAttackTarget();
        if(theTarget == null)
        {
            return false;
        }

        String name = EntityList.getEntityString(theTarget);
        if(name == null)
        {
            return false;
        }

        Pattern targetPattern = Pattern.compile(targetEntityRegex);
        Matcher targetMatcher = targetPattern.matcher(name);

        if(!targetMatcher.find())
        {
            return false;
        }

        return true;
    }

    @Override
    public void startExecuting()
    {
        counter = 0;
        this.avoidsWater = this.theDoll.getNavigator().getAvoidsWater();
        this.theDoll.getNavigator().setAvoidsWater(false);
    }

    @Override
    public boolean continueExecuting()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode() || theDoll.isRideonMode())
        {
            return false;
        }
        if(this.pathfinder.noPath())
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
        this.theDoll.getNavigator().setAvoidsWater(this.avoidsWater);
    }

    @Override
    public void updateTask()
    {
        if(!this.pathfinder.noPath())
        {
            this.theDoll.getLookHelper().setLookPositionWithEntity(
                this.theTarget, 10.0F, (float)this.theDoll.getVerticalFaceSpeed());
        }

        if (--this.counter <= 0)
        {
            this.counter = 20;

            this.pathfinder.tryMoveToEntityLiving(this.theTarget, this.speed);
            if(this.theDoll.getDistanceSqToEntity(this.theTarget) < 9f
               && this.theDoll.getEntitySenses().canSee(this.theTarget))
            {
                if (this.theDoll.getHeldItem() != null)
                {
                    this.theDoll.swingItem();
                }

                if(theDoll.getOwnerEntity() != null)
                {
                    theTarget.attackEntityFrom(
                        DamageSource.causePlayerDamage(
                            theDoll.getOwnerEntity()),attackStrength);
                }
                else
                {
                    theTarget.attackEntityFrom(
                        DamageSource.causeMobDamage(
                            theDoll),attackStrength);
                }
            }
        }
    }
}
