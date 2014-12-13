package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIReportBlock extends EntityDollAIBase
{
    private int counter;

    public static String targetBlockRegex;
    public static int mineRange;

    public EntityDollAIReportBlock(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(4);
    }

    @Override
    public boolean shouldExecute()
    {
        return theDoll.isEnable();
    }

    public void startExecuting()
    {
    	super.startExecuting();
    	
        counter = 0;
    }
    
    public boolean continueExecuting()
    {
        return theDoll.isEnable();
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
                        Block b = theWorld.getBlockState(new BlockPos(dollposX+dx, dollposY+dy, dollposZ+dz)).getBlock();
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

                            // if(Math.abs(targetX)+Math.abs(targetY)+Math.abs(targetZ) >distanceToBlock)
                            // {
                            //     if(canDigBlock(dollposX+dx, dollposY+dy, dollposZ+dz))
                            //     {
                            //         targetX = dx;
                            //         targetY = dy;
                            //         targetZ = dz;
                            //         isTarget = true;
                            //     }
                            // }
                        }
                    }
                }
            }

            // 出力文字列の作成
            StringBuffer msg = new StringBuffer(theDoll.getName() + " : ");

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
        }
        counter = (counter + 1)%20;
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
