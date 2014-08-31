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
 * 霊夢人形クラス
 */
public class DollWakasagi extends DollBase
{
    public DollWakasagi()
    {
    }
    
    /** 人形の名前 */
    @Override
    public String getDollName()
    {
        return "wakasagi";
    }

    /** 人形アイテムのアイコン名 */
    @Override
    public String getIconName()
    {
        return "bare";
    }

    /**
     * メインテクスチャのパス
     */
    @Override
    public String getMainTexturePath()
    {
        return "textures/dolls/wakasagi.png";
    }
    
    /**
     * 隠しかどうか
     */
    @Override
    public boolean isSecret()
    {
    	return true;
    }
    
    /**
     * 防具テクスチャのパス
     * @param type 防具の素材
     * @param slot 防具の種類
     * @param var テクスチャのバリエーション
     */
    public String getArmorTexturePath(int type, int slot ,String var)
    {
        return String.format("textures/dolls/armor/tall/%d_%d%s.png",type, (slot<=1?1:2),
                             (var == null ? "" : var));
    }

    /**
     * 人形のレシピを追加する
     */
    @Override
    public void addRecipes()
    {
//        GameRegistry.addRecipe(
//            new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
//                          DollRegistry.getDollID(getDollName())),
//            "MW ",
//            "WHW",
//            " W ",
//            'M', Items.diamond,
//            'W', Blocks.wool,
//            'H', new ItemStack(TouhouAliceCore.instance.itemAliceDoll,
//                               1, 0));
    }

    /**
     * 手持ちアイテムを取得する
     */
    @Override
    public ItemStack getHeldItem()
    {
        ItemStack rmuHeldItem = null;
//        try
//        {
//            Class<?> c = Class.forName(
//                "thKaguyaMod.init.THKaguyaItems");
//            Field f = c.getField("hakurei_miko_stick");
//            Object obj = f.get(null);
//            rmuHeldItem = new ItemStack((Item)obj);
//            FMLLog.info("Found \"thKaguyaMod.init.THKaguyaItems.hakurei_miko_stick\"!");
//        }
//        catch(Exception e)
//        {
//        }
        return rmuHeldItem;
    }
    
    /**
     * レシピ用のアイテムを取得する
     */    
    public ItemStack getRecipeItem()
    {
        ItemStack item = null; //new ItemStack(Item.stick);
//        try
//        {
//            Class<?> c = Class.forName(
//                "thKaguyaMod.init.THKaguyaItems");
//            Field f = c.getField("yin_yang_orb");
//            Object obj = f.get(null);
//            item = new ItemStack((Item)obj);
//            FMLLog.info("Found \"thKaguyaMod.init.THKaguyaItems.yin_yang_orb\"!");
//        }
//        catch(Exception e)
//        {
//        }
        return item;
    }
    
    /**
     * 人形の移動速度を取得する
     */
    public double getSpeed()
    {
        return 0.22D;
    }

    /**
     * 人形の高さを取得する
     */
    public float getHeight()
    {
        return 1.4F;
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
        return 12.0D;
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
                     0F, 8F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(48, 0, false, -2.5F, -1.0F, -1.0F, 2, 1, 3, 0F,
                     -2F, 5F, -1F, -76.15F, 0F, -25.56F, "rightear", "head");
        model.addBox(48, 0, true, 0F, -1F, -1F, 2, 1, 3, 0F,
                     2.5F, 5F, -1F, -76.15F, 0F, 25.56F, "leftear", "head");
        model.addBox(24, 0, false, -3F, -6F, -3F, 6, 6, 6, 0.375F,
                     0F, 8F, 0F, 0F, 0F, 0F, "headwear", null);
        model.addBox(0, 12, false, -3F, 0F, -1.5F, 6, 7, 3, 0F,
                     0F, 8F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(0, 24, false, -3F, 1.5F, -1F, 6, 3, 2, 0F,
                     0F, 8F, 0F, -20F, 0F, 0F, "chest", "body");
        model.addBox(0, 29, false, -3F, 0F, -2F, 6, 1, 5, 0F,
                     0F, 15F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(22, 25, false, -4F, 0F, -3F, 8, 4, 6, 0F,
                     0F, 15.5F, 0.5F, 9F, 0F, 0F, "skirt2", "body");
        model.addBox(0, 42, false, -3F, 0F, -4F, 6, 7, 5, 0F,
                     0F, 16F, 2F, 15F, 0F, 0F, "tail1", "body");
        model.addBox(22, 42, false, -2F, 0F, 0F, 4, 4, 7, 0F,
                0F, 20F, 0F, 0F, 0F, 0F, "tail2", "body");
        model.addBox(0, 54, false, -1.5F, -1.5F, 0F, 3, 3, 4, 0F,
                0F, 22F, 6F, 21.3F, 0F, 0F, "tail3", "body");
        model.addBox(14, 54, false, -4F, 0F, 1F, 8, 1, 4, 0F,
                0F, 21F, 7F, 21.3F, 0F, 0F, "tail4", "body");
        model.addBox(30, 12, false, -2F, -1F, -1F, 2, 7, 2, 0F,
                     -3F, 9F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(30, 12, true, 0F, -1F, -1F, 2, 7, 2, 0F,
                     3F, 9F, 0F, 0F, 0F, 0F, "leftarm", null);
//        model.addBox(18, 12, false, -3F, 0F, -1.5F, 3, 8, 3, 0F,
//                     0F, 16F, 0F, 0F, 0F, 0F, "rightleg", null);
//        model.addBox(18, 12, true, 0F, 0F, -1.5F, 3, 8, 3, 0F,
//                     0F, 16F, 0F, 0F, 0F, 0F, "leftleg", null);

        return model;
    }

    /**
     * 人形のレンダータイプを取得する
     */
    @Override
    public EnumDollRenderType getRenderType()
    {
        return EnumDollRenderType.TALL;
    }
}
