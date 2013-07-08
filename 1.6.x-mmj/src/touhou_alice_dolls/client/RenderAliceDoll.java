////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls.client;

import net.minecraft.client.renderer.entity.RenderBiped;

import iwa_yuki.touhou_alice_dolls.*;

/**
 * 人形のレンダラクラス
 */
public class RenderAliceDoll extends RenderBiped
{
    public RenderAliceDoll()
    {
        super(new ModelAliceDoll(0.0F), 0.5F);
    }

    /**
     * 防具のモデルを設定
     */
    @Override
    protected void func_82421_b()
    {
        this.field_82423_g = new ModelAliceDoll(0.2F);
        this.field_82425_h = new ModelAliceDoll(0.1F);
    }
}
