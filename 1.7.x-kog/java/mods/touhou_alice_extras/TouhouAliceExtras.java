////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_extras;

import cpw.mods.fml.common.Mod;
// import cpw.mods.fml.common.network.NetworkMod;
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
import net.minecraftforge.common.config.Configuration;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_core.dolls.DollRegistry;
import mods.touhou_alice_dolls.TouhouAliceDolls;
import mods.touhou_alice_extras.dolls.*;
import mods.touhou_alice_extras.AI.*;

/**
 * アリスの人形MOD Exクラス
 */
@Mod(
	    modid = TouhouAliceExtras.MODID,
	    name = TouhouAliceExtras.MODNAME,
	    version = TouhouAliceExtras.VERSION,
	    dependencies = TouhouAliceExtras.DEPENDENCIES
    )
//@NetworkMod(
//    clientSideRequired = true,
//    serverSideRequired = false
//    )
public class TouhouAliceExtras
{
	/** MODの識別子 */
    public static final String MODID = "touhou_alice_extras";
    /** MODの名前 */
    public static final String MODNAME = "Alice's Dolls MOD Ex";
    /** MODのバージョン */
    public static final String VERSION = "1.7.x-kog";
    /** MODの依存関係 */
    public static final String DEPENDENCIES = "required-after:touhou_alice_core;after:mod_thKaguya";
    
    /**
     * MODの唯一のインスタンス
     */
    @Instance("touhou_alice_extras")
    public static TouhouAliceExtras instance;


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

            EntityDollAIAttackWithBullet.searchRange =
            EntityDollAIAttackWithAmulet.searchRange =
            EntityDollAIAttackWithGoliath.searchRange =
                cfg.get("DollAISearchEntity", "searchRange", 16.0D).getDouble(16.0D);

            EntityDollAIAttackWithBullet.searchHeight =
            EntityDollAIAttackWithAmulet.searchHeight =
            EntityDollAIAttackWithGoliath.searchHeight =
                cfg.get("DollAISearchEntity", "searchHeight", 8.0D).getDouble(8.0D);
            
            EntityDollAIAttackWithGoliath.targetEntityRegex =
                cfg.get("DollAIAttackEnemy", "targetEntityRegex",
                        "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|Blaze|LavaSlime|Witch").getString();

            EntityDollAIAttackWithBullet.targetEntityRegex =
            EntityDollAIAttackWithAmulet.targetEntityRegex =
                cfg.get("DollAIAttackWithBullet", "targetEntityRegex", "Zombie|Skeleton|Creeper|Spider|Slime|Silverfish|Blaze|Ghast|LavaSlime|THFairy|THPhantom").getString();


            EntityDollAIAttackWithGoliath.attackStrength =
                cfg.get("DollAIAttackEnemy", "attackStrengthGoliath", 7).getInt();

            EntityDollAIFarmer.farmRange = 
                cfg.get("DollAIFarmer", "farmRange", 5).getInt();
         }
        catch (Exception e)
        {
        	FMLLog.severe("Configuration load error!");
        }
        finally
        {
            cfg.save();
        }
        
        // 人形の登録
        DollRegistry.addDoll(11, new DollMarisa());
        DollRegistry.addDoll(12, new DollYuuka());
        DollRegistry.addDoll(13, new DollReimu());
        DollRegistry.addDoll(14, new DollWakasagi());
        DollRegistry.addDoll(31, new DollGoliath());
    }

    /**
     * 初期化処理
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
