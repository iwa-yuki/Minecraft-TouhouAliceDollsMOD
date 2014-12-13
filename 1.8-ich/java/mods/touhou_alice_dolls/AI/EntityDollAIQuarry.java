package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIQuarry extends EntityDollAIBase
{
    public static String levelingBlockRegex;
    public static int mineRange;
    public static double mineSpeed;
    public static int levelingRange = 8;

    private PathNavigate pathfinder;
    private int counter;
    private float speed;
    private boolean anchorLockon;
    private int anchorX;
    private int anchorY;
    private int anchorZ;
    private boolean isMining;
    private int mineX;
    private int mineY;
    private int mineZ;
    private int levelingXmax;
    private int levelingXmin;
    private int levelingZmax;
    private int levelingZmin;
    Pattern targetPattern;
    Matcher targetMatcher;

    public EntityDollAIQuarry(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(7);
        this.pathfinder = doll.getNavigator();
        targetPattern = Pattern.compile(levelingBlockRegex);
        counter = 0;
        speed = 0.01F;
        anchorLockon = false;
        isMining = false;
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
        ItemStack subItem = theDoll.getStackInSlot(0);
        if(subItem == null)
        {
            return false;
        }
        if(subItem.getItem() != Items.cake)
        {
            return false;
        }
        if(++counter > 64)
        {
            counter = 0;
        }

        // アンカー探索
        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);
        anchorLockon = false;
        for(int jj=0;jj<=1;++jj)
        {
            if(counter == 0 && jj == 1)
            {
                continue;
            }
            int j = jj==0?counter:-counter;
            for(int i=-16;i<=16;++i)
            {
                for(int k=-16;k<=16;++k)
                {
                    // ダイヤブロックの上にレッドストーントーチがあるとアンカーとして認識する
                    if(theWorld.getBlockState(new BlockPos(dollposX+i, dollposY+j, dollposZ+k)).getBlock()
                       == Blocks.diamond_block)
                    {
                        if(theWorld.getBlockState(new BlockPos(dollposX+i, dollposY+j+1, dollposZ+k)).getBlock()
                           == Blocks.redstone_torch)
                        {
                            anchorLockon = true;
                            anchorX = dollposX+i;
                            anchorY = dollposY+j;
                            anchorZ = dollposZ+k;
                        }
                    }
                }
            }
        }
        if(!anchorLockon)
        {
            return false;
        }

        // 掘削範囲制御
        levelingXmax = levelingRange;
        for(int lx=1;lx<=levelingRange;++lx)
        {
            if(theWorld.getBlockState(new BlockPos(anchorX+lx, anchorY+1, anchorZ)).getBlock()
               == Blocks.redstone_torch)
            {
                levelingXmax = lx-1;
                break;
            }
        }
        levelingXmin = levelingRange;
        for(int lx=1;lx<=levelingRange;++lx)
        {
            if(theWorld.getBlockState(new BlockPos(anchorX-lx, anchorY+1, anchorZ)).getBlock()
               == Blocks.redstone_torch)
            {
                levelingXmin = lx-1;
                break;
            }
        }
        levelingZmax = levelingRange;
        for(int lz=1;lz<=levelingRange;++lz)
        {
            if(theWorld.getBlockState(new BlockPos(anchorX, anchorY+1, anchorZ+lz)).getBlock()
               == Blocks.redstone_torch)
            {
                levelingZmax = lz-1;
                break;
            }
        }
        levelingZmin = levelingRange;
        for(int lz=1;lz<=levelingRange;++lz)
        {
            if(theWorld.getBlockState(new BlockPos(anchorX, anchorY+1, anchorZ-lz)).getBlock()
               == Blocks.redstone_torch)
            {
                levelingZmin = lz-1;
                break;
            }
        }
        //チャット出力
        StringBuffer msg = new StringBuffer(theDoll.getName() + " : ");
        msg.append("Anchor lockon!");
        theDoll.chatMessage(msg.toString(),2);
        msg = new StringBuffer(theDoll.getName() + " : ");
        msg.append("Cuarrying mode(");
        msg.append(levelingXmax+levelingXmin+1);
        msg.append(",");
        msg.append(anchorY+1);
        msg.append(",");
        msg.append(levelingZmax+levelingZmin+1);
        msg.append(")");
        theDoll.chatMessage(msg.toString(),2);

        return true;
    }

    public void startExecuting()
    {
    	super.startExecuting();
    	
        counter = 0;
        isMining = false;
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
        ItemStack subItem = theDoll.getStackInSlot(0);
        if(subItem == null)
        {
            return false;
        }
        if(subItem.getItem() != Items.cake)
        {
            return false;
        }
        if(theWorld.getBlockState(new BlockPos(anchorX, anchorY, anchorZ)).getBlock()
           != Blocks.diamond_block)
        {
            return false;
        }
        if(theWorld.getBlockState(new BlockPos(anchorX, anchorY+1, anchorZ)).getBlock()
           != Blocks.redstone_torch)
        {
            return false;
        }

        return true;
    }
    
    @Override
    public void resetTask()
    {
        counter = 0;
        this.pathfinder.clearPathEntity();
        
        super.resetTask();
    }

    public void updateTask()
    {
        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);

        if(isMining)
        {
            // 採掘を行う
            if(counter >= 0)
            {
                this.theDoll.getLookHelper().setLookPosition(
                    (double)(mineX) + 0.5D,
                    (double)(mineY) + 0.5D,
                    (double)(mineZ) + 0.5D,
                    20.0F, (float)this.theDoll.getVerticalFaceSpeed());
            }
        
            if (this.counter == 0)
            {
                Block b = theWorld.getBlockState(new BlockPos(
                        mineX, mineY, mineZ)).getBlock();
                if(b != null)
                {
                    theWorld.destroyBlock(new BlockPos(mineX, mineY, mineZ), true);
                }
                isMining = false;
            }
            if(this.counter > 0 && this.counter%4 == 0)
            {
                Block b = theWorld.getBlockState(new BlockPos(
                        mineX, mineY, mineZ)).getBlock();
                if(b != null)
                {
                    SoundType stepsound = b.stepSound;
                    theDoll.playSound(stepsound.getBreakSound(), (stepsound.getVolume() + 1.0f) / 8f, stepsound.getFrequency() * 0.5f);
                }
            }
            counter--;
        }
        else
        {
            // 採掘対象を決める
            for(int j=mineRange; j>=-mineRange; --j)
            {
                if(dollposY+j<=anchorY)
                {
                    continue;
                }
                for(int i=-mineRange; i<=mineRange; ++i)
                {
                    if(dollposX+i<anchorX-levelingXmin || dollposX+i>anchorX+levelingXmax)
                    {
                        continue;
                    }
                    for(int k=-mineRange; k<=mineRange; ++k)
                    {
                        if(dollposZ+k<anchorZ-levelingZmin || dollposZ+k>anchorZ+levelingZmax)
                        {
                            continue;
                        }
                        int distanceToBlock = Math.abs(i)+Math.abs(j)+Math.abs(k);
                        if(distanceToBlock > mineRange)
                        {
                            continue;
                        }
                        if(this.canDigBlock(dollposX+i, dollposY+j, dollposZ+k))
                        {
                            mineX = dollposX+i;
                            mineY = dollposY+j;
                            mineZ = dollposZ+k;
                            isMining = true;
                            break;
                        }
                    }
                    if(isMining)
                    {
                        break;
                    }
                }
                if(isMining)
                {
                    break;
                }
            }
            if(!isMining)
            {
                // 手の届かない場所にしかブロックがない場合
                int j=dollposY>(anchorY+1) ? dollposY : (anchorY+1);
                for(int i=-levelingXmin; i<=levelingXmax; ++i)
                {
                    for(int k=-levelingZmin; k<=levelingZmax; ++k)
                    {
                        if(this.canDigBlock(anchorX+i, j, anchorZ+k))
                        {
                            mineX = anchorX+i;
                            mineY = j;
                            mineZ = anchorZ+k;
                            isMining = true;
                            break;
                        }
                    }
                    if(isMining)
                    {
                        break;
                    }
                }
            }
            if(!isMining)
            {
                // 採掘対象が存在しない場合はアンカーを破壊して終了
                mineX = anchorX;
                mineY = anchorY+1;
                mineZ = anchorZ;
                isMining = true;

                StringBuffer msg = new StringBuffer(theDoll.getName() + " : ");
                msg.append("Quarrying finished.");
                theDoll.chatMessage(msg.toString(),2);
            }
            if(isMining)
            {
                Block b = theWorld.getBlockState(new BlockPos(
                        mineX, mineY, mineZ)).getBlock();
                int blockStrength = -1;
                if(b != null)
                {
                    blockStrength = MathHelper.floor_double(
                        20.0*b.getBlockHardness(
                            theWorld, new BlockPos(mineX, mineY, mineZ))/mineSpeed);
                }
                
                this.counter = blockStrength < 0 ? 0 : blockStrength;
                this.pathfinder.tryMoveToXYZ(
                    (double)(mineX) + 0.5D,
                    (double)(mineY) + 0.5D,
                    (double)(mineZ) + 0.5D,
                    this.speed);
            }
        }
    }
    
    // 採掘可能かどうか判定する
	private boolean canDigBlock(int i, int j, int k)
	{
		if(theWorld.isAirBlock(new BlockPos(i, j, k)))
		{
			return false;
		}
		
		Block b = theWorld.getBlockState(new BlockPos(i, j, k)).getBlock();
		if(b==null)
        {
            return false;
        }
        else
		{
			if(b.getBlockHardness(theWorld, new BlockPos(i, j, k)) < 0f || b.getBlockHardness(theWorld, new BlockPos(i, j, k)) > Blocks.obsidian.getBlockHardness(theWorld, new BlockPos(0, 0, 0)))
			{
				return false;
			}
			if(theWorld.getBlockState(new BlockPos(i, j + 1, k)).getBlock() == Blocks.sand || theWorld.getBlockState(new BlockPos(i, j + 1, k)).getBlock() == Blocks.gravel)
			{
				return false;
			}
            String name = getBlockName(b);
            targetMatcher = targetPattern.matcher(name);
            if(!targetMatcher.find())
            {
                return false;
            }
		}
		
		int u=-1;
		while(theWorld.isAirBlock(new BlockPos(i, j+u, k)))
		{
			if(j+u<=0)
			{
				return false;
			}
			Block bu = theWorld.getBlockState(new BlockPos(i, j+u-1, k)).getBlock();
			if(bu.getMaterial() == Material.lava)
			{
				return false;
			}
			--u;
		}
		
		for(int ii=-1;ii<=1;++ii)
		{
			for(int jj=-1;jj<=1;++jj)
			{
				for(int kk=-1;kk<=1;++kk)
				{
					if((ii>0?ii:-ii)+(jj>0?jj:-jj)+(kk>0?kk:-kk)<=1)
					{
						Block bu = theWorld.getBlockState(new BlockPos(i+ii, j+jj, k+kk)).getBlock();
						if(bu.getMaterial() == Material.lava)
						{
							return false;
						}
						if(bu.getMaterial() == Material.water)
						{
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}

    private String getBlockName(Block b)
    {
        if(b == null)
        {
            return "";
        }
    
        String blockName = b.getUnlocalizedName();
        if(blockName == null)
        {
            blockName = String.format("Block%d", Block.getIdFromBlock(b));
        }
        else
        {
            int dot = blockName.indexOf(".");
            if(dot != -1)
            {
                blockName = blockName.substring(dot+1);
            }
        }
        return blockName;
    }
}
