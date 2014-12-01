package mods.touhou_alice_core.doll;

import mods.touhou_alice_core.client.ModelAliceDoll;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DollRegistry {
	
	private static final DollBase[] dollList = new DollBase[256];
	
	static {
		dollList[0] = new DollBase();
	}

	/**
	 * 人形が登録されているかどうか
	 * @param id 人形ID
	 * @return 登録されていればtrue
	 */
	public static boolean isExist(int id) {		
		return (id>=0) && (id<dollList.length) && (dollList[id] != null);
	}

	/**
	 * 人形のメインテクスチャを取得する
	 * @param id 人形ID
	 * @return メインテクスチャのパス
	 */
	@SideOnly(Side.CLIENT)
	public static String getMainTexturePath(int id) {
		
		if(isExist(id)) {
			return dollList[id].getMainTexturePath();
		}
		
		return dollList[0].getMainTexturePath();
	}
	
	/**
	 * 人形の防具テクスチャを取得する
	 * @param id 人形ID
	 * @param stack 防具のItemStack
	 * @param slot 防具の種類
	 * @param type 防具の材質
	 * @return 防具テクスチャのパス
	 */
	@SideOnly(Side.CLIENT)
	public static String getArmorTexturePath(int id, ItemStack stack, int slot, String type) {
		
		if(isExist(id)) {
			return dollList[id].getArmorTexturePath(stack, slot, type);
		}
		
		return dollList[0].getArmorTexturePath(stack, slot, type);
	}
	public static ModelAliceDoll getModelInstance(int id, float expand) {

		if(isExist(id)) {
			return dollList[id].getModelInstance(expand);
		}
		
		return dollList[0].getModelInstance(expand);
	}

	public static int getDollListLength() {
		return dollList.length;
	}

	public static boolean isSecret(int id) {
		if(isExist(id)) {
			return dollList[id].isSecret();
		}
		return false;
	}

	public static String getDollName(int id) {
		if(isExist(id)) {
			return dollList[id].getDollName();
		}
		return "bare";
	}
}
