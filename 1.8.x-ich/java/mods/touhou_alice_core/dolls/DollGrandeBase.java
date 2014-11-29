////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.touhou_alice_core.client.*;
import mods.touhou_alice_core.AI.*;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_core.EntityAliceDoll;

/**
 * 人形のベースクラス(Grande)
 */
public class DollGrandeBase extends DollBase
{
    public DollGrandeBase()
    {
    }
    
    /** 人形の名前 */
    public String getDollName()
    {
        return "baregrande";
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
        return "textures/dolls/baregrande.png";
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
    public void addRecipes()
    {
        GameRegistry.addRecipe(
                new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                              DollRegistry.getDollID(getDollName())),
                " W ",
                "WHW",
                " W ",
                'W', Blocks.wool,
                'H', new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                        DollRegistry.getDollID("baretall")));
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
        return 0.5F;
    }

    /**
     * 人形の体力を取得する
     */
    public double getHealth()
    {
        return 16.0D;
    }
    
    /**
     * 人形の移動速度を取得する
     */
    public double getSpeed()
    {
        return 0.28D;
    }
    
    /**
     * 人形がふわふわ落下するかどうかを取得する
     */
    public boolean isSlowFall()
    {
        return true;
    }
    
    /**
     * 手持ちアイテムを取得する
     */
    public ItemStack getHeldItem()
    {
        return null;
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
        ModelAliceDoll model = new ModelAliceDoll(expand, 0.0f, 64, 64);

        model.setRenderType(getRenderType());

        genBaseModel(model, expand);
        genAccessory(model, expand);

        return model;
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のベースモデルを生成する
     */
    protected void genBaseModel(ModelAliceDoll model, float expand)
    {
        model.addBox(0, 0, false, -3F, -6F, -3F, 6, 6, 6, 0F,
                     0F, 6F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(24, 0, false, -3F, -6F, -3F, 6, 6, 6, 0.375F,
                     0F, 6F, 0F, 0F, 0F, 0F, "headwear", null);
        model.addBox(0, 12, false, -3F, 0F, -1.5F, 6, 8, 3, 0F,
                     0F, 6F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(30, 12, false, -2F, -1F, -1F, 2, 8, 2, 0F,
                     -3F, 7F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(30, 12, true, 0F, -1F, -1F, 2, 8, 2, 0F,
                     3F, 7F, 0F, 0F, 0F, 0F, "leftarm", null);
        model.addBox(18, 12, false, -3F, 0F, -1.5F, 3, 9, 3, 0F,
                     0F, 15F, 0F, 0F, 0F, 0F, "rightleg", null);
        model.addBox(18, 12, true, 0F, 0F, -1.5F, 3, 9, 3, 0F,
                     0F, 15F, 0F, 0F, 0F, 0F, "leftleg", null);
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形の装飾を生成する
     */
    protected void genAccessory(ModelAliceDoll model, float expand)
    {
        model.addBox(0, 24, false, -3F, 1.5F, -1F, 6, 4, 2, 0F,
                0F, 5F, 0F, -20F, 0F, 0F, "chest", "body");
        model.addBox(0, 30, false, -3F, 0F, -2F, 6, 1, 4, 0F,
                0F, 13F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(0, 35, false, -4F, 0F, -3F, 8, 2, 6, 0F,
                0F, 14F, 0F, 0F, 0F, 0F, "skirt2", "body");
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のレンダータイプを取得する
     */
    public EnumDollRenderType getRenderType()
    {
        return EnumDollRenderType.GRANDE;
    }
}
