////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mods.touhou_alice_core.*;
import mods.touhou_alice_core.packet.MessageAyaShot;

/**
 * クライアント側のみの処理を行うクラス
 */
public class ClientProxy extends CommonProxy
{
    /**
     * レンダラの登録
     */
    @Override
    public void registerRenderers()
    {
        render = new RenderAliceDoll();
        RenderingRegistry.registerEntityRenderingHandler(
                EntityAliceDoll.class, render);
    }
    
    /**
     * 人形モデルの登録
     */
    @Override
    public void registerDollModel(int id)
    {
        render.registerDollModel(id);
    }

    /**
     * 射命丸人形がスクリーンショットを生成する必要があるときに呼ばれる
     * @param message
     * @param ctx
     */
    @Override
    public boolean onMessageAyaShot(MessageAyaShot message, MessageContext ctx)
    {
    	AyaCamera ayaya = new AyaCamera();
        return ayaya.shot(message.entityID);
    }

    private RenderAliceDoll render;
}
