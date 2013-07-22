////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.client.*;
import mods.touhou_alice_dolls.TouhouAliceDolls;

/**
 * 上海人形クラス
 */
public class DollShanghai extends DollBase
{
    public DollShanghai()
    {
        // アイテムの表示名を設定
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.shanghai.name", "en_US", "Shanghai Doll");
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.shanghai.name", "ja_JP", "上海人形");
    }
    
    /** 人形の名前 */
    public String getDollName()
    {
        return "shanghai";
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
        return "textures/dolls/shanghai.png";
    }

    /**
     * 人形のレシピを追加する
     */
    public void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "SW ",
                               "WHW",
                               " W ",
                               'S', new ItemStack(Item.swordGold),
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(TouhouAliceDolls.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    public ItemStack getHeldItem()
    {
        return new ItemStack(Item.swordGold);
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    public void onInitializeAI()
    {
    }
}
