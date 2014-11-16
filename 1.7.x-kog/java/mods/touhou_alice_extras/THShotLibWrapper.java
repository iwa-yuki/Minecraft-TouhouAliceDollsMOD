////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_extras;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.FMLLog;

import java.lang.reflect.Method;

/**
 * 五つの難題MOD+の弾幕生成ライブラリのラッパークラス
 */
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
        return (instance.getClazzTHShotLib() != null) && (instance.getClazzShotData() != null);
    }

    private Class<?> clazzTHShotLib = null;
    private Class<?> clazzShotData = null;

    private Class<?> getClazzTHShotLib()
    {
        if(clazzTHShotLib == null)
        {
            try
            {
            	clazzTHShotLib = Class.forName("thKaguyaMod.THShotLib");
                FMLLog.info("Found \"thKaguyaMod.THShotLib\"!");
            }
            catch(Exception e)
            {
            }
        }
        return clazzTHShotLib;
    }

    private Class<?> getClazzShotData()
    {
        if(clazzShotData == null)
        {
            try
            {
            	clazzShotData = Class.forName("thKaguyaMod.ShotData");
                FMLLog.info("Found \"thKaguyaMod.ShotData\"!");
            }
            catch(Exception e)
            {
            }
        }
        return clazzShotData;
    }
    
    private boolean callTHShotLibMethod(String methodName,
                                 Class[] argsClass, Object[] args)
    {
        if(!isEnable())
        {
            return false;
        }
        boolean result = false;
        try
        {
            Method m = clazzTHShotLib.getMethod(methodName, argsClass);
            m.invoke(null, args);
            result = true;
        }
        catch(Exception e)
        {
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ShotData
    
    public Object createShotData(int form, int color, float size, float damage, int delay, int end, int special)
    {
    	if(!isEnable())
    	{
    		return null;
    	}
        Object result = null;
        try
        {
            Method m = clazzShotData.getMethod("shot", int.class, int.class, float.class, float.class, int.class, int.class, int.class);
            result = m.invoke(null, form, color, size, damage, delay, end, special);
        }
        catch(Exception e)
        {
        }
    	
    	return result;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // THShotLib

    /**
     * 五つの難題MOD+の弾幕生成ライブラリを利用してN-way弾を発射
     */
    public boolean createWideShot(EntityLivingBase user,
    							  Vec3 pos, 
    							  Vec3 angle,
    							  double speed, 
    							  Object shot,
    							  int way, 
    							  float wideAngle)
    {
        boolean result = false;
        {
            Class[] argsClass = {EntityLivingBase.class,
                                 Vec3.class,
                                 Vec3.class,
                                 double.class,
                                 clazzShotData,
                                 int.class,
                                 float.class};
            Object[] args = {user,
                             pos,
                             angle,
                             new Double(speed),
                             shot,
                             new Integer(way),
                             new Float(wideAngle)};
        
            result = this.callTHShotLibMethod("createWideShot", argsClass, args);
        }
        return result;
    }
    
    /**
     * 弾幕の発射音を鳴らす
     * @param user　弾源となるEntity
     * @return メソッドの呼び出しが成功したかどうか
     */
    public boolean playShotSound(Entity user)
    {
        boolean result = false;
        {
            Class[] argsClass = {Entity.class};
            Object[] args = {user};
        
            result = this.callTHShotLibMethod("playShotSound", argsClass, args);
        }
        return result;
    }
}
