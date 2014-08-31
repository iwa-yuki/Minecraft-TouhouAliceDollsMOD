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
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.lang.reflect.Field;

import mods.touhou_alice_dolls.client.*;
import mods.touhou_alice_dolls.AI.*;
import mods.touhou_alice_dolls.TouhouAliceDolls;
import mods.touhou_alice_dolls.EntityAliceDoll;

/**
 * 幽香人形クラス
 */
public class DollYuuka extends DollBase
{
    public DollYuuka()
    {
        // アイテムの表示名を設定
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.yuuka.name", "en_US", "Yuuka Doll");
        LanguageRegistry.instance().addStringLocalization(
            "item.alicedoll.yuuka.name", "ja_JP", "幽香人形");
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "yuuka";
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
        return "textures/dolls/yuuka.png";
    }

    /**
     * 防具テクスチャのパス
     * @param type 防具の素材
     * @param slot 防具の種類
     * @param var テクスチャのバリエーション
     */
    public String getArmorTexturePath(int type, int slot ,String var)
    {
        return String.format("textures/dolls/armor/grande/%d_%d%s.png",type, (slot<=1?1:2),
                             (var == null ? "" : var));
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
        ItemStack heldItem = getHeldItem();
        if(heldItem != null)
        {
            GameRegistry.addRecipe(
                new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                              DollRegistry.getDollID(getDollName())),
                "MW ",
                "WHW",
                " W ",
                'M', getHeldItem(),
                'W', Block.cloth,
                'H', new ItemStack(TouhouAliceDolls.instance.itemAliceDoll,
                                   1, 0));
        }
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        ItemStack mrsHeldItem = null; //new ItemStack(Block.plantRed);
        try
        {
            Class<?> c = Class.forName(
                "net.minecraft.thKaguyaMod.mod_thKaguya");
            Field f = c.getField("yuukaParasolItem");
            Object obj = f.get(null);
            mrsHeldItem = new ItemStack((Item)obj);
            FMLLog.info("Found \"thKaguya.yuukaParasolItem\"!");
        }
        catch(Exception e)
        {
            FMLLog.info("Not found \"thKaguya.yuukaParasolItem\".");
        }
        return mrsHeldItem;
    }
    
    /**
     * 人形の移動速度を取得する
     */
    public double getSpeed()
    {
        return 0.24D;
    }

    /**
     * 人形の高さを取得する
     */
    public float getHeight()
    {
        return 1.5F;
    }

    /**
     * 人形の幅を取得する
     */
    public float getWidth()
    {
        return 0.35F;
    }

    /**
     * 人形の体力を取得する
     */
    public double getHealth()
    {
        return 16.0D;
    }

    /**
     * インベントリのサイズを取得する
     */
    public int getSizeInventory()
    {
        return 18;
    }
    
    /**
     * AIの初期化が必要なときに呼ばれる
     */
    @Override
    public void onInitializeAI(EntityAliceDoll doll)
    {
        super.onInitializeAI(doll);
        doll.addAI(1, new EntityDollAIFarmer(doll));
    }
    @SideOnly(Side.CLIENT)
    /**
     * 人形のModelを生成する
     */
    public ModelBiped getModelInstance(float expand)
    {
        ModelAliceDoll model = new ModelAliceDoll(expand, 0F, 64, 64);

        model.setRenderType(getRenderType());

        model.addBox(0, 0, false, -3F, -6F, -3F, 6, 6, 6, 0F,
                     0F, 6F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(24, 0, false, -3F, -6F, -3F, 6, 6, 6, 0.375F,
                     0F, 6F, 0F, 0F, 0F, 0F, "headwear", null);
        model.addBox(0, 12, false, -3F, 0F, -1.5F, 6, 8, 3, 0F,
                     0F, 6F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(0, 24, false, -3F, 1.5F, -1F, 6, 4, 2, 0F,
                     0F, 5F, 0F, -20F, 0F, 0F, "chest", "body");
        model.addBox(0, 30, false, -3F, 0F, -2F, 6, 1, 4, 0F,
                     0F, 13F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(0, 35, false, -4F, 0F, -3F, 8, 2, 6, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "skirt2", "body");
        model.addBox(0, 43, false, -5F, 0F, -4.5F, 10, 5, 9, 0F,
                     0F, 16F, 0F, 0F, 0F, 0F, "skirt3", "body");
        model.addBox(30, 12, false, -2F, -1F, -1F, 2, 8, 2, 0F,
                     -3F, 7F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(30, 12, true, 0F, -1F, -1F, 2, 8, 2, 0F,
                     3F, 7F, 0F, 0F, 0F, 0F, "leftarm", null);
        model.addBox(18, 12, false, -3F, 0F, -1.5F, 3, 9, 3, 0F,
                     0F, 15F, 0F, 0F, 0F, 0F, "rightleg", null);
        model.addBox(18, 12, true, 0F, 0F, -1.5F, 3, 9, 3, 0F,
                     0F, 15F, 0F, 0F, 0F, 0F, "leftleg", null);

        return model;
    }

    /**
     * 人形のレンダータイプを取得する
     */
    EnumRenderType getRenderType()
    {
        return EnumRenderType.GRANDE;
    }
}
