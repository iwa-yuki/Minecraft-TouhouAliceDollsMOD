package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIExplosion extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private EntityLiving theTarget;
    private float speed;
    private int counter;
    private int fuse;
    private boolean avoidsWater;
    public static double searchRange;
    public static double searchHeight;
    public static String targetEntityRegex;
    public static float explodeStrength;
    public static boolean mobGriefing;
    
    public EntityDollAIExplosion(EntityAliceDoll doll)
    {
        super(doll);
        this.speed = 1.0F;
        this.pathfinder = doll.getNavigator();
        this.setMutexBits(3);
    }

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
        ItemStack subItem = theDoll.getStackInSlot(0);
        if(subItem != null && subItem.getItem() == Items.cake)
        {
            return false;
        }

        //攻撃対象探索
        List<EntityLiving> targetList =
            (List<EntityLiving>)(theWorld.getEntitiesWithinAABB(EntityLiving.class, theDoll.boundingBox.expand(searchRange, searchHeight, searchRange)));
        Pattern targetPattern = Pattern.compile(targetEntityRegex);
        Matcher targetMatcher;
        theTarget = null;

        for(EntityLiving e : targetList)
        {
            String name = EntityList.getEntityString(e);
            if(name == null)
            {
                continue;
            }

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

        // 出力文字列の作成
        StringBuffer msg=new StringBuffer(theDoll.getDollName() + " : ");
        
        if(theTarget == null)
        {
            msg.append("No target");
            theDoll.chatMessage(msg.toString(),2);
            return false;
        }
        else
        {
            msg.append(EntityList.getEntityString(theTarget));
            msg.append("(");
            msg.append(theTarget.hashCode());
            msg.append(") ");                    
            theDoll.chatMessage(msg.toString(),1);
        }
        return true;
    }

    public void startExecuting()
    {
        counter = 0;
        fuse = 0;
        this.avoidsWater = this.theDoll.getNavigator().getAvoidsWater();
        this.theDoll.getNavigator().setAvoidsWater(false);
    }
    
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
        if(fuse == 0 && this.pathfinder.noPath())
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
        if(counter == 0)
        {
            this.pathfinder.tryMoveToEntityLiving(this.theTarget, this.speed);
        }
        if(this.theDoll.getDistanceSqToEntity(this.theTarget) < 5f)
        {
            if(fuse == 1)
            {
                theDoll.playSound("random.fuse", 1.0F, 0.5F);
            }
            if(fuse >= 30)
            {
                if (!theWorld.isRemote)
                {
                    boolean var2 = theWorld.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    float strength = explodeStrength;
                    ItemStack subItem = theDoll.getStackInSlot(0);
                    if(subItem != null && subItem.getItem() == Item.getItemFromBlock(Blocks.tnt))
                    {
                        strength = MathHelper.sqrt_float(
                            strength*strength + 16.0F*(subItem.stackSize));
                        theDoll.decrStackSize(0, subItem.stackSize);
                    }
                    Explosion exp = theWorld.createExplosion(theDoll, theDoll.posX, theDoll.posY, theDoll.posZ, strength, var2 & mobGriefing);

                    theDoll.onDeath(DamageSource.setExplosionSource(exp));
                    theDoll.setDead();
                }
            }
            ++fuse;
        }
        else
        {
            fuse = fuse > 0 ? fuse - 1 : 0;
        }
        counter = (counter + 1)%20;
    }
}
