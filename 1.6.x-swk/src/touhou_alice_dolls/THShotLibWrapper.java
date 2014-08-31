////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.FMLLog;

import java.lang.reflect.Method;

public class THShotLibWrapper
{
    private static THShotLibWrapper instance = new THShotLibWrapper();

    /**
     * singleton class
     */
    private THShotLibWrapper() {}

    /**
     * インスタンスを取得
     */
    public static THShotLibWrapper getInstance()
    {
        return instance;
    }

    /**
     * ラッパークラスが利用可能かどうか
     */
    public static boolean isEnable()
    {
        return instance.getClazz() != null;
    }

    private Class<?> clazz = null;

    private Class<?> getClazz()
    {
        if(clazz == null)
        {
            try
            {
                clazz = Class.forName("net.minecraft.thKaguyaMod.thShotLib");
                FMLLog.info("Found \"thKaguya.thShotLib\"!");
            }
            catch(Exception e)
            {
            }
        }
        return clazz;
    }
    
    private boolean callMethod(String methodName,
                                 Class[] argsClass, Object[] args)
    {
        if(!isEnable())
        {
            return false;
        }
        boolean result = false;
        try
        {
            Method m = clazz.getMethod(methodName, argsClass);
            m.invoke(null, args);
            result = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * 五つの難題MOD+の弾幕生成ライブラリを利用してN-way弾を発射
     */
    public boolean createWideShot01(EntityLivingBase user,
                                 float angleXZ,
                                 float angleY,
                                 double speed,
                                 int shotType,
                                 int way,
                                 float wideAngle)
    {
        Class[] argsClass = {EntityLivingBase.class,
                             float.class,
                             float.class,
                             double.class,
                             int.class,
                             int.class,
                             float.class};
        Object[] args = {user,
                         new Float(angleXZ),
                         new Float(angleY),
                         new Double(speed),
                         new Integer(shotType),
                         new Integer(way),
                         new Float(wideAngle)};
        
        return this.callMethod("createWideShot01", argsClass, args);
    }
}
