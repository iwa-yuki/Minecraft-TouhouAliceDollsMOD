package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAISearchBlock extends EntityDollAIBase
{
    private int counter;

    public static String targetBlockRegex;
    public static int mineRange;

    public EntityDollAISearchBlock(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(8);
    }

    @Override
    public boolean shouldExecute()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        return true;
    }

    public void startExecuting()
    {
        counter = 0;
    }
    
    public boolean continueExecuting()
    {
        return theDoll.isEnable();
    }
    
    @Override
    public void resetTask()
    {
        theDoll.isTargetLockon = false;
    }

    public void updateTask()
    {        
        if(counter == 0)
        {
            //採掘対象探索
            int dollposX = MathHelper.floor_double(theDoll.posX);
            int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
            int dollposZ = MathHelper.floor_double(theDoll.posZ);
            Pattern targetPattern = Pattern.compile(targetBlockRegex);
            Matcher targetMatcher;
            TreeMap<String, Integer> blockCount = new TreeMap<String, Integer>();
            int targetX = mineRange;
            int targetY = mineRange;
            int targetZ = mineRange;
            boolean isTarget = false;

            // 周囲のブロック検索
            for(int dx=-mineRange; dx<=mineRange; ++dx)
            {
                for(int dy=-mineRange; dy<=mineRange; ++dy)
                {
                    for(int dz=-mineRange; dz<=mineRange; ++dz)
                    {
                        int distanceToBlock = Math.abs(dx)+Math.abs(dy)+Math.abs(dz);
                        if(distanceToBlock > mineRange)
                        {
                            continue;
                        }
                        Block b = Block.blocksList[theWorld.getBlockId(dollposX+dx, dollposY+dy, dollposZ+dz)];
                        if(b==null)
                        {
                            continue;
                        }
                        String blockName = getBlockName(b);
                    
                        //採掘対象かどうか
                        targetMatcher = targetPattern.matcher(blockName);
                        if(targetMatcher.find())
                        {
                            if(blockCount.containsKey(blockName))
                            {
                                int c = blockCount.get(blockName).intValue() + 1;
                                blockCount.put(blockName, new Integer(c));
                            }
                            else
                            {
                                blockCount.put(blockName, new Integer(1));
                            }

                            if(Math.abs(targetX)+Math.abs(targetY)+Math.abs(targetZ) >distanceToBlock)
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
            }

            // 出力文字列の作成
            StringBuffer msg = new StringBuffer(theDoll.getDollName() + " : ");

            if(blockCount.isEmpty())
            {
                msg.append("No target");
                theDoll.chatMessage(msg.toString(),2);
            }
            else
            {
                Iterator it = blockCount.keySet().iterator();
                while(it.hasNext())
                {
                    String s = (String)it.next();
                    int v = blockCount.get(s).intValue();
                    msg.append(s);
                    msg.append("[");
                    msg.append(v);
                    msg.append("] ");
                }
                theDoll.chatMessage(msg.toString(),1);
            }

            if(isTarget && (theDoll.isPatrolMode() || theDoll.isFollowMode()))
            {
                //掘削対象ブロックの検索
                int mineX = 0;
                int mineY = 0;
                int mineZ = 0;
                
                boolean isMining = false;

                for(int r=1; r<=mineRange; ++r)
                {
                    boolean isAirBlock = false;
                    int ii, jj, kk;
                    ii = jj = kk = 0;
                    for(int k=-1; k<=1; ++k)
                    {
                        for(int j=-1; j<=1; ++j)
                        {
                            for(int i=-1; i<=1; ++i)
                            {
                                //カレントブロックに隣接するブロックを対象とする
                                if(Math.abs(i)+Math.abs(j)+Math.abs(k) != 1)
                                {
                                    continue;
                                }
                                if(Math.abs(targetX-(mineX+ii))+Math.abs(targetY-(mineY+jj))+Math.abs(targetZ-(mineZ+kk))
                                   > Math.abs(targetX-(mineX+i))+Math.abs(targetY-(mineY+j))+Math.abs(targetZ-(mineZ+k)))
                                {
                                    if(canDigBlock(dollposX+mineX+i, dollposY+mineY+j, dollposZ+mineZ+k))
                                    {
                                        //ターゲットにもっとも近い掘削可能ブロック
                                        ii=i;
                                        jj=j;
                                        kk=k;
                                        isMining = true;
                                    }
                                    else if(theWorld.isAirBlock(dollposX+mineX+i, dollposY+mineY+j, dollposZ+mineZ+k))
                                    {
                                        ii=i;
                                        jj=j;
                                        kk=k;                                    
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
                theDoll.isTargetLockon = isMining;
                theDoll.targetX = dollposX + mineX;
                theDoll.targetY = dollposY + mineY;
                theDoll.targetZ = dollposZ + mineZ;
            }
            else
            {
                theDoll.isTargetLockon = false;
            }
        }
        counter = (counter + 1)%20;
    }
    
    // 採掘可能かどうか判定する
	private boolean canDigBlock(int i, int j, int k)
	{
		if(theWorld.isAirBlock(i, j, k))
		{
			return false;
		}
		
		Block b = Block.blocksList[theWorld.getBlockId(i, j, k)];
		if(b!=null)
		{
			if(b.getBlockHardness(theWorld, i, j, k) < 0f || b.getBlockHardness(theWorld, i, j, k) > Block.obsidian.getBlockHardness(theWorld, 0, 0, 0))
			{
				return false;
			}
			if(theWorld.getBlockId(i, j + 1, k) == Block.sand.blockID || theWorld.getBlockId(i, j + 1, k) == Block.gravel.blockID)
			{
				return false;
			}
		}
		
		int u=-1;
		while(theWorld.isAirBlock(i, j+u, k))
		{
			if(j+u<=0)
			{
				return false;
			}
			if(theWorld.getBlockMaterial(i, j+u-1, k)==Material.lava)
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
}
