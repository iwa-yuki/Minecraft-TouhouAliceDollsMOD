////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mods.touhou_alice_core.doll.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_dolls.AI.*;

/**
 * グランギニョルクラス
 */
public class DollGrandGuignol extends DollBase
{
    public DollGrandGuignol()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.grandguignol.name", "en_US", "Grand-Guignol");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.grandguignol.name", "ja_JP", "グランギニョル");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "grandguignol";
    }
    
    /**
     * メインテクスチャのパス
     */
    @Override
    public String getMainTexturePath()
    {
        return "textures/dolls/grandguignol.png";
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
//        GameRegistry.addRecipe(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
//                                             DollRegistry.getDollID(getDollName())),
//                               "SW ",
//                               "WHW",
//                               " W ",
//                               'S', new ItemStack(Items.skull, 1, 1), // Wither skull
//                               'W', Blocks.wool,
//                               'H', new ItemStack(TouhouAliceCore.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return null; //new ItemStack(Items.skull, 1, 1);
    }

    /**
     * 隠しかどうかを取得する
     */
    public boolean isSecret()
    {
    	return true;
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);
    }
}
