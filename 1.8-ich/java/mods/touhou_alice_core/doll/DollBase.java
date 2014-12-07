package mods.touhou_alice_core.doll;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_core.client.EnumDollRenderType;
import mods.touhou_alice_core.client.ModelAliceDoll;

public class DollBase {

	/**
	 * 人形のメインテクスチャのパスを取得する
	 * @return テクスチャのパス
	 */
	@SideOnly(Side.CLIENT)
	public String getMainTexturePath() {
		return TouhouAliceCore.MODID + ":textures/dolls/bare.png";
	}

	/**
	 * 人形の防具テクスチャのパスを取得する
	 * @param stack 防具ItemStack
	 * @param slot 防具の種類
	 * @param type 防具の材質
	 * @return
	 */
	public String getArmorTexturePath(ItemStack stack, int slot, String type) {
		
      ItemArmor item = (ItemArmor)stack.getItem();
      String s1 = String.format(TouhouAliceCore.MODID + ":textures/dolls/armor/doll/%s_layer_%d%s.png",
              item.getArmorMaterial().func_179242_c(), (slot == 2 ? 2 : 1), type == null ? "" : String.format("_%s", type));

      return s1;
	}
	
	@SideOnly(Side.CLIENT)
	public ModelAliceDoll getModelInstance(float expand) {
		
        ModelAliceDoll model = new ModelAliceDoll(expand, 0.0f, 64, 32);
        
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
        model.addBox(0, 0, false, -2F, -4F, -2F, 4, 4, 4, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "head", null);
        model.addBox(0, 8, false, -2F, 0F, -1F, 4, 5, 2, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "body", null);
        model.addBox(0, 15, false, -2F, -1F, -1F, 2, 4, 2, 0F,
                     -2F, 15F, 0F, 0F, 0F, 0F, "rightarm", null);
        model.addBox(8, 15, false, 0F, -1F, -1F, 2, 4, 2, 0F,
                     2F, 15F, 0F, 0F, 0F, 0F, "leftarm", null);
        model.addBox(0, 21, false, -1F, 0F, -1F, 2, 5, 2, 0F,
                     -1F, 19F, 0F, 0F, 0F, 0F, "rightleg", null);
        model.addBox(8, 21, false, -1F, 0F, -1F, 2, 5, 2, 0F,
                     1F, 19F, 0F, 0F, 0F, 0F, "leftleg", null);
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形の装飾を生成する
     */
    protected void genAccessory(ModelAliceDoll model, float expand)
    {
        model.addBox(36, 0, false, -3F, -1.2F, -0.8F, 6, 3, 1, 0.1F,
                     0F, 10F, 2F, 30F, 0F, 0F, "ribbon", "head");
        model.addBox(16, 0, false, -3F, 4F, -2F, 6, 2, 4, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "skirt1", "body");
        model.addBox(16, 6, false, -4F, 6F, -3F, 8, 2, 6, 0F,
                     0F, 14F, 0F, 0F, 0F, 0F, "skirt2", "body");
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のレンダータイプを取得する
     */
    public EnumDollRenderType getRenderType()
    {
        return EnumDollRenderType.DOLL;
    }

    /**
     * 人形が隠しかどうか
     * @return
     */
	public boolean isSecret() {
		return false;
	}

	/**
	 * 人形の名前を取得する
	 * @return
	 */
	public String getDollName() {
		return "bare";
	}

	/**
	 * 人形の手持ちアイテムを取得する
	 * @return 手持ちアイテム
	 */
	public ItemStack getHeldItem() {
		return null;
	}

	/**
	 * 人形の幅を取得する
	 * @return
	 */
	public float getWidth() {
		return 0.3F;
	}

	/**
	 * 人形の高さを取得する
	 * @return
	 */
	public float getHeight() {
		return 0.7F;
	}

	/**
	 * 人形の体力を取得する
	 * @return
	 */
	public double getHealth() {
		return 8.0D;
	}

	/**
	 * 人形の移動速度を取得する
	 * @return
	 */
	public double getSpeed() {
		return 025D;
	}

	/**
	 * 人形のAIを初期化する
	 * @param entityAliceDoll 人形Entity
	 */
	public void onInitializeAI(EntityAliceDoll entityAliceDoll) {

	}

	/**
	 * レシピを追加する
	 */
	public void addRecipes() {
		
	}

	/**
	 * 人形がふわふわ落下するかどうか
	 * @return
	 */
	public boolean isSlowFall() {
		return true;
	}

	/**
	 * 人形が空中に浮かぶかどうか
	 * @return
	 */
	public boolean isHover() {
		return false;
	}


}
