////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mods.touhou_alice_core.packet.MessageAyaShot;
import net.minecraft.entity.player.EntityPlayer;

/**
 * サーバー・クライアント共通の処理を行うクラス
 */
public class CommonProxy
{
    /**
     * レンダラの登録
     */
    public void registerRenderers()
    {
        // 何もしない
    }

    /**
     * 人形モデルの登録
     */
    public void registerDollModel(int id)
    {
        // 何もしない
    }
    
    /**
     * 射命丸人形がスクリーンショットを生成する必要があるときに呼ばれる
     * @param message
     * @param ctx
     * @return スクリーンショット生成成功ならtrue
     */
    public boolean onMessageAyaShot(MessageAyaShot message, MessageContext ctx)
    {
        return false;
    }
}
