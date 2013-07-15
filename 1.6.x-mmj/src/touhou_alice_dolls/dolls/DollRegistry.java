////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.dolls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBiped;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.client.*;

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
     * @param type 防具の種類
     * @param layer 防具のレイヤー
     * @return 防具テクスチャのパス
     */
    static public String getMainTexturePath(int id, String type, int layer)
    {
        return isExist(id) ?
            dollList[id].getArmorTexturePath(type, layer) : null;
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
     * 人形のAIを初期化する必要があるときに呼ばれる
     * @param id 人形のID
     */
    static public void onInitializeAI(int id)
    {
        if(isExist(id))
        {
            dollList[id].onInitializeAI();
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
}
