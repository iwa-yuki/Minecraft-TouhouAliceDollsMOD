package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.DamageSource;
import mods.touhou_alice_core.ai.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIAttackEnemy extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private EntityLivingBase theTarget;
    private float speed;
    private int counter;
    private int targetLost;
    private boolean avoidsWater;
    public static double searchRange;
    public static double searchHeight;
    public static String targetEntityRegex;
    public static int attackStrength;

    public EntityDollAIAttackEnemy(EntityAliceDoll doll)
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
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode() || theDoll.isRideonMode())
        {
            return false;
        }
        if(--counter > 0)
        {
            return false;
        }
        counter = 20;
        
        //攻撃対象設定
        Pattern targetPattern = Pattern.compile(targetEntityRegex);
        Matcher targetMatcher;
        List<EntityLivingBase> targetList =
            (List<EntityLivingBase>)(theWorld.getEntitiesWithinAABB(EntityLivingBase.class, theDoll.getEntityBoundingBox().expand(searchRange, searchHeight, searchRange)));
        theTarget = null;
        for(EntityLivingBase e : targetList)
        {
            String name = EntityList.getEntityString(e);
            if(name == null || name == "")
            {
                continue;
            }
            
            if(theDoll.isPatrolMode())
            {
                targetMatcher = targetPattern.matcher(name);
                if(targetMatcher.find())
                {
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else
                    {
                        if(theDoll.getDistanceSqToEntity(theTarget)
                           > theDoll.getDistanceSqToEntity(e))
                        {
                            theTarget = e;
                        }
                    }
                }
            }
        
            if(theDoll.isFollowMode())
            {
                Entity tt = null;
                if(e instanceof EntityCreature)
                {
                    tt = ((EntityCreature)e).getEntityToAttack();
                }
                else if(e instanceof EntityLiving)
                {
                    tt = ((EntityLiving)e).getAttackTarget();
                }
                if(theDoll.isOwner(tt))
                {
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else
                    {
                        if(theDoll.getDistanceSqToEntity(theTarget)
                           > theDoll.getDistanceSqToEntity(e))
                        {
                            theTarget = e;
                        }
                    }
                }
            }
        }
        
        if(theTarget == null)
        {
            return false;
        }

        return true;
    }

    @Override
    public void startExecuting()
    {
    	super.startExecuting();
    	
        counter = 0;
        targetLost = 0;
        this.avoidsWater = ((PathNavigateGround)this.theDoll.getNavigator()).func_179689_e();
        ((PathNavigateGround)this.theDoll.getNavigator()).func_179690_a(false);
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
        if(targetLost >= 60)
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
        ((PathNavigateGround)this.theDoll.getNavigator()).func_179690_a(this.avoidsWater);
        counter = 0;
        
        super.resetTask();
    }

    @Override
    public void updateTask()
    {
        if(!this.theTarget.isEntityAlive())
        {
            return;
        }
        if(this.theDoll.getEntitySenses().canSee(this.theTarget))
        {
            this.theDoll.getLookHelper().setLookPositionWithEntity(
                this.theTarget, 10.0F, (float)this.theDoll.getVerticalFaceSpeed());
            targetLost = targetLost > 0 ? (targetLost - 1) : 0;
        }
        else
        {
            targetLost++;
        }

        if (--this.counter <= 0)
        {
            this.counter = 20;

            this.pathfinder.tryMoveToEntityLiving(this.theTarget, this.speed);
            if(this.theDoll.getDistanceSqToEntity(this.theTarget) < 9f
               && this.theDoll.getEntitySenses().canSee(this.theTarget)
               && this.theTarget.isEntityAlive())
            {
                this.theDoll.swingItem();

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
