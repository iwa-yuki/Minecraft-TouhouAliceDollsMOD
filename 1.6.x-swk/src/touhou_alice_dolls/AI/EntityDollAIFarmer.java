package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.material.Material;
import net.minecraft.block.StepSound;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import mods.touhou_alice_dolls.EntityAliceDoll;
import mods.touhou_alice_dolls.THShotLibWrapper;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIFarmer extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private int counter;
    private float speed; 
    private int targetX;
    private int targetY;
    private int targetZ;
    private int targetID;

    public static int farmRange;
    
    public EntityDollAIFarmer(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(3);
        this.pathfinder = theDoll.getNavigator();
        counter = 0;
        speed = 1.0F;
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
        if(!theDoll.isPatrolMode())
        {
            return false;
        }
        if(--counter > 0)
        {
            return false;
        }
        counter = 20;

        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);

        // 土を耕す
        for(int j=0; j<farmRange; ++j)
        {
            int id = theWorld.getBlockId(dollposX, dollposY-j, dollposZ);
            if(id == Block.dirt.blockID || id == Block.grass.blockID)
            {
                int cc = 0;
                cc += theWorld.getBlockId(dollposX+1, dollposY-j, dollposZ)
                    == Block.tilledField.blockID ? 1 : 0;
                cc += theWorld.getBlockId(dollposX-1, dollposY-j, dollposZ)
                    == Block.tilledField.blockID ? 1 : 0;
                cc += theWorld.getBlockId(dollposX, dollposY-j, dollposZ+1)
                    == Block.tilledField.blockID ? 1 : 0;
                cc += theWorld.getBlockId(dollposX, dollposY-j, dollposZ-1)
                    == Block.tilledField.blockID ? 1 : 0;
                //隣接ブロックのうち2個が耕地だったら耕す
                if(cc >= 2)
                {
                    Block block = Block.tilledField;
                    theWorld.playSoundEffect((double)((float)dollposX + 0.5F), (double)((float)(dollposY-j) + 0.5F), (double)((float)dollposZ + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                    theDoll.swingItem();
                    if(!theWorld.isRemote)
                    {
                        theWorld.setBlock(dollposX, dollposY-j, dollposZ,
                                          Block.tilledField.blockID, 0, 3);
                    }
                    break;
                }
            }
        }
        
        //種をまく
        for(int j=0; j<farmRange; ++j)
        {
            int id = theWorld.getBlockId(dollposX, dollposY-j, dollposZ);
            if(id == Block.tilledField.blockID &&
               theWorld.isAirBlock(dollposX, dollposY-j+1, dollposZ))
            {
                // カボチャ、スイカの苗に隣接する場合はまかない
                int cc = 0;
                cc += Block.blocksList[theWorld.getBlockId(dollposX+1, dollposY-j+1, dollposZ)] instanceof BlockStem ? 1 : 0;
                cc += Block.blocksList[theWorld.getBlockId(dollposX-1, dollposY-j+1, dollposZ)] instanceof BlockStem ? 1 : 0;
                cc += Block.blocksList[theWorld.getBlockId(dollposX, dollposY-j+1, dollposZ+1)] instanceof BlockStem ? 1 : 0;
                cc += Block.blocksList[theWorld.getBlockId(dollposX, dollposY-j+1, dollposZ-1)] instanceof BlockStem ? 1 : 0;
                if(cc > 0)
                {
                    break;
                }

                for(int slot = 0; slot<theDoll.getSizeInventory();++slot)
                {
                    ItemStack itemstack = theDoll.getStackInSlot(slot);
                    if(itemstack != null)
                    {
                        Item item = itemstack.getItem();
                        if(item instanceof IPlantable)
                        {
                            IPlantable seed = (IPlantable)item;
                            if(seed.getPlantType(
                                   theWorld, dollposX, dollposY-j+1, dollposZ)
                               != EnumPlantType.Crop)
                            {
                                continue;
                            }
                            int bid = seed.getPlantID(
                                theWorld, dollposX, dollposY-j+1, dollposZ);
                            theDoll.decrStackSize(slot, 1);
                            theDoll.swingItem();
                            if(!theWorld.isRemote)
                            {
                                theWorld.setBlock(
                                    dollposX, dollposY-j+1, dollposZ,
                                    bid, 0, 3);
                            }
                            break;
                        }
                    }
                }

            }
        }
        
        targetX = farmRange;
        targetY = farmRange;
        targetZ = farmRange;
        targetID = -1;

        for(int dy=-farmRange; dy<=farmRange; ++dy)
        {
            for(int dx=-farmRange; dx<=farmRange; ++dx)
            {
                for(int dz=-farmRange; dz<=farmRange; ++dz)
                {
                    int distanceToBlock
                        = Math.abs(dx)+Math.abs(dy)+Math.abs(dz);
                    if(distanceToBlock > farmRange)
                    {
                        continue;
                    }
                    if(Math.abs(targetX)+Math.abs(targetY)+Math.abs(targetZ)
                       < distanceToBlock)
                    {
                        continue;
                    }
                    
                    int bid = theWorld.getBlockId(
                        dollposX+dx, dollposY+dy, dollposZ+dz);
                    int bmeta = theWorld.getBlockMetadata(
                        dollposX+dx, dollposY+dy, dollposZ+dz);
                    // 収穫可能か確認
                    if(Block.blocksList[bid] instanceof BlockCrops)
                    {
                        // 最終段階まで成長していればターゲットとする
                        if(bmeta == 7)
                        {
                            targetX = dx;
                            targetY = dy;
                            targetZ = dz;
                            targetID = bid;
                        }
                    }
                    else if(Block.blocksList[bid] instanceof BlockPumpkin ||
                        Block.blocksList[bid] instanceof BlockMelon)
                    {
                        // 苗に隣接していればターゲットとする
                        int cc = 0;
                        cc += Block.blocksList[theWorld.getBlockId(dollposX+dx+1, dollposY+dy, dollposZ+dz)] instanceof BlockStem ? 1 : 0;
                        cc += Block.blocksList[theWorld.getBlockId(dollposX+dx-1, dollposY+dy, dollposZ+dz)] instanceof BlockStem ? 1 : 0;
                        cc += Block.blocksList[theWorld.getBlockId(dollposX+dx, dollposY+dy, dollposZ+dz+1)] instanceof BlockStem ? 1 : 0;
                        cc += Block.blocksList[theWorld.getBlockId(dollposX+dx, dollposY+dy, dollposZ+dz-1)] instanceof BlockStem ? 1 : 0;
                        if(cc > 0)
                        {
                            targetX = dx;
                            targetY = dy;
                            targetZ = dz;
                            targetID = bid;
                        }
                    }
                }
            }
        }
        if(targetID == -1)
        {
            return false;
        }
        
        targetX += dollposX;
        targetY += dollposY;
        targetZ += dollposZ;
        
        return true;
    }
    
    public void startExecuting()
    {
        counter = 0;
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
        if(Math.abs(MathHelper.floor_double(theDoll.posX-targetX))
           + Math.abs(MathHelper.floor_double(theDoll.posY-targetY))
           + Math.abs(MathHelper.floor_double(theDoll.posZ-targetZ)) > farmRange)
        {
            return false;
        }
        if(targetID == -1)
        {
            return false;
        }
        if(theWorld.getBlockId(targetX, targetY, targetZ) != targetID)
        {
            return false;
        }
        return true;
    }
    
    @Override
    public void resetTask()
    {
        this.pathfinder.clearPathEntity();
        counter = 0;
    }

    @Override
    public void updateTask()
    {
        if(counter == 20)
        {
            theWorld.destroyBlock(targetX, targetY, targetZ, true);
            theDoll.swingItem();
            int seedID = Block.blocksList[targetID].idDropped(
                0, theDoll.getRNG(), 0);
            if(Item.itemsList[seedID] instanceof IPlantable)
            {
                for(int slot = 0; slot<theDoll.getSizeInventory();++slot)
                {
                    ItemStack itemstack = theDoll.getStackInSlot(slot);
                    if(itemstack != null)
                    {
                        if(itemstack.itemID == seedID)
                        {
                            theDoll.decrStackSize(slot, 1);
                            if(!theWorld.isRemote)
                            {
                                theWorld.setBlock(targetX, targetY, targetZ,
                                                  targetID, 0, 3);
                            }
                            break;
                        }
                    }
                }
            }
            
            targetID = -1;
        }
        else if(counter == 0)
        {
            this.pathfinder.tryMoveToXYZ(
                targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D, this.speed);
        }
        this.theDoll.getLookHelper().setLookPosition(
            targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D,
            10.0F, (float)this.theDoll.getVerticalFaceSpeed());

        counter++;
    }
}
