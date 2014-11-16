////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.client;

import cpw.mods.fml.client.registry.RenderingRegistry;

import mods.touhou_alice_core.*;

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

    private RenderAliceDoll render;
}
