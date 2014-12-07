package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.pathfinding.PathNavigate;
import mods.touhou_alice_core.ai.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAIReportArtifact extends EntityDollAIBase
{
    private int counter;
    private int chunkX;
    private int chunkZ;
    private int baseY;
    private TreeMap<Integer, String> spawnerMap;

    public EntityDollAIReportArtifact(EntityAliceDoll doll)
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
            chunkX = MathHelper.floor_double(theDoll.posX)/16;
            baseY = MathHelper.floor_double(theDoll.posY + (double)theDoll.getEyeHeight());
            chunkZ = MathHelper.floor_double(theDoll.posZ)/16;
            
            spawnerMap = new TreeMap<Integer, String>();
        }
        
        for(int dx = -16; dx <= 32; ++dx)
        {
            for(int dz = -16; dz <= 32; ++dz)
            {
                for(int dy = -1; dy <= 1; dy+=2)
                {
                    if(counter == 0 && dy == 1)
                    {
                        continue;
                    }
                    Block b = theWorld.getBlock(chunkX*16+dx, baseY+counter*dy, chunkZ*16+dz);
                    if(b == Blocks.mob_spawner)
                    {
                        TileEntityMobSpawner spawner = (TileEntityMobSpawner)(theWorld.getTileEntity(chunkX*16+dx, baseY+counter*dy, chunkZ*16+dz));
                        MobSpawnerBaseLogic spawnerLogic = spawner.func_145881_a();
                        
                        if(spawner != null)
                        {
                            String mobname = spawnerLogic.getEntityNameToSpawn();
                            if(mobname == null)
                            {
                                mobname = "unknown";
                            }
                            else if(mobname == "")
                            {
                                mobname = "unknown";
                            }

                            if(spawnerMap.containsKey(counter*dy))
                            {
                                String s = spawnerMap.get(counter*dy);
                                spawnerMap.put(counter*dy, String.format("%s,%s", s, mobname));
                            }
                            else
                            {
                                spawnerMap.put(counter*dy, mobname);
                            }
                        }

                    }
                }
            }
        }
        
        if(counter == 63)
        {
            StringBuffer msg=new StringBuffer(theDoll.getName() + " : ");
            for(int i=64; i>0; --i)
            {
                if(i%8 == 0)
                {
                    msg.append("-");
                }
                if(spawnerMap.containsKey(i))
                {
                    msg.append("[");
                    msg.append(spawnerMap.get(i));
                    msg.append("]");
                }
            }
            if(spawnerMap.containsKey(0))
            {
                msg.append("<[");
                msg.append(spawnerMap.get(0));
                msg.append("]>");
            }
            else
            {
                msg.append(getStrongholdDirection());
            }
            for(int i=-1; i>=-64; --i)
            {
                if(spawnerMap.containsKey(i))
                {
                    msg.append("[");
                    msg.append(spawnerMap.get(i));
                    msg.append("]");
                }
                if(i%8 == 0)
                {
                    msg.append("-");
                }
            }
            
            theDoll.chatMessage(msg.toString(),2);
        }
        
        counter = (counter + 1) % 64;
    }
    
    //遺跡の方角を計算する
	public String getStrongholdDirection()
	{
		ChunkPosition chunkposition = theWorld.findClosestStructure("Stronghold", (int)theDoll.posX, (int)theDoll.posY, (int)theDoll.posZ);
		
		if(chunkposition == null)
		{
			return "<->";
		}
		
		double vecX = chunkposition.chunkPosX - theDoll.posX;
		double vecY = chunkposition.chunkPosY - theDoll.posY;
		double vecZ = chunkposition.chunkPosZ - theDoll.posZ;
		double dist_sq = vecX*vecX + vecZ*vecZ + 0.01D;
		
		if(dist_sq<16.0D)
		{
			return "<C>";
		}
		
		if(vecX>=vecZ && vecX>=-vecZ)
		{
			return "<E>";
		}
		if(-vecX>=vecZ && -vecX>=-vecZ)
		{
			return "<W>";
		}
		if(vecZ>=vecX && vecZ>=-vecX)
		{
			return "<S>";
		}
		if(-vecZ>=vecX && -vecZ>=-vecX)
		{
			return "<N>";
		}
		return "<->";
	}
}
