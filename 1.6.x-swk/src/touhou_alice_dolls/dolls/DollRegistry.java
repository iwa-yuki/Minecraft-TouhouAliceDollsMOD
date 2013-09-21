////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.client.*;
import mods.touhou_alice_dolls.EntityAliceDoll;

/**
 * 人形管理クラス
 */
public class DollRegistry
{
    private static DollBase[] dollList;

    static
    {
        dollList = new DollBase[256];

        dollList[0] = new DollBase();
        dollList[1] = new DollShanghai();
        dollList[2] = new DollHorai();
        dollList[3] = new DollOoedo();
        dollList[4] = new DollRussia();
        dollList[5] = new DollGermany();
        dollList[6] = new DollLondon();
        dollList[11] = new DollMarisa();
        dollList[12] = new DollYuuka();
        dollList[31] = new DollGoliath();
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
     * IDを指定して名前を取得
     * @param id 人形のID
     * @return 人形の名前
     */
    static public String getDollName(int id)
    {
        return isExist(id) ? dollList[id].getDollName() : "unknown";
    }

    /**
     * IDを指定してアイコン名を取得
     * @param id 人形のID
     * @return アイコン名
     */
    static public String getIconName(int id)
    {
        return isExist(id) ? dollList[id].getIconName() : "unknown";
    }

    /**
     * IDを指定してメインテクスチャのパスを取得
     * @param id 人形のID
     * @return メインテクスチャのパス
     */
    static public String getMainTexturePath(int id)
    {
        return isExist(id) ? dollList[id].getMainTexturePath() : null;
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
            dollList[id].getArmorTexturePath(type, slot, var) : null;
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

        return 0.25D;
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
     * 人形のレシピを追加する
     */
    static public void addRecipes()
    {
        for(int i=0;i<getDollListLength();++i)
        {
            if(isExist(i))
            {
                dollList[i].addRecipes();
            }
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
        return isExist(id) ? dollList[id].getSizeInventory() : 9;
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
        return isExist(id) ? dollList[id].getModelInstance(expand) : null;
    }

    @SideOnly(Side.CLIENT)
    /**
     * 人形のレンダータイプを取得する
     * @param id 人形のID
     * @retuen レンダータイプ
     */
    static public EnumRenderType getRenderType(int id)
    {
        return isExist(id) ? dollList[id].getRenderType() : EnumRenderType.BIPED;
    }
}
