////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.common.FMLLog;

import java.util.logging.Level;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import mods.touhou_alice_core.dolls.*;
import mods.touhou_alice_core.gui.GuiHandler;
import mods.touhou_alice_core.packet.PacketHandler;
import mods.touhou_alice_core.AI.*;
import mods.touhou_alice_core.chunkloader.*;

/**
 * アリスの人形MOD コアクラス
 */
@Mod(
    modid = TouhouAliceCore.MODID,
    name = TouhouAliceCore.MODNAME,
    version = TouhouAliceCore.VERSION
    )
public class TouhouAliceCore
{
	/** MODの識別子 */
    public static final String MODID = "touhou_alice_core";
    /** MODの名前 */
    public static final String MODNAME = "Alice's Core MOD";
    /** MODのバージョン */
    public static final String VERSION = "1.7.x-kog";

    /**
     * MODの唯一のインスタンス
     */
    @Instance("touhou_alice_core")
    public static TouhouAliceCore instance;

    /**
     * サーバー・クライアントでの処理振り分け用プロキシ
     */
    @SidedProxy(
        clientSide = "mods.touhou_alice_core.client.ClientProxy",
        serverSide = "mods.touhou_alice_core.CommonProxy"
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
                "entity", "EntityAliceDollID", 118).getInt();
        }
        catch (Exception e)
        {
            FMLLog.severe("%s Configuration load error!");
        }
        finally
        {
            cfg.save();
        }

        // 素体人形の登録
        DollRegistry.addDoll(0, new DollBase());
        DollRegistry.addDoll(10, new DollShortBase());
        DollRegistry.addDoll(20, new DollTallBase());
        DollRegistry.addDoll(30, new DollGrandeBase());
        
        // アイテムの登録
        registerItems();
        
        // パケットハンドラの初期化
        PacketHandler.init();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void renderFirstPersonHand(RenderHandEvent event) {
    	Minecraft mc = Minecraft.getMinecraft();
    	if(mc.func_175606_aa() instanceof EntityAliceDoll)
    	{
    		// 人形がスクリーンショットを撮るときはプレイヤーの手を描画しない
    		event.setCanceled(true);
    	}
    }

    /**
     * 初期化処理
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // エンティティの登録
        registerEntities();

        // レンダラの登録
        proxy.registerRenderers();

        // レシピの登録
        registerRecipes();
        
        // GuiHandlerの登録
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        // チャンクローダーの初期化
        chunkloader = new DollChunkLoader(this);
        
        // 人形の初期化
        DollRegistry.initialize();
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * アイテムの登録
     */
    private void registerItems()
    {
        // ドールコア
        TouhouAliceCore.itemDollCore = new ItemDollCore();
        GameRegistry.registerItem(TouhouAliceCore.itemDollCore, "dollcore", TouhouAliceCore.MODID);

        // 人形
        TouhouAliceCore.itemAliceDoll = new ItemAliceDoll();
        GameRegistry.registerItem(TouhouAliceCore.itemAliceDoll, "alicedoll", TouhouAliceCore.MODID);
    }
    
    /**
     * エンティティの登録
     */
    private void registerEntities()
    {
        EntityRegistry.registerGlobalEntityID(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID);
        EntityRegistry.registerModEntity(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID,
            this, 128, 2, true);
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
                               '#', Items.redstone,
                               '$', new ItemStack(Items.dye, 1, 4),
                               'd', Items.diamond);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * 人形のエンティティID
     */
    private int entityAliceDollID;

    /**
     * ドールコアアイテム
     */
    public static Item itemDollCore;

    /**
     * 人形アイテム
     */
    public static Item itemAliceDoll;

    /**
     * チャンクローダー
     */
    public DollChunkLoader chunkloader;
}
