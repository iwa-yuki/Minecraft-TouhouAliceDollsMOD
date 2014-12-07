////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.touhou_alice_core.doll.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_dolls.AI.*;

/**
 * 大江戸人形クラス
 */
public class DollOoedo extends DollBase
{
    public DollOoedo()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.ooedo.name", "en_US", "Ooedo Doll");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.ooedo.name", "ja_JP", "大江戸人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "ooedo";
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
        GameRegistry.addRecipe(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "TW ",
                               "WHW",
                               " W ",
                               'T', Blocks.tnt,
                               'W', Blocks.wool,
                               'H', new ItemStack(TouhouAliceCore.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Blocks.tnt);
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
