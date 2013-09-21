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
import mods.touhou_alice_dolls.AI.*;
import mods.touhou_alice_dolls.TouhouAliceDolls;
import mods.touhou_alice_dolls.EntityAliceDoll;

/**
 * 大江戸人形クラス
 */
public class DollOoedo extends DollBase
{
    public DollOoedo()
    {
        // アイテムの表示名を設定
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.ooedo.name", "en_US", "Ooedo Doll");
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.ooedo.name", "ja_JP", "大江戸人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "ooedo";
    }

    /** 人形アイテムのアイコン名 */
    @Override
    public String getIconName()
    {
        return this.getDollName();
    }

    /**
     * メインテクスチャのパス
     */
    @Override
    public String getMainTexturePath()
    {
        return "textures/dolls/ooedo.png";
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "TW ",
                               "WHW",
                               " W ",
                               'T', Block.tnt,
                               'W', Block.cloth,
                               'H', new ItemStack(TouhouAliceDolls.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Block.tnt);
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);

        doll.addAI(4, new EntityDollAIExplosion(doll));
        doll.addAI(8, new EntityDollAIQuarry(doll));
    }
}
