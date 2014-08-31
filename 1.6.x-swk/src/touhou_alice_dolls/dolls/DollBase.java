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
     * @param type 防具の素材
     * @param slot 防具の種類
     * @param var テクスチャのバリエーション
     */
    public String getArmorTexturePath(int type, int slot ,String var)
    {
        return String.format("textures/dolls/armor/doll/%d_%d%s.png",type, (slot<=1?1:2),
                             (var == null ? "" : var));
    }

    /**
     * 人形のレシピを追加する
     */
    public void addRecipes()
    {
        GameRegistry.addRecipe(
            new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                          DollRegistry.getDollID(getDollName())),
            " W ",
            "WHW",
            " W ",
            'W', Block.cloth,
            'H', new ItemStack(TouhouAliceDolls.instance.itemDollCore));
    }

    /**
     * 人形の高さを取得する
     */
    public float getHeight()
    {
        return 0.7F;
    }

    /**
     * 人形の幅を取得する
     */
    public float getWidth()
    {
        return 0.3F;
    }

    /**
     * 人形の体力を取得する
     */
    public double getHealth()
    {
        return 8.0D;
    }
    
    /**
     * 人形の移動速度を取得する
     */
    public double getSpeed()
    {
        return 0.25D;
    }
    
    /**
     * 手持ちアイテムを取得する
     */
    public ItemStack getHeldItem()
    {
        return null;
    }

    /**
     * AIの初期化が必要なときに呼ばれる
     */
    public void onInitializeAI(EntityAliceDoll doll)
    {
        doll.addAI(0, new EntityDollAISwimming(doll));
        doll.addAI(12, new EntityDollAIFollowOwner(doll));
        doll.addAI(13, new EntityDollAIWander(doll));
        doll.addAI(14, new EntityDollAIWatchOwner(doll));
        doll.addAI(15, new EntityDollAIWatchClosest(doll));
        doll.addAI(16, new EntityDollAILookIdle(doll));
    }

    /**
     * インベントリのサイズを取得する
     */
    public int getSizeInventory()
    {
        return 9;
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のModelを生成する
     */
    public ModelBiped getModelInstance(float expand)
    {
        ModelAliceDoll model = new ModelAliceDoll(expand);

        model.setRenderType(getRenderType());

        model.addBox(0, 4, false, -2F, -4F, -2F, 4, 4, 4, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(0, 0, false, -3F, -1.2F, -0.8F, 6, 3, 1, 0.1F,
                     0F, 10F, 2F, 0.2F*1.570796F, 0F, 0F, "ribbon", "head");
        model.addBox(16, 0, false, -2F, 0F, -1F, 4, 4, 2, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(16, 6, false, -3F, 4F, -2F, 6, 2, 4, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(16, 12, false, -4F, 6F, -3F, 8, 2, 6, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "skirt2", "body");
        model.addBox(0, 12, false, -2F, -1F, -1F, 2, 4, 2, 0F,
                     -2F, 15F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(8, 12, false, 0F, -1F, -1F, 2, 4, 2, 0F,
                     2F, 15F, 0F, 0F, 0F, 0F, "leftarm", null);
        model.addBox(0, 18, false, -1F, 0F, -1F, 2, 4, 2, 0F,
                     -1F, 20F, 0F, 0F, 0F, 0F, "rightleg", null);
        model.addBox(8, 18, false, -1F, 0F, -1F, 2, 4, 2, 0F,
                     1F, 20F, 0F, 0F, 0F, 0F, "leftleg", null);

        return model;
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のレンダータイプを取得する
     */
    EnumRenderType getRenderType()
    {
        return EnumRenderType.DOLL;
    }
}
