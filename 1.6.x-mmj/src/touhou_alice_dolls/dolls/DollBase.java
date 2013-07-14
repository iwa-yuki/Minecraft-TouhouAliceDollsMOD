////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.client.*;

/**
 * 人形のベースクラス
 */
public class DollBase
{
    public DollBase()
    {
        // アイテムの表示名を設定
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.bare.name", "en_US", "Bare Doll");
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.bare.name", "ja_JP", "素体人形");
    }
    
    /** 人形の名前 */
    public String getDollName()
    {
        return "bare";
    }

    /** 人形アイテムのアイコン名 */
    public String getIconName()
    {
        return this.getDollName();
    }

    /**
     * メインテクスチャのパス
     */
    public String getMainTexturePath()
    {
        return "textures/dolls/bare.png";
    }

    /**
     * 防具テクスチャのパス
     * @param type 防具の種類
     * @param layer 防具のレイヤー
     */
    public String getArmorTexturePath(String type, int layer)
    {
        return "";
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    public void onInitializeAI()
    {
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のModelを生成する
     * @param id 人形のID
     * @param float Modelの拡張係数
     * @retuen 人形Model
     */
    public ModelAliceDoll getModelInstance(float expand)
    {
        return new ModelAliceDoll(expand);
    }

}
