package mods.touhou_alice_core.ai;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.entity.player.EntityPlayer;
import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

/**
 * 持ち主を追いかける
 */
public class EntityDollAIFollowOwner extends EntityDollAIBase
{
    private EntityPlayer theOwner;
    private float speed;
    private float maxDist;
    private float minDist;
    private PathNavigate pathfinder;
    private boolean avoidsWater;
    private int counter;

    public EntityDollAIFollowOwner(EntityAliceDoll doll)
    {
        super(doll);
        this.speed = 1.0F;
        this.minDist = 4.0f;
        this.maxDist = 2.5f;
        this.pathfinder = doll.getNavigator();
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityPlayer owner = this.theDoll.getOwnerEntity();

        if (owner == null)
        {
            return false;
        }
        if (!this.theDoll.isFollowMode())
        {
            return false;
        }
        if(this.theDoll.isGUIOpened())
        {
        	return false;
        }
        if (this.theDoll.getDistanceSqToEntity(owner) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        
        this.theOwner = owner;
        return true;
    }

    @Override
    public boolean continueExecuting()
    {
        if(this.pathfinder.noPath())
        {
            return false;
        }
        if(this.theDoll.getDistanceSqToEntity(this.theOwner) < (double)(this.maxDist * this.maxDist))
        {
            return false;
        }
        if(!this.theDoll.isFollowMode())
        {
            return false;
        }
        if(this.theDoll.isGUIOpened())
        {
        	return false;
        }
        return true;
    }

    @Override
    public void startExecuting()
    {
    	super.startExecuting();
    	
        this.counter = 0;
        this.avoidsWater = ((PathNavigateGround)this.theDoll.getNavigator()).func_179689_e();
        ((PathNavigateGround)this.theDoll.getNavigator()).func_179690_a(false);
    }

    @Override
    public void resetTask()
    {
        this.theOwner = null;
        this.pathfinder.clearPathEntity();
        ((PathNavigateGround)this.theDoll.getNavigator()).func_179690_a(false);
        
        super.resetTask();
    }

    @Override
    public void updateTask()
    {
        this.theDoll.getLookHelper().setLookPositionWithEntity(
            this.theOwner, 10.0F, (float)this.theDoll.getVerticalFaceSpeed());

        if (--this.counter <= 0)
        {
            this.counter = 10;

            if (!this.pathfinder.tryMoveToEntityLiving(
                    this.theOwner, this.speed))
            {
                if (this.theDoll.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                {
                    int var1 = MathHelper.floor_double(this.theOwner.posX)-2;
                    int var2 = MathHelper.floor_double(this.theOwner.posZ)-2;
                    int var3 = MathHelper.floor_double(this.theOwner.getBoundingBox().minY);

                    for (int var4 = 0; var4 <= 4; ++var4)
                    {
                        for (int var5 = 0; var5 <= 4; ++var5)
                        {
                        	if ((World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(var1 + var4, var3 - 1, var2 + var5))) && (!this.theWorld.getBlockState(new BlockPos(var1 + var4, var3, var2 + var5)).getBlock().isNormalCube()) && (!this.theWorld.getBlockState(new BlockPos(var1 + var4, var3 + 1, var2 + var5)).getBlock().isNormalCube()))
                            {
                                this.theDoll.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.theDoll.rotationYaw, this.theDoll.rotationPitch);
                                this.pathfinder.clearPathEntity();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
