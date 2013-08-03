package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAITorcher extends EntityDollAIBase
{
    public static int lightThreshold;
    
    public EntityDollAITorcher(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(0);
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
        if(subItem.itemID != Block.torchWood.blockID)
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
        ++targetY;

        if(targetY<dollposY-3 || targetY >dollposY+3)
        {
            return false;
        }

        if(!isValidLightLevel(targetX, targetY, targetZ))
        {
            return false;
        }
        
        int bid = theWorld.getBlockId(targetX, targetY-1, targetZ);
        Block b = Block.blocksList[bid];
        if(b != null && b.isOpaqueCube())
        {
            theDoll.decrStackSize(0, 1);
            theWorld.setBlock(targetX, targetY, targetZ, Block.torchWood.blockID, 5, 3);
        }

        return false;
    }
    
    protected boolean isValidLightLevel(int var1, int var2, int var3)
    {
        if (this.theWorld.getSavedLightValue(EnumSkyBlock.Sky, var1, var2, var3) > this.theDoll.getRNG().nextInt(32))
        {
            return false;
        }
        else
        {
            int var4 = this.theWorld.getBlockLightValue(var1, var2, var3);

            if (this.theWorld.isThundering())
            {
                int var5 = this.theWorld.skylightSubtracted;
                this.theWorld.skylightSubtracted = 10;
                var4 = this.theWorld.getBlockLightValue(var1, var2, var3);
                this.theWorld.skylightSubtracted = var5;
            }

            return var4 <= lightThreshold;
        }
    }
}
