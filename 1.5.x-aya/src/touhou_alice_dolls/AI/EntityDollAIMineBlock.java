package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_dolls.EntityAliceDoll;
import mods.touhou_alice_dolls.DollRegistry;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIMineBlock extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private int counter;
    private float speed;

    public static double mineSpeed;

    public EntityDollAIMineBlock(EntityAliceDoll doll)
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
        if(this.theDoll.isStandbyMode() || this.theDoll.isRideonMode())
        {
            return false;
        }
        if(theDoll.getDollID() != DollRegistry.getDollID("Horai")
           && theDoll.getDollID() != DollRegistry.getDollID("Ooedo"))
        {
            return false;
        }
        if(!this.theDoll.isTargetLockon)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean continueExecuting()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        if(counter < 0 && this.pathfinder.noPath())
        {
            return false;
        }
        if(this.theDoll.isStandbyMode() || this.theDoll.isRideonMode())
        {
            return false;
        }
        if(!this.theDoll.isTargetLockon)
        {
            return false;
        }
        if(Block.blocksList[theWorld.getBlockId(
                   theDoll.targetX, theDoll.targetY, theDoll.targetZ)] == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting()
    {
        Block b = Block.blocksList[theWorld.getBlockId(
                theDoll.targetX, theDoll.targetY, theDoll.targetZ)];
        if(b == null)
        {
            return;
        }
        
        int blockStrength = MathHelper.floor_double(20.0*b.getBlockHardness(theWorld, theDoll.targetX, theDoll.targetY, theDoll.targetZ)/mineSpeed);
        this.counter = blockStrength < 0 ? 0 : blockStrength;
        this.pathfinder.tryMoveToXYZ(
            (double)(theDoll.targetX) + 0.5D,
            (double)(theDoll.targetY) + 0.5D,
            (double)(theDoll.targetZ) + 0.5D,
            this.speed);
    }

    @Override
    public void resetTask()
    {
        this.pathfinder.clearPathEntity();
    }

    @Override
    public void updateTask()
    {
        if(counter >= 0)
        {
            this.theDoll.getLookHelper().setLookPosition(
                (double)(theDoll.targetX) + 0.5D,
                (double)(theDoll.targetY) + 0.5D,
                (double)(theDoll.targetZ) + 0.5D,
                20.0F, (float)this.theDoll.getVerticalFaceSpeed());
        }
        
        if (this.counter == 0)
        {
            Block b = Block.blocksList[theWorld.getBlockId(
                    theDoll.targetX, theDoll.targetY, theDoll.targetZ)];
            if(b != null)
            {
                theWorld.func_94578_a(theDoll.targetX, theDoll.targetY, theDoll.targetZ, true);
            }
        }
        if(this.counter > 0 && this.counter%4 == 0)
        {
            Block b = Block.blocksList[theWorld.getBlockId(
                    theDoll.targetX, theDoll.targetY, theDoll.targetZ)];
            if(b != null)
            {
                StepSound stepsound = b.stepSound;
                theWorld.playSoundEffect(theDoll.targetX+0.5f, theDoll.targetY+0.5f, theDoll.targetZ+0.5f, stepsound.getBreakSound(), (stepsound.getVolume() + 1.0f) / 8f, stepsound.getPitch() * 0.5f);
            }
        }
        counter--;
    }
}
