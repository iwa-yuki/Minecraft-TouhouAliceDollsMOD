////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mods.touhou_alice_core.client.*;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;

/**
 * 人形管理クラス
 */
public class DollRegistry
{
    private static DollBase[] dollList;

    static
    {
        dollList = new DollBase[256];
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
    
    static public void initialize()
    {
    	for(int id = 0; id < getDollListLength(); ++id)
    	{
    		if(dollList[id] != null)
    		{
    			dollList[id].addRecipes();
    			TouhouAliceCore.proxy.registerDollModel(id);
    		}
    	}
    }

    /**
     * 人形リストの要素数
     */
    static public int getDollListLength()
    {
        return (dollList!=null ? dollList.length : 0);
    }

    /**
     * 指定された名前の人形のIDを取得
     * @param name 人形の名前
     * @return 人形のID(存在しなければ-1)
     */
    static public int getDollID(String name)
    {
        for(int i=0;i<getDollListLength();++i)
        {
            if(isExist(i))
            {
                if(name.equals(getDollName(i)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 指定IDの人形が登録されているかどうか
     * @param id 人形のID
     * @return 人形が登録されて入ればtrue
     */
    static public boolean isExist(int id)
    {
        return (id>=0 && id<getDollListLength() && dollList[id]!=null);
    }

    /**
     * 指定IDの人形が隠しかどうか
     * @param id 人形のID
     * @return 人形が隠しならばtrue
     */
    static public boolean isSecret(int id)
    {
        return isExist(id) ? dollList[id].isSecret() : false;
    }
    
    /**
     * IDを指定して名前を取得
     * @param id 人形のID
     * @return 人形の名前
     */
    static public String getDollName(int id)
    {
        return isExist(id) ? dollList[id].getDollName() : "";
    }

    /**
     * IDを指定してアイコン名を取得
     * @param id 人形のID
     * @return アイコン名
     */
    static public String getIconName(int id)
    {
        return isExist(id) ? dollList[id].getIconName() : "";
    }

    /**
     * IDを指定してメインテクスチャのパスを取得
     * @param id 人形のID
     * @return メインテクスチャのパス
     */
    static public String getMainTexturePath(int id)
    {
        return isExist(id) ? dollList[id].getMainTexturePath() : "textures/entity/steve.png";
    }

    /**
     * IDを指定して防具テクスチャのパスを取得
     * @param id 人形のID
     * @param type 防具の素材
     * @param slot 防具の種類
     * @param var テクスチャのバリエーション
     * @return 防具テクスチャのパス
     */
    static public String getArmorTexturePath(int id, int type, int slot, String var)
    {
        return isExist(id) ?
            dollList[id].getArmorTexturePath(type, slot, var) : "";
    }

    /**
     * 人形の高さを取得
     * @param id 人形のID
     */
    static public float getHeight(int id)
    {
        if(isExist(id))
        {
            return dollList[id].getHeight();
        }

        return 1.8F;
    }

    /**
     * 人形の幅を取得
     * @param id 人形のID
     */
    static public float getWidth(int id)
    {
        if(isExist(id))
        {
            return dollList[id].getWidth();
        }

        return 0.7F;
    }
    
    /**
     * 人形の体力を取得
     * @param id 人形のID
     */
    static public double getHealth(int id)
    {
        if(isExist(id))
        {
            return dollList[id].getHealth();
        }

        return 20.0D;
    }
    
    /**
     * 人形の移動速度を取得
     * @param id 人形のID
     */
    static public double getSpeed(int id)
    {
        if(isExist(id))
        {
            return dollList[id].getSpeed();
        }

        return 0.3D;
    }
    
    /**
     * 人形がふわふわ落下するかどうか
     * @param id 人形のID
     */
    static public boolean isSlowFall(int id)
    {
        if(isExist(id))
        {
            return dollList[id].isSlowFall();
        }

        return true;
    }
    
    /**
     * 人形が浮遊するかどうか
     * @param id 人形のID
     */
    static public boolean isHover(int id)
    {
        if(isExist(id))
        {
            return dollList[id].isHover();
        }

        return true;
    }
    
    /**
     * 人形のAIを初期化する必要があるときに呼ばれる
     * @param id 人形のID
     */
    static public void onInitializeAI(int id, EntityAliceDoll doll)
    {
        if(isExist(id))
        {
            dollList[id].onInitializeAI(doll);
        }
    }

    /**
     * 手持ちアイテムを取得する
     * @param id 人形のID
     * @return アイテムスタック
     */
    static public ItemStack getHeldItem(int id)
    {
        return isExist(id) ? dollList[id].getHeldItem() : null;
    }

    /**
     * インベントリのサイズを取得する
     * @param id 人形のID
     * @return インベントリのサイズ
     */
    static public int getSizeInventory(int id)
    {
        return isExist(id) ? dollList[id].getSizeInventory() : 27;
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のModelを生成する
     * @param id 人形のID
     * @param float Modelの拡張係数
     * @retuen 人形Model
     */
    static public ModelBiped getModelInstance(int id, float expand)
    {
        return isExist(id) ? dollList[id].getModelInstance(expand) : new ModelBiped(expand);
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のレンダータイプを取得する
     * @param id 人形のID
     * @retuen レンダータイプ
     */
    static public EnumDollRenderType getRenderType(int id)
    {
        return isExist(id) ? dollList[id].getRenderType() : EnumDollRenderType.BIPED;
    }
}
