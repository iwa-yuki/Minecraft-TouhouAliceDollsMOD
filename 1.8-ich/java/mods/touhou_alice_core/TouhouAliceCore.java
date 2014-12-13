package mods.touhou_alice_core;

import mods.touhou_alice_core.chunkloader.DollChunkLoader;
import mods.touhou_alice_core.dolls.DollRegistry;
import mods.touhou_alice_core.gui.GuiHandler;
import mods.touhou_alice_core.packet.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * アリスの人形MOD コアクラス
 * @author iwa_yuki
 *
 */
@Mod(
		modid = TouhouAliceCore.MODID,
		name = TouhouAliceCore.MODNAME,
		version = TouhouAliceCore.VERSION,
		dependencies = TouhouAliceCore.DEPENDENCIES
		)
public class TouhouAliceCore
{
	/** MODの識別子 */
    public static final String MODID = "touhou_alice_core";
    /** MODの名前 */
    public static final String MODNAME = "Alice's Core MOD";
    /** MODのバージョン */
    public static final String VERSION = "1.8-ich";
    /** MODの依存関係 */
    public static final String DEPENDENCIES = "required-after:Forge";

    /**
     * MODの唯一のインスタンス
     */
    @Instance(MODID)
    public static TouhouAliceCore instance;
    
    /**
     * サーバー・クライアントでの処理振り分け用プロキシ
     */
    @SidedProxy(
        clientSide = "mods.touhou_alice_core.client.ClientProxy",
        serverSide = "mods.touhou_alice_core.CommonProxy"
        )
    public static CommonProxy proxy;
    
    
    /** 人形のEntityID */
	private int entityAliceDollID;
	
	/** ドールコアアイテム */
	public static Item itemDollCore;
	
	/** 人形アイテム */
	public static Item itemAliceDoll;

	/** チャンクローダー */
    public DollChunkLoader chunkloader;
	
    

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	LoadConfig(event);
    	registerItems();
    	registerEntities();
        PacketHandler.init();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerItemModel(TouhouAliceCore.itemDollCore, 0, MODID + ":dollcore");
        for(int id = 0; id < DollRegistry.getDollListLength(); ++id) {
        	if(DollRegistry.isExist(id)) {
        		proxy.registerItemModel(TouhouAliceCore.itemAliceDoll, id, MODID + ":alicedoll_" + DollRegistry.getDollName(id));
        	}
        }
    	proxy.registerRenderers();
    	registerRecipes();
        // GuiHandlerの登録
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        // チャンクローダーの初期化
        chunkloader = new DollChunkLoader(this);
        // 人形の初期化
        DollRegistry.initialize();
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
    		event.setCanceled(true);
    	}
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /** 
     * ENtityを登録する
     */
    private void registerEntities() {
        EntityRegistry.registerGlobalEntityID(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID);
        EntityRegistry.registerModEntity(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID,
            this, 128, 2, true);
	}

	/**
     * アイテムを登録する
     */
	private void registerItems() {
		
        // ドールコア
        TouhouAliceCore.itemDollCore = new ItemDollCore();
        GameRegistry.registerItem(TouhouAliceCore.itemDollCore, "dollcore");

        // 人形
        TouhouAliceCore.itemAliceDoll = new ItemAliceDoll();
        GameRegistry.registerItem(TouhouAliceCore.itemAliceDoll, "alicedoll");
	}

    /**
     * コンフィグを読み込む
     */
	private void LoadConfig(FMLPreInitializationEvent event) {
		
        Configuration cfg = new Configuration(
            event.getSuggestedConfigurationFile());
        
        try
        {
            cfg.load();

            this.entityAliceDollID = cfg.get(
                "entity", "EntityAliceDollID", EntityRegistry.findGlobalUniqueEntityId()).getInt();
        }
        catch (Exception e)
        {
        	event.getModLog().error("%s Configuration load error!", e.getLocalizedMessage());
        }
        finally
        {
            cfg.save();
        }
		
	}

	/**
	 * レシピを登録する
	 */
    private void registerRecipes() {
    	
        GameRegistry.addRecipe(new ItemStack(this.itemDollCore),
                "# $",
                "#d$",
                " # ",
                '#', Items.redstone,
                '$', new ItemStack(Items.dye, 1, 4),
                'd', Items.diamond);
	}


}
