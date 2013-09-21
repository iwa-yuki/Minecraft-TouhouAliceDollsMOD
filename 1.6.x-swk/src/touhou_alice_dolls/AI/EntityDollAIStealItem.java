package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.player.EntityPlayer;
import mods.touhou_alice_dolls.EntityAliceDoll;
import net.minecraft.util.MathHelper;
import net.minecraft.util.DamageSource;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIStealItem extends EntityDollAIBase
{
    public static double searchRange;
    public static double searchHeight;

    private PathNavigate pathfinder;
    private float speed;
    private int counter;
    private boolean avoidsWater;
    private EntityLivingBase theTarget;
    
    public EntityDollAIStealItem(EntityAliceDoll doll)
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
        if(!theDoll.isPatrolMode())
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

        theTarget = null;
        //羊から毛を集める
        for(EntityLivingBase e : targetList)
        {
            if(e instanceof EntitySheep)
            {
                EntitySheep sheep = (EntitySheep)e;
                if(sheep.getSheared() || sheep.isChild())
                {
                    continue;
                }
                if(theTarget == null)
                {
                    theTarget = e;
                }
                else if(theDoll.getDistanceSqToEntity(theTarget)
                        > theDoll.getDistanceSqToEntity(e))
                {
                    theTarget = e;
                }
            }
        }
        if(theTarget != null)
        {
            return true;
        }
        //ニワトリから羽根を集める
        if(theDoll.getRNG().nextFloat()<0.05F)
        {
            for(EntityLivingBase e : targetList)
            {
                if(e instanceof EntityChicken)
                {
                    EntityChicken chicken = (EntityChicken)e;
                    if(chicken.isChild())
                    {
                        continue;
                    }
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else if(theDoll.getDistanceSqToEntity(theTarget)
                       > theDoll.getDistanceSqToEntity(e))
                    {
                        theTarget = e;
                    }
                }
            }
            if(theTarget != null)
            {
                return true;
            }
        }
        //キノコ牛からキノコを集める
        if(theDoll.getRNG().nextFloat()<0.05F)
        {
            for(EntityLivingBase e : targetList)
            {
                if(e instanceof EntityMooshroom)
                {
                    EntityMooshroom moo = (EntityMooshroom)e;
                    if(moo.isChild())
                    {
                        continue;
                    }
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else if(theDoll.getDistanceSqToEntity(theTarget)
                       > theDoll.getDistanceSqToEntity(e))
                    {
                        theTarget = e;
                    }
                }
            }
            if(theTarget != null)
            {
                return true;
            }
        }
        //アイアンゴーレムから花を集める
        if(theDoll.getRNG().nextFloat()<0.05F)
        {
            for(EntityLivingBase e : targetList)
            {
                if(e instanceof EntityIronGolem)
                {
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else if(theDoll.getDistanceSqToEntity(theTarget)
                       > theDoll.getDistanceSqToEntity(e))
                    {
                        theTarget = e;
                    }
                }
            }
            if(theTarget != null)
            {
                return true;
            }
        }
        
        return theTarget != null;
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
        if(!theDoll.isPatrolMode())
        {
            return false;
        }
        if(this.pathfinder.noPath())
        {
            return false;
        }
        if(theTarget == null)
        {
            return false;
        }
        if(!theTarget.isEntityAlive())
        {
            return false;
        }
        return true;
    }

    @Override
    public void resetTask()
    {
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

            this.pathfinder.tryMoveToEntityLiving(theTarget, this.speed);
            if(this.theDoll.getDistanceSqToEntity(theTarget) < 9f
               && this.theDoll.getEntitySenses().canSee(theTarget))
            {
                if(theTarget instanceof EntitySheep)
                {
                    EntitySheep sheep = (EntitySheep)theTarget;
                    Random rand = sheep.getRNG();
                    if(!sheep.getSheared() && !sheep.isChild())
                    {
                        sheep.setSheared(true);
                        int i = 1 + rand.nextInt(3);

                        for (int j = 0; j < i; j++)
                        {
                            EntityItem entityitem = sheep.entityDropItem(new ItemStack(Block.cloth.blockID, 1, sheep.getFleeceColor()), 1.0F);
                            entityitem.motionY += rand.nextFloat() * 0.05F;
                            entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                            entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        }
                        theWorld.playSoundAtEntity(theTarget, "mob.sheep.shear", 0.5F, (theDoll.getRNG().nextFloat() - theDoll.getRNG().nextFloat()) * 0.2F + 1.0F);
                        theDoll.chatMessage(theDoll.getDollName() + " : Wool!",2);
                        theDoll.swingItem();
                        theTarget = null;
                    }
                }
                else if(theTarget instanceof EntityChicken)
                {
                    EntityChicken chicken = (EntityChicken)theTarget;
                    Random rand = theTarget.getRNG();
                    if(!chicken.isChild())
                    {
                        theTarget.entityDropItem(new ItemStack(Item.feather,1), 0.0F);
                        theDoll.chatMessage(theDoll.getDollName() + " : Feather!",2);
                        theDoll.swingItem();
                        theTarget.attackEntityFrom(DamageSource.causeMobDamage(theDoll), 0);
                        theTarget = null;
                    }
                }
                else if(theTarget instanceof EntityMooshroom)
                {
                    EntityMooshroom mooshroom = (EntityMooshroom)theTarget;
                    Random rand = theTarget.getRNG();
                    if(!mooshroom.isChild())
                    {
                        theTarget.entityDropItem(new ItemStack(Block.mushroomRed,1), 0.0F);
                        theDoll.chatMessage(theDoll.getDollName() + " : Mushroom!",2);
                        theDoll.swingItem();
                        theTarget.attackEntityFrom(DamageSource.causeMobDamage(theDoll), 0);
                        theTarget = null;
                    }
                }
                else if(theTarget instanceof EntityIronGolem)
                {
                    EntityIronGolem golem = (EntityIronGolem)theTarget;
                    Random rand = theTarget.getRNG();

                    if(golem.getHoldRoseTick() == 0)
                    {
                        golem.setHoldingRose(true);
                    }
                    if(golem.getHoldRoseTick() != 0)
                    {
                        if(golem.getEntitySenses().canSee(theDoll) && theDoll.getRNG().nextFloat()<0.1f)
                        {
                            golem.setHoldingRose(false);
                            theTarget.entityDropItem(new ItemStack(Block.plantRed,1), 0.0F);
                            theDoll.chatMessage(theDoll.getDollName() + " : Rose!",2);
                            theTarget = null;
                        }
                    }
                }
            }
        }
    }
}
