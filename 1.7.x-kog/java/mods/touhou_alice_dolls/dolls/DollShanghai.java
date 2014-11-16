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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.touhou_alice_core.dolls.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_dolls.AI.*;

/**
 * 上海人形クラス
 */
public class DollShanghai extends DollBase
{
    public DollShanghai()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.shanghai.name", "en_US", "Shanghai Doll");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.shanghai.name", "ja_JP", "上海人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "shanghai";
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
        return "textures/dolls/shanghai.png";
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                             DollRegistry.getDollID(getDollName())),
                               "SW ",
                               "WHW",
                               " W ",
                               'S', Items.golden_sword,
                               'W', Blocks.wool,
                               'H', new ItemStack(TouhouAliceCore.instance.itemDollCore));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Items.golden_sword);
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);

        doll.addAI(1, new EntityDollAIReportEntity(doll));
        doll.addAI(2, new EntityDollAIAttackEnemy(doll));
        doll.addAI(3, new EntityDollAIStealItem(doll));
    }
}
