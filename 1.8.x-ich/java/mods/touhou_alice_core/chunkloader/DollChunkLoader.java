////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.chunkloader;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.*;
import net.minecraftforge.common.DimensionManager;
import java.util.*;
import com.google.common.collect.*;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;

import mods.touhou_alice_core.*;

public class DollChunkLoader implements LoadingCallback
{
    private Object mod;

    /**
     * チャンクローダー初期化
     * @param mod 登録元のMOD
     */
    public DollChunkLoader(Object mod)
    {
        this.mod = mod;
        // コールバック登録
        ForgeChunkManager.setForcedChunkLoadingCallback(mod, this);
    }

    /**
     * Ticketの読み込みが完了したときに呼ばれる
     * @param tickets 読み込まれたTicketリスト
     * @param world チャンクロードの対象となるWorldオブジェクト
     */
    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
        for(Ticket t : tickets)
        {
            // 人形との紐付けを行う
            Entity entity = t.getEntity();
            if((entity != null) && (entity instanceof EntityAliceDoll))
            {
                EntityAliceDoll doll = (EntityAliceDoll)(entity);

                if(doll.enableChunkLoad())
                {
                    doll.updateChunkLoad();
                    
                    FMLLog.info("%s[%d].loadChunks",
                                doll.getDollName(), doll.hashCode());
                    if(doll.ticket != null)
                    {
                        ImmutableSet chunkList =  doll.ticket.getChunkList();
                        for(Object c : chunkList)
                        {
                            FMLLog.info("    + %s : dim %d", c.toString(), doll.worldObj.provider.dimensionId);
                        }
                    }
                    continue;
                }
            }
            
            ForgeChunkManager.releaseTicket(t);
        }
    }

    /**
     * チャンクローダーにチャンクを登録する
     * @param doll チャンクロードのアンカーとなる人形
     * @param chunk 追加するチャンク
     */
    public void addChunk(EntityAliceDoll doll, ChunkCoordIntPair chunk)
    {
        if(doll == null || doll.ticket == null)
        {
            return;
        }
        ForgeChunkManager.forceChunk(doll.ticket, chunk);

        // FMLLog.info("%s[%d].forceChunk(%d,%d)",
        //             doll.getDollName(), doll.entityId,
        //             chunk.chunkXPos, chunk.chunkZPos);
    }

    /**
     * チャンクローダーからチャンクを削除する
     * @param doll チャンクロードのアンカーとなる人形
     * @param chunk 削除するチャンク
     */
    public void removeChunk(EntityAliceDoll doll, ChunkCoordIntPair chunk)
    {
        if(doll == null || doll.ticket == null)
        {
            return;
        }
        ForgeChunkManager.unforceChunk(doll.ticket, chunk);

        // FMLLog.info("%s[%d].unforceChunk(%d,%d)",
        //             doll.getDollName(), doll.entityId,
        //             chunk.chunkXPos, chunk.chunkZPos);
    }

    /**
     * Ticketを発行し、チャンクロードを開始する
     * @param doll チャンクロードのアンカーとなる人形
     */
    public void requestTicket(EntityAliceDoll doll)
    {
        if(doll == null)
        {
            return;
        }
        if(doll.ticket != null)
        {
            releaseTicket(doll);
        }
        doll.ticket = ForgeChunkManager.requestTicket(mod, doll.worldObj, Type.ENTITY);
        if(doll.ticket != null)
        {
            FMLLog.info("DollChunkManager.requestTicket(%s[%d])",
                        doll.getDollName(), doll.hashCode());
            doll.ticket.bindEntity(doll);
        }
    }
    
    /**
     * Ticketを開放し、チャンクロードを終了する
     * @param doll チャンクロードのアンカーとなる人形
     */
    public void releaseTicket(EntityAliceDoll doll)
    {
        if(doll.ticket != null)
        {
            ForgeChunkManager.releaseTicket(doll.ticket);
            
            FMLLog.info("DollChunkManager.releaseTicket(%s[%d])",
                        doll.getDollName(), doll.hashCode());
            doll.ticket = null;
        }
    }
}

