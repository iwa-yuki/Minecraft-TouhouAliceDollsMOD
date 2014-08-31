////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_extras.dolls;

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
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.lang.reflect.Field;

import mods.touhou_alice_core.dolls.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_extras.AI.*;
import mods.touhou_alice_core.client.*;

/**
 * ゴリアテ人形クラス
 */
public class DollGoliath extends DollBase
{
    public DollGoliath()
    {
        // アイテムの表示名を設定
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.goliath.name", "en_US", "Goliath Doll");
//        LanguageRegistry.instance().addStringLocalization(
//            "item.alicedoll.goliath.name", "ja_JP", "ゴリアテ人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "goliath";
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
        return "textures/dolls/goliath.png";
    }

    /**
     * 防具テクスチャのパス
     * @param type 防具の素材
     * @param slot 防具の種類
     * @param var テクスチャのバリエーション
     */
    public String getArmorTexturePath(int type, int slot ,String var)
    {
        return String.format("textures/dolls/armor/venti/%d_%d%s.png",type, (slot<=1?1:2),
                             (var == null ? "" : var));
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        GameRegistry.addRecipe(
            new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                          DollRegistry.getDollID(getDollName())),
            "SW ",
            "WHW",
            " W ",
            'S', Items.diamond_sword,
            'W', Blocks.iron_block,
            'H', new ItemStack(TouhouAliceCore.instance.itemAliceDoll,
                               1, 0));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        return new ItemStack(Items.diamond_sword);
    }
    
    /**
     * 人形の移動速度を取得する
     */
    public double getSpeed()
    {
        return 0.28D;
    }

    /**
     * 人形の高さを取得する
     */
    public float getHeight()
    {
        return 2.8F;
    }

    /**
     * 人形の幅を取得する
     */
    public float getWidth()
    {
        return 0.75F;
    }

    /**
     * 人形の体力を取得する
     */
    public double getHealth()
    {
        return 50.0D;
    }

    /**
     * インベントリのサイズを取得する
     */
    public int getSizeInventory()
    {
        return 9;
    }
    
    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);
        doll.addAI(1, new EntityDollAIAttackWithGoliath(doll));
    }
    @SideOnly(Side.CLIENT)
    /**
     * 人形のModelを生成する
     */
    public ModelBiped getModelInstance(float expand)
    {
        ModelAliceDoll model = new ModelAliceDoll(expand, 0F, 128, 128);

        model.setRenderType(getRenderType());

        model.addBox(0, 0, false, -4F, -8F, -4F, 8, 8, 8, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(64, 17, false, -7F, -1F, 0F, 11, 4, 1, 0F,
                     0F, -20F, 4F, 28.822F, 0F, -21.302F, "ribbon1", "head");
        model.addBox(64, 17, false, -4F, -1F, 0F, 11, 4, 1, 0F,
                     0F, -20F, 4F, 28.822F, 0F, 21.302F, "ribbon2", "head");
        model.addBox(32, 0, false, -4F, -8F, -4F, 8, 8, 8, 0.5F,
                     0F, -12F, 0F, 0F, 0F, 0F, "headwear", null);
        model.addBox(64, 0, false, -4F, -1F, -4F, 8, 9, 8, 0.5F,
                     0F, -12F, 0F, 14.911F, 0F, 0F, "headwear2", "head");
        model.addBox(0, 40, false, -4F, 0F, -2F, 8, 7, 4, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(0, 51, false, -3F, 7F, -2F, 6, 5, 4, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "body2", "body");
        model.addBox(0, 60, false, -4F, 12F, -2F, 8, 4, 5, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "body3", "body");
        model.addBox(28, 16, false, -4F, 1.6F, -1.5F, 8, 5, 2, 0F,
                     0F, -12F, 0F, -22F, 0F, 0F, "chest", "body");
        model.addBox(0, 69, false, -5F, 14F, -3F, 10, 1, 7, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(0, 77, false, -6F, 15F, -4F, 12, 2, 9, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "skirt2", "body");
        model.addBox(0, 88, false, -7F, 17F, -5F, 14, 3, 11, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "skirt3", "body");
        model.addBox(0, 102, false, -8F, 20F, -6F, 16, 4, 13, 0F,
                     0F, -12F, 0F, 0F, 0F, 0F, "skirt4", "body");
        model.addBox(16, 16, false, -3F, -1F, -1.5F, 3, 14, 3, 0F,
                     -4F, -10F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(28, 23, false, -4.6F, -2F, -2F, 5, 4, 4, 0F,
                     -4F, -10F, 0F, 0F, 0F, -12.781F, "rightwear", "rightarm");
        model.addBox(28, 31, false, -3F, 10F, -1.5F, 3, 1, 3, 0.5F,
                     -4F, -10F, 0F, 0F, 0F, 0F, "rightring", "rightarm");
        model.addBox(16, 16, true, 0F, -1F, -1.5F, 3, 14, 3, 0F,
                     4F, -10F, 0F, 0F, 0F, 0F, "leftarm", null);
        model.addBox(46, 23, true, -0.4F, -2F, -2F, 5, 4, 4, 0F,
                     4F, -10F, 0F, 0F, 0F, 12.781F, "leftwear", "leftarm");
        model.addBox(28, 31, true, -1F, 10F, -1.5F, 3, 1, 3, 0.5F,
                     5F, -10F, 0F, 0F, 0F, 0F, "leftring", "leftarm");
        model.addBox(0, 16, false, -2F, 4F, 0F, 4, 20, 4, 0F,
                     -2F, 0F, -2F, 0F, 0F, 0F, "rightleg", null);
        model.addBox(0, 16, true, 2F, 4F, 0F, 4, 20, 4, 0F,
                     -2F, 0F, -2F, 0F, 0F, 0F, "leftleg", null);

        return model;
    }

    /**
     * 人形のレンダータイプを取得する
     */
    @Override
    public EnumDollRenderType getRenderType()
    {
        return EnumDollRenderType.VENTI;
    }
}
