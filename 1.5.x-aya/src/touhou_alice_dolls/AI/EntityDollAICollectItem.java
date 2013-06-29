package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.DamageSource;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import mods.touhou_alice_dolls.EntityAliceDoll;
import mods.touhou_alice_dolls.DollRegistry;

import java.util.*;
import java.util.regex.*;

public class EntityDollAICollectItem extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private EntityItem theItem;
    private float speed;
    private int counter;
    private int catchCounter;
    private boolean avoidsWater;

    public static double searchRange;
    public static double searchHeight;
    public static float canCollectRange;

    public EntityDollAICollectItem(EntityAliceDoll doll)
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
        if(theDoll.getDollID() != DollRegistry.getDollID("Russia"))
        {
            return false;
        }
        // 蒐集対象探索
        List<EntityItem> itemList = (List<EntityItem>)(theWorld.getEntitiesWithinAABB(EntityItem.class, theDoll.boundingBox.expand(searchRange, searchHeight, searchRange)));

        theItem = null;
        for(EntityItem e : itemList)
        {
            if(theItem == null)
            {
                theItem = e;
            }
            else
            {
                if(theDoll.getDistanceSqToEntity(theItem)>theDoll.getDistanceSqToEntity(e))
                {
                    theItem = e;
                }
            }
        }
        if(theItem ==null)
        {
            return false;
        }

        // 出力文字列の作成
        StringBuffer msg=new StringBuffer(theDoll.getDollName() + " : ");
        String itemname = theItem.getEntityItem().getDisplayName();
        if(itemname == null)
        {
            itemname = "unknown";
        }
        msg.append(itemname);
        theDoll.chatMessage(msg.toString(), 3);
        
        return true;
    }

    @Override
    public void startExecuting()
    {
        counter = 0;
        catchCounter = 0;
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
        // if(this.pathfinder.noPath())
        // {
        //     return false;
        // }
        if(this.theItem == null)
        {
            return false;
        }
        if(!this.theItem.isEntityAlive())
        {
            return false;
        }
        return true;
    }

    @Override
    public void resetTask()
    {
        this.theItem = null;
        this.pathfinder.clearPathEntity();
        this.theDoll.getNavigator().setAvoidsWater(this.avoidsWater);
    }

    @Override
    public void updateTask()
    {
        if(!this.pathfinder.noPath())
        {
            this.theDoll.getLookHelper().setLookPositionWithEntity(
                this.theItem, 10.0F, (float)this.theDoll.getVerticalFaceSpeed());
            catchCounter = catchCounter>0 ? (catchCounter-1) : 0;
        }
        else
        {
            catchCounter++;
        }
        if(counter == 0)
        {
            this.pathfinder.tryMoveToXYZ(theItem.posX, theItem.posY, theItem.posZ, this.speed);
        }
        // アイテム回収
        if(theDoll.getDistanceToEntity(theItem) < canCollectRange || catchCounter > 60)
        {
            if(theDoll.addItemStackToInventory(theItem.getEntityItem()))
            {
                theWorld.playSoundAtEntity(theDoll, "random.pop", 0.2F, ((theDoll.getRNG().nextFloat() - theDoll.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                if(theItem.getEntityItem().stackSize <= 0)
                {
                    theItem.setDead();
                }
            }
            else
            {
                    theDoll.chatMessage(theDoll.getDollName() + " : Can't pick up!", 3);
                    theItem = null;
                    // theItem.motionX += 0.1D - 0.2D*theDoll.getRNG().nextDouble();
                    // theItem.motionY += 0.3D + 0.1D*theDoll.getRNG().nextDouble();
                    // theItem.motionZ += 0.1D - 0.2D*theDoll.getRNG().nextDouble();
            }
        }
        counter = (counter + 1)%20;
        // System.out.println(catchCounter);
    }
}
