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
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.touhou_alice_core.dolls.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_dolls.AI.*;

/**
 * 倫敦人形クラス
 */
public class DollLondon extends DollBase
{
    public DollLondon()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.london.name", "en_US", "London Doll");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.london.name", "ja_JP", "倫敦人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "london";
    }

    
    /**
     * メインテクスチャのパス
     */
    @Override
    public String getMainTexturePath()
    {
        return "touhou_alice_core:textures/dolls/london.png";
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "EW ",
                               "WHW",
                               " W ",
                               'E', Items.ender_eye,
                               'W', Blocks.wool,
                               'H', new ItemStack(TouhouAliceCore.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Items.ender_eye);
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);

        doll.addAI(11, new EntityDollAIReportArtifact(doll));
    }
}
