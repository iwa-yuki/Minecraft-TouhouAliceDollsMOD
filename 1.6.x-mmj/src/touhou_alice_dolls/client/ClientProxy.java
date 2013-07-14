////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.client;

import cpw.mods.fml.client.registry.RenderingRegistry;

import mods.touhou_alice_dolls.*;

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
        RenderingRegistry.registerEntityRenderingHandler(
                EntityAliceDoll.class, new RenderAliceDoll());
    }
}
