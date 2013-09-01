package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIMineBlock extends EntityDollAIBase
{
    private PathNavigate pathfinder;
    private int counter;
    private float speed;
    
    private int targetX;
    private int targetY;
    private int targetZ;

    private int mineX;
    private int mineY;
    private int mineZ;

    public static String targetBlockRegex;
    public static int mineRange;
    public static double mineSpeed;

    public EntityDollAIMineBlock(EntityAliceDoll doll)
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
        if(this.theDoll.isStandbyMode() || this.theDoll.isRideonMode())
        {
            return false;
        }
        if(--counter > 0)
        {
            return false;
        }
        counter = 20;

        //採掘対象探索
        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(
            theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);
        Pattern targetPattern = Pattern.compile(targetBlockRegex);
        Matcher targetMatcher;

        targetX = mineRange;
        targetY = mineRange;
        targetZ = mineRange;
        boolean isTarget = false;

        // 周囲のブロック検索
        for(int dy=-mineRange; dy<=mineRange; ++dy)
        {
            for(int dx=-mineRange; dx<=mineRange; ++dx)
            {
                for(int dz=-mineRange; dz<=mineRange; ++dz)
                {
                    int distanceToBlock =
                        Math.abs(dx)+Math.abs(dy)+Math.abs(dz);
                    if(distanceToBlock > mineRange)
                    {
                        // 探索範囲外は除外
                        continue;
                    }
                    if(Math.abs(targetX)+Math.abs(targetY)+Math.abs(targetZ)
                       < distanceToBlock)
                    {
                        // 発見済みの採掘対象より遠いならば除外
                        continue;
                    }

                    Block b = Block.blocksList[
                        theWorld.getBlockId(dollposX+dx,
                                            dollposY+dy,
                                            dollposZ+dz)];
                    if(b==null)
                    {
                        continue;
                    }
                    String blockName = getBlockName(b);
                    
                    //採掘対象かどうか
                    targetMatcher = targetPattern.matcher(blockName);
                    if(targetMatcher.find())
                    {
                        if(canDigBlock(dollposX+dx, dollposY+dy, dollposZ+dz))
                        {
                            targetX = dx;
                            targetY = dy;
                            targetZ = dz;
                            isTarget = true;
                        }
                    }
                }
            }
        }
        if(!isTarget)
        {
            return false;
        }
        
        //掘削対象ブロックの検索
        mineX = 0;
        mineY = 0;
        mineZ = 0;
                
        boolean isMining = false;

        for(int r=1; r<=mineRange; ++r)
        {
            int ii = 0;
            int jj = 0;
            int kk = 0;
            for(int k=-1; k<=1; ++k)
            {
                for(int j=1; j>=-1; --j)
                {
                    for(int i=-1; i<=1; ++i)
                    {
                        //隣接するブロックを再帰探索する
                        if(Math.abs(i)+Math.abs(j)+Math.abs(k) != 1)
                        {
                            continue;
                        }
                        if(Math.abs(targetX-mineX)+Math.abs(targetY-mineY)+Math.abs(targetZ-mineZ)
                           > Math.abs(targetX-(mineX+i))+Math.abs(targetY-(mineY+j))+Math.abs(targetZ-(mineZ+k)))
                        {
                            if(canDigBlock(dollposX+mineX+i, dollposY+mineY+j, dollposZ+mineZ+k))
                            {
                                //ターゲットにもっとも近い掘削可能ブロック
                                ii= i;
                                jj= j;
                                kk= k;
                                isMining = true;
                            }
                            else if(theWorld.isAirBlock(dollposX+mineX+i, dollposY+mineY+j, dollposZ+mineZ+k))
                            {
                                //ターゲットにもっとも近い移動可能ブロック
                                ii=i;
                                jj=j;
                                kk=k;
                                isMining = false;
                            }
                        }
                    }
                }
            }
            mineX += ii;
            mineY += jj;
            mineZ += kk;
            if(isMining)
            {
                break;
            }
        }
        if(!isMining)
        {
            return false;
        }
        //相対座標から絶対座標へ
        targetX += dollposX;
        targetY += dollposY;
        targetZ += dollposZ;
        mineX += dollposX;
        mineY += dollposY;
        mineZ += dollposZ;
        
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
        if(this.theWorld.isAirBlock(
               mineX, mineY, mineZ))
        {
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting()
    {
        Block b = Block.blocksList[theWorld.getBlockId(
                mineX, mineY, mineZ)];
        if(b == null)
        {
            return;
        }
        
        int blockStrength = MathHelper.floor_double(
            20.0*b.getBlockHardness(
                theWorld, mineX, mineY, mineZ)/mineSpeed);
        this.counter = blockStrength < 0 ? 0 : blockStrength;
        this.pathfinder.tryMoveToXYZ(
            (double)(mineX) + 0.5D,
            (double)(mineY) + 0.5D,
            (double)(mineZ) + 0.5D,
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
                (double)(mineX) + 0.5D,
                (double)(mineY) + 0.5D,
                (double)(mineZ) + 0.5D,
                20.0F, (float)this.theDoll.getVerticalFaceSpeed());
        }
        
        if (this.counter == 0)
        {
            Block b = Block.blocksList[theWorld.getBlockId(
                    mineX, mineY, mineZ)];
            if(b != null)
            {
                theWorld.destroyBlock(mineX, mineY, mineZ, true);
            }
        }
        if(this.counter > 0 && this.counter%4 == 0)
        {
            Block b = Block.blocksList[theWorld.getBlockId(
                    mineX, mineY, mineZ)];
            if(b != null)
            {
                StepSound stepsound = b.stepSound;
                theDoll.playSound(stepsound.getBreakSound(), (stepsound.getVolume() + 1.0f) / 8f, stepsound.getPitch() * 0.5f);
            }
        }
        counter--;
    }

    /**
     * ブロックの名前を取得
     */
    private String getBlockName(Block b)
    {
        if(b == null)
        {
            return "";
        }
    
        String blockName = b.getUnlocalizedName();
        if(blockName == null)
        {
            blockName = String.format("Block%d", b.blockID);
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

    /**
     * 採掘可能かどうか判定する
     */
	private boolean canDigBlock(int i, int j, int k)
	{
        //空気は掘れない
		if(theWorld.isAirBlock(i, j, k))
		{
			return false;
		}
		
		Block b = Block.blocksList[theWorld.getBlockId(i, j, k)];
		if(b!=null)
		{
            //黒曜石より硬いものは掘れない
			if(b.getBlockHardness(theWorld, i, j, k) < 0f || b.getBlockHardness(theWorld, i, j, k) > Block.obsidian.getBlockHardness(theWorld, 0, 0, 0))
			{
				return false;
			}
            //上に砂や砂利があると掘れない(生き埋め防止)
			if(theWorld.getBlockId(i, j + 1, k) == Block.sand.blockID || theWorld.getBlockId(i, j + 1, k) == Block.gravel.blockID)
			{
				return false;
			}
		}
		
		int u=-1;
		while(theWorld.isAirBlock(i, j+u, k))
		{
            //掘ったら奈落
			if(j+u<=0)
			{
				return false;
			}
            //掘ったらマグマダイブ
			if(theWorld.getBlockMaterial(i, j+u-1, k)==Material.lava)
			{
				return false;
			}
			--u;
		}

        //水や溶岩に隣接していると掘れない(だばぁ防止)
		for(int ii=-1;ii<=1;++ii)
		{
			for(int jj=-1;jj<=1;++jj)
			{
				for(int kk=-1;kk<=1;++kk)
				{
					if((ii>0?ii:-ii)+(jj>0?jj:-jj)+(kk>0?kk:-kk)<=1)
					{
						if(theWorld.getBlockMaterial(i+ii, j+jj, k+kk)==Material.lava)
						{
							return false;
						}
						if(theWorld.getBlockMaterial(i+ii, j+jj, k+kk)==Material.water)
						{
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
}
