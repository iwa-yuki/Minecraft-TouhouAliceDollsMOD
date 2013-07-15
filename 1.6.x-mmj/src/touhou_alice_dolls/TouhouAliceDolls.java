////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.SidedProxy;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;

import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.Configuration;

import mods.touhou_alice_dolls.dolls.DollRegistry;

/**
 * アリスの人形MOD メインクラス
 */
@Mod(
    modid = "touhou_alice_dolls",
    name = "Alice's Dolls MOD",
    version = "1.6.x-mmj",
    dependencies="required-after:FML;after:mod_thKaguya"
    )
@NetworkMod(
    clientSideRequired = true,
    serverSideRequired = false
    )
public class TouhouAliceDolls
{
    /**
     * MODの唯一のインスタンス
     */
    @Instance("touhou_alice_dolls")
    public static TouhouAliceDolls instance;

    /**
     * サーバー・クライアントでの処理振り分け用プロキシ
     */
    @SidedProxy(
        clientSide = "mods.touhou_alice_dolls.client.ClientProxy",
        serverSide = "mods.touhou_alice_dolls.CommonProxy"
        )
    public static CommonProxy proxy;

    /**
     * 初期化前処理
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // コンフィグ読み込み
        Configuration cfg = new Configuration(
            event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();

            this.entityAliceDollID = cfg.get(
                "entity", "EntityAliceDollID", 68).getInt();
            this.itemDollCoreID = cfg.getItem(
                "itemDollCoreID", 5000).getInt();
            this.itemAliceDollID = cfg.getItem(
                "itemAliceDollID", 5001).getInt();
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Configuration load error!");
        }
        finally
        {
            cfg.save();
        }
        
        // アイテムの登録
        registerItems();
    }

    /**
     * 初期化処理
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // エンティティの登録
        EntityRegistry.registerGlobalEntityID(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID);
        EntityRegistry.registerModEntity(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID,
            this, 64, 1, true);

        // レンダラの登録
        proxy.registerRenderers();

        // レシピの登録
        registerRecipes();
    }

    /**
     * 初期化後処理
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * アイテムの登録
     */
    private void registerItems()
    {
        // ドールコア
        this.itemDollCore = new ItemDollCore(this.itemDollCoreID);
        LanguageRegistry.instance().addNameForObject(
            itemDollCore, "en_US", "Doll Core");
        LanguageRegistry.instance().addNameForObject(
            itemDollCore, "ja_JP", "ドールコア");

        // 人形
        this.itemAliceDoll = new ItemAliceDoll(this.itemAliceDollID);
        LanguageRegistry.instance().addNameForObject(
            itemAliceDoll, "en_US", "Alice's Doll");
        LanguageRegistry.instance().addNameForObject(
            itemAliceDoll, "ja_JP", "アリスの人形");
    }

    /**
     * レシピの登録
     */
    private void registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(this.itemDollCore),
                               "# $",
                               "#d$",
                               " # ",
                               '#', new ItemStack(Item.redstone),
                               '$', new ItemStack(Item.dyePowder, 1, 4),
                               'd', new ItemStack(Item.diamond));

        DollRegistry.addRecipes();
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * 人形のエンティティID
     */
    private int entityAliceDollID;

    /**
     * ドールコアのアイテムID
     */
    private int itemDollCoreID;

    /**
     * 人形のアイテムID
     */
    private int itemAliceDollID;

    /**
     * ドールコアアイテム
     */
    public Item itemDollCore;

    /**
     * 人形アイテム
     */
    public Item itemAliceDoll;
}
