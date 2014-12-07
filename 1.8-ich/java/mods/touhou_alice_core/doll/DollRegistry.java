package mods.touhou_alice_core.doll;

import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_core.client.EnumDollRenderType;
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
	 * 人形の初期化
	 */
	public static void initialize() {
		
    	for(int id = 0; id < getDollListLength(); ++id)
    	{
    		if(dollList[id] != null)
    		{
    			dollList[id].addRecipes();
    		}
    	}
	}
	
    /**
     * 人形を追加する
     * @param id 人形のID
     * @param doll 追加する人形
     * @return 追加に成功したかどうか
     */
    static public boolean addDoll(int id, DollBase doll)
    {
        if(id < 0 || id >= getDollListLength())
        {
            return false;
        }
        if(dollList[id] != null)
        {
            return false;
        }
        dollList[id] = doll;

        return true;
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
	
	@SideOnly(Side.CLIENT)
	public static EnumDollRenderType getRenderType(int id) {
		if(isExist(id)) {
			return dollList[id].getRenderType();
		}
		return dollList[0].getRenderType();
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
		return dollList[0].getDollName();
	}

	public static ItemStack getHeldItem(int id) {
		if(isExist(id)) {
			return dollList[id].getHeldItem();
		}
		return dollList[0].getHeldItem();
	}

	public static float getWidth(int id) {
		if(isExist(id)) {
			return dollList[id].getWidth();
		}
		return dollList[0].getWidth();
	}

	public static float getHeight(int id) {
		if(isExist(id)) {
			return dollList[id].getHeight();
		}
		return dollList[0].getHeight();
	}

	public static double getHealth(int id) {
		if(isExist(id)) {
			return dollList[id].getHealth();
		}
		return dollList[0].getHealth();
	}

	public static double getSpeed(int id) {
		if(isExist(id)) {
			return dollList[id].getSpeed();
		}
		return dollList[0].getSpeed();
	}

	public static void onInitializeAI(int id,
			EntityAliceDoll entityAliceDoll) {
		if(isExist(id)) {
			dollList[id].onInitializeAI(entityAliceDoll);
		}
		else {
			dollList[0].onInitializeAI(entityAliceDoll);
		}
	}

	public static boolean isSlowFall(int id) {
		if(isExist(id)) {
			return dollList[id].isSlowFall();
		}
		return dollList[0].isSlowFall();
	}

	public static boolean isHover(int id) {
		if(isExist(id)) {
			return dollList[id].isHover();
		}
		return dollList[0].isHover();
	}

	public static int getDollID(String dollName) {
    	for(int id = 0; id < getDollListLength(); ++id)
    	{
    		if((dollList[id] != null) && dollList[id].getDollName().equalsIgnoreCase(dollName))
    		{
    			return id;
    		}
    	}
    	
    	return 0;
	}
}
