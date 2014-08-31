package mods.touhou_alice_extras.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_extras.*;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIAttackWithAmulet extends EntityDollAIBase
{
    public static double searchRange;
    public static double searchHeight;
    public static String targetEntityRegex;

    private PathNavigate pathfinder;
    private EntityLivingBase theTarget;
    private float speed;
    private int counter;

    public EntityDollAIAttackWithAmulet(EntityAliceDoll doll)
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
        if(theDoll.isRideonMode())
        {
            return false;
        }
        if(--counter > 0)
        {
            return false;
        }
        counter = 20;

        List<EntityLivingBase> targetList =
            (List<EntityLivingBase>)(theWorld.getEntitiesWithinAABB(EntityLivingBase.class, theDoll.boundingBox.expand(searchRange, searchHeight, searchRange)));
        Pattern targetPattern = Pattern.compile(targetEntityRegex);
        Matcher targetMatcher;

        theTarget = null;
        for(EntityLivingBase e : targetList)
        {
            String name = EntityList.getEntityString(e);
            if(name == null)
            {
                continue;
            }

            //攻撃対象設定
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
        theDoll.setTargetEntity(theTarget);
        return theTarget != null;
    }

    @Override
    public void startExecuting()
    {
        counter = 0;
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
        if(theDoll.isRideonMode())
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
        if(this.pathfinder.noPath()
           && !this.theDoll.getEntitySenses().canSee(this.theTarget))
        {
            return false;
        }
        return true;
    }

    @Override
    public void resetTask()
    {
        this.theTarget = null;
        this.theDoll.setTargetEntity(null);
        this.pathfinder.clearPathEntity();
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
        }
        if(this.counter % 20 == 0)
        {
            if(!this.theDoll.getEntitySenses().canSee(this.theTarget))
            {
                if(theDoll.isPatrolMode() || theDoll.isFollowMode())
                {
                    this.pathfinder.tryMoveToEntityLiving(
                        this.theTarget, this.speed);
                }
                else
                {
                    this.pathfinder.clearPathEntity();
                }
            }
            else
            {
                this.pathfinder.clearPathEntity();
            }
        }
        if (--this.counter <= 0)
        {
            this.counter = 80;

            if(this.theDoll.getEntitySenses().canSee(this.theTarget))
            {
            	double xSource = theDoll.posX;
            	double ySource = theDoll.posY+theDoll.getEyeHeight();
            	double zSource = theDoll.posZ;
            	
                double xDistance = theTarget.posX - theDoll.posX;
    			double yDistance = (theTarget.posY+theTarget.getEyeHeight())
                    - (theDoll.posY+theDoll.getEyeHeight());
    			double zDistance = theTarget.posZ - theDoll.posZ;
    			
    			double dd = Math.sqrt(xDistance*xDistance + yDistance*yDistance + zDistance*zDistance);
    			
    			double xCoord = xDistance / dd;
    			double yCoord = yDistance / dd;
    			double zCoord = zDistance / dd;
    			

                boolean shot = false;
                for(int i=0; i<8;++i)
                {
                    double dir = 45 * (double)(i) * Math.PI / 180D;
    				Object shotData = THShotLibWrapper.getInstance().createShotData(27, 0, 1.0F, 8.0F, i*2, 90, 10);
                    shot |= THShotLibWrapper.getInstance().createWideShot(
                    			theDoll, 
                    			Vec3.createVectorHelper(xSource + xCoord*Math.cos(dir)-zCoord*Math.sin(dir),
                    					                ySource + yCoord*Math.cos(dir),
                    					                zSource + xCoord*Math.sin(dir)+zCoord*Math.cos(dir)),
                    			Vec3.createVectorHelper(xCoord, yCoord, zCoord),
                    			0.7D,
                    			shotData,
                    			1,
                    			0);
                }
                if(shot)
                {
                    theDoll.playSound("random.bow", 2.0F, 0.8F);
                    theDoll.swingItem();
                }
            }
            else
            {
                this.counter = 20;
            }
        }
    }
}
