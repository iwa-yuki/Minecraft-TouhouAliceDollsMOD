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
import mods.touhou_alice_core.doll.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_dolls.AI.*;

/**
 * 独逸人形クラス
 */
public class DollGermany extends DollBase
{
    public DollGermany()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.germany.name", "en_US", "Germany Doll");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.germany.name", "ja_JP", "独逸人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "germany";
    }
    
    /**
     * メインテクスチャのパス
     */
    @Override
    public String getMainTexturePath()
    {
        return "textures/dolls/germany.png";
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "AW ",
                               "WHW",
                               " W ",
                               'A', Items.golden_axe,
                               'W', Blocks.wool,
                               'H', new ItemStack(TouhouAliceCore.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Items.golden_axe);
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);

        doll.addAI(10, new EntityDollAICutTree(doll));
        doll.addAI(11, new EntityDollAIPlantTree(doll));
    }
}
