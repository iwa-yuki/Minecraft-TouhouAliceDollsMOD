package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAILevelingBlock extends EntityDollAIBase
{
    public static String levelingBlockRegex;
    public static int mineRange;
    public static int levelingRange = 8;

    private int counter;
    private boolean anchorLockon;
    private int anchorX;
    private int anchorY;
    private int anchorZ;
    private int levelingXmax;
    private int levelingXmin;
    private int levelingZmax;
    private int levelingZmin;
    Pattern targetPattern;
    Matcher targetMatcher;

    public EntityDollAILevelingBlock(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(8);
        targetPattern = Pattern.compile(levelingBlockRegex);
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
        if(subItem.itemID != Item.cake.itemID)
        {
            return false;
        }
        return true;
    }

    public void startExecuting()
    {
        counter = 0;
        anchorLockon = false;
    }
    
    public boolean continueExecuting()
    {
        return this.shouldExecute();
    }
    
    @Override
    public void resetTask()
    {
        theDoll.isTargetLockon = false;
        anchorLockon = false;
    }

    public void updateTask()
    {
        int dollposX = MathHelper.floor_double(theDoll.posX);
        int dollposY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
        int dollposZ = MathHelper.floor_double(theDoll.posZ);

        if(!anchorLockon)
        {
            // アンカー未発見のとき
            for(int i=-16;i<=16;++i)
            {
                for(int k=-16;k<=16;++k)
                {
                    // ダイヤブロックの上にレッドストーントーチがあるとアンカーとして認識する
                    if(theWorld.getBlockId(dollposX+i, counter, dollposZ+k)
                       == Block.blockDiamond.blockID)
                    {
                        if(theWorld.getBlockId(dollposX+i, counter+1, dollposZ+k)
                           == Block.torchRedstoneActive.blockID)
                        {
                            anchorLockon = true;
                            anchorX = dollposX+i;
                            anchorY = counter;
                            anchorZ = dollposZ+k;

                            // 掘削範囲制御
                            levelingXmax = levelingRange;
                            for(int lx=1;lx<=levelingRange;++lx)
                            {
                                if(theWorld.getBlockId(anchorX+lx, anchorY+1, anchorZ)
                                   == Block.torchRedstoneActive.blockID)
                                {
                                    levelingXmax = lx-1;
                                    break;
                                }
                            }
                            levelingXmin = levelingRange;
                            for(int lx=1;lx<=levelingRange;++lx)
                            {
                                if(theWorld.getBlockId(anchorX-lx, anchorY+1, anchorZ)
                                   == Block.torchRedstoneActive.blockID)
                                {
                                    levelingXmin = lx-1;
                                    break;
                                }
                            }
                            levelingZmax = levelingRange;
                            for(int lz=1;lz<=levelingRange;++lz)
                            {
                                if(theWorld.getBlockId(anchorX, anchorY+1, anchorZ+lz)
                                   == Block.torchRedstoneActive.blockID)
                                {
                                    levelingZmax = lz-1;
                                    break;
                                }
                            }
                            levelingZmin = levelingRange;
                            for(int lz=1;lz<=levelingRange;++lz)
                            {
                                if(theWorld.getBlockId(anchorX, anchorY+1, anchorZ-lz)
                                   == Block.torchRedstoneActive.blockID)
                                {
                                    levelingZmin = lz-1;
                                    break;
                                }
                            }
                                
                            StringBuffer msg = new StringBuffer(theDoll.getDollName() + " : ");
                            msg.append("Anchor lockon!");
                            theDoll.chatMessage(msg.toString(),2);
                            msg = new StringBuffer(theDoll.getDollName() + " : ");
                            msg.append("Cuarrying mode(");
                            msg.append(levelingXmax+levelingXmin+1);
                            msg.append(",");
                            msg.append(anchorY+1);
                            msg.append(",");
                            msg.append(levelingZmax+levelingZmin+1);
                            msg.append(")");
                            theDoll.chatMessage(msg.toString(),2);
                            break;
                        }
                    }
                    if(anchorLockon)
                    {
                        break;
                    }
                }
                if(anchorLockon)
                {
                    break;
                }
            }
            counter = (counter + 1) % 256;
        }
        else
        {
            // アンカーを見失ったとき
            if(theWorld.getBlockId(anchorX, anchorY, anchorZ) != Block.blockDiamond.blockID)
            {
                theDoll.isTargetLockon = false;
                anchorLockon = false;
                return;
            }
            if(theWorld.getBlockId(anchorX, anchorY+1, anchorZ) != Block.torchRedstoneActive.blockID)
            {
                theDoll.isTargetLockon = false;
                anchorLockon = false;
                return;
            }

            // アンカーが存在するとき

            // 採掘中であれば続行
            if(theDoll.isTargetLockon && this.canDigBlock(theDoll.targetX, theDoll.targetY, theDoll.targetZ))
            {
                return;
            }

            // 採掘中でなければ採掘対象を決める
            theDoll.isTargetLockon = false;
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
                            theDoll.targetX = dollposX+i;
                            theDoll.targetY = dollposY+j;
                            theDoll.targetZ = dollposZ+k;
                            theDoll.isTargetLockon = true;
                        }
                        if(theDoll.isTargetLockon)
                        {
                            break;
                        }
                    }
                    if(theDoll.isTargetLockon)
                    {
                        break;
                    }
                }
                if(theDoll.isTargetLockon)
                {
                    break;
                }
            }
            if(!theDoll.isTargetLockon)
            {
                // 手の届かない場所にしかブロックがない場合
                int j=dollposY>(anchorY+1) ? dollposY : (anchorY+1);
                for(int i=-levelingXmin; i<=levelingXmax; ++i)
                {
                    for(int k=-levelingZmin; k<=levelingZmax; ++k)
                    {
                        if(this.canDigBlock(anchorX+i, j, anchorZ+k))
                        {
                            theDoll.targetX = anchorX+i;
                            theDoll.targetY = j;
                            theDoll.targetZ = anchorZ+k;
                            theDoll.isTargetLockon = true;
                        }
                        if(theDoll.isTargetLockon)
                        {
                            break;
                        }
                    }
                    if(theDoll.isTargetLockon)
                    {
                        break;
                    }
                }
            }
            if(!theDoll.isTargetLockon)
            {
                // レッドストーントーチ破壊
                theDoll.targetX = anchorX;
                theDoll.targetY = anchorY+1;
                theDoll.targetZ = anchorZ;
                theDoll.isTargetLockon = true;

                StringBuffer msg = new StringBuffer(theDoll.getDollName() + " : ");
                msg.append("Quarrying finished.");
                theDoll.chatMessage(msg.toString(),2);
            }
        }
    }
    
    // 採掘可能かどうか判定する
	private boolean canDigBlock(int i, int j, int k)
	{
		if(theWorld.isAirBlock(i, j, k))
		{
			return false;
		}
		
		Block b = Block.blocksList[theWorld.getBlockId(i, j, k)];
		if(b==null)
        {
            return false;
        }
        else
		{
			if(b.getBlockHardness(theWorld, i, j, k) < 0f || b.getBlockHardness(theWorld, i, j, k) > Block.obsidian.getBlockHardness(theWorld, 0, 0, 0))
			{
				return false;
			}
			if(theWorld.getBlockId(i, j + 1, k) == Block.sand.blockID || theWorld.getBlockId(i, j + 1, k) == Block.gravel.blockID)
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
