package mods.touhou_alice_core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
    public TouhouAliceCore instance;
    
    /** 人形のEntityID */
	private int entityAliceDollID;
	
	/** ドールコアアイテム */
	public static Item itemDollCore;
	
	/** 人形アイテム */
	public static Item itemAliceDoll;
    
    /**
     * 
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	LoadConfig(event);
    	registerItems();
    	registerEntities();
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /** 
     * ENtityを登録する
     */
    private void registerEntities() {
		// TODO Auto-generated method stub
		
	}

	/**
     * アイテムを登録する
     */
	private void registerItems() {
		
        // ドールコア
        TouhouAliceCore.itemDollCore = new ItemDollCore();
        GameRegistry.registerItem(TouhouAliceCore.itemDollCore, "dollcore");

        // 人形
//        TouhouAliceCore.itemAliceDoll = new ItemAliceDoll();
//        GameRegistry.registerItem(TouhouAliceCore.itemAliceDoll, "alicedoll", TouhouAliceCore.MODID);
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
                "entity", "EntityAliceDollID", 118).getInt();
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

}
