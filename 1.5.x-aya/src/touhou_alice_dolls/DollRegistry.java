package mods.touhou_alice_dolls;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import mods.touhou_alice_dolls.AI.EntityDollAIBase;

// 人形リスト
public class DollRegistry
{
    private static Map dollIDToDollName = new HashMap();
    private static Map dollNameToDollID = new HashMap();
    private static Map dollIDToTexture = new HashMap();
    private static Map dollIDToHeldItem = new HashMap();
    private static Map dollIDToInventorySize = new HashMap();
    private static Map dollAIList = new HashMap();
    public static boolean isBipedModel = false;

    // 人形リストに新しい人形を追加
    public static void registerDoll(
        int dollID, String dollName, String tex, ItemStack heldItem, int invSize)
    {
        dollIDToDollName.put(Integer.valueOf(dollID), dollName);
        dollNameToDollID.put(dollName, Integer.valueOf(dollID));
        dollIDToTexture.put(Integer.valueOf(dollID), tex);
        dollIDToHeldItem.put(Integer.valueOf(dollID), heldItem);
        dollIDToInventorySize.put(Integer.valueOf(dollID), invSize);
        // System.out.println("DollRegistry.registerDoll("+dollID+","+dollName+")");
    }

    // 人形が登録されているか
    public static boolean isDollRegistered(int dollID)
    {
        return dollIDToDollName.containsKey(dollID);
    }

    // 人形の名前を取得
    public static String getName(int dollID)
    {
        Object name = dollIDToDollName.get(dollID);
        return name != null ? (String)name : "unknown";
    }

    public static int getDollID(String name)
    {
        Object obj = dollNameToDollID.get(name);
        if(obj != null)
        {
            int id = (Integer)obj;
            return id;
        }
        return -1;
    }

    // テクスチャを取得
    public static String getTexture(int dollID)
    {
        Object tex = dollIDToTexture.get(dollID);
        return tex != null ? (getTextureCurrentPath() + (String)tex)
            : (getTextureCurrentPath() + "bare.png");
    }

    public static String getTextureCurrentPath()
    {
        if(isBipedModel)
        {
            return "/mods/touhou_alice_dolls/dolls/biped/";
        }
        else
        {
            return "/mods/touhou_alice_dolls/dolls/";
        }
    }

    // 手持ちアイテムを取得
    public static ItemStack getHeldItem(int dollID)
    {
        Object item = dollIDToHeldItem.get(dollID);
        if(item != null)
        {
            return ((ItemStack)item).copy();
        }
        return null;
    }

    // インベントリのサイズを取得
    public static int getInventorySize(int dollID)
    {
        Object invSize = dollIDToInventorySize.get(dollID);
        if(invSize != null)
        {
            int size = (Integer)invSize;
            return size;
        }
        return 0;
    }

    // AIを登録
    public static void registerAI(int index, String className)
    {
        try
        {
            Class clazz = Class.forName("mods.touhou_alice_dolls.AI."+className);
            dollAIList.put(Integer.valueOf(index), clazz);
        }
        catch (Exception e)
        {
            throw new LoaderException(e);
        }
    }

    // AIを人形に適用
    public static void initTasks(EntityAliceDoll doll)
    {
        try
        {
            Iterator it = dollAIList.keySet().iterator();
            while (it.hasNext())
            {
                Object key = it.next();
                Integer index = (Integer)key;
                Class clazz = (Class)(dollAIList.get(key));
                Constructor ct = clazz.getConstructor(EntityAliceDoll.class);
                EntityDollAIBase ai = (EntityDollAIBase)(ct.newInstance(doll));

                doll.tasks.addTask(index, ai);
            }
        }
        catch (Exception e)
        {
            throw new LoaderException(e);
        }
    }
}
