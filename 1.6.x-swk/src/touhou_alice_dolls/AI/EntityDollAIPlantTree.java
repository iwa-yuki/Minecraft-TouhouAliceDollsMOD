package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIPlantTree extends EntityDollAIBase
{
    public EntityDollAIPlantTree(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(4);
    }

    @Override
    public boolean shouldExecute()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(theDoll.isStandbyMode())
        {
            return false;
        }
        ItemStack subItem = theDoll.getStackInSlot(0);
        if(subItem == null)
        {
            return false;
        }
        if(subItem.itemID != Block.sapling.blockID)
        {
            return false;
        }
        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);

        int targetX = dollposX;
        int targetY = dollposY;
        int targetZ = dollposZ;

        while(!theWorld.isAirBlock(targetX, targetY, targetZ))
        {
            ++targetY;
        }
        while(theWorld.isAirBlock(targetX, targetY, targetZ))
        {
            --targetY;
        }

        if(targetY<dollposY-3 || targetY >dollposY+3)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX, targetY, targetZ)
           != Block.dirt.blockID &&
           theWorld.getBlockId(targetX, targetY, targetZ)
           != Block.grass.blockID)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX+1, targetY, targetZ)
           != Block.glowStone.blockID)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX-1, targetY, targetZ)
           != Block.glowStone.blockID)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX, targetY, targetZ+1)
           != Block.glowStone.blockID)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX, targetY, targetZ-1)
           != Block.glowStone.blockID)
        {
            return false;
        }
        
        theDoll.decrStackSize(0, 1);
        theWorld.setBlock(targetX, targetY+1, targetZ, subItem.itemID, subItem.getItemDamage(), 3);

        return false;
    }
    
}
