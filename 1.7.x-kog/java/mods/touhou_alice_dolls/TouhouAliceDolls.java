////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

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
import mods.touhou_alice_dolls.dolls.*;
import mods.touhou_alice_dolls.AI.*;

/**
 * アリスの人形MOD メインクラス
 */
@Mod(
	    modid = TouhouAliceDolls.MODID,
	    name = TouhouAliceDolls.MODNAME,
	    version = TouhouAliceDolls.VERSION,
	    dependencies = TouhouAliceDolls.DEPENDENCIES
	    )
//@NetworkMod(
//    clientSideRequired = true,
//    serverSideRequired = false
//    )
public class TouhouAliceDolls
{
	/** MODの識別子 */
    public static final String MODID = "touhou_alice_dolls";
    /** MODの名前 */
    public static final String MODNAME = "Alice's Dolls MOD";
    /** MODのバージョン */
    public static final String VERSION = "1.7.x-kog";
    /** MODの依存関係 */
    public static final String DEPENDENCIES = "required-after:touhou_alice_core";
    
    /**
     * MODの唯一のインスタンス
     */
    @Instance("touhou_alice_dolls")
    public static TouhouAliceDolls instance;


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

            EntityDollAIReportEntity.searchRange =
            EntityDollAIAttackEnemy.searchRange =
            EntityDollAIStealItem.searchRange =
                cfg.get("DollAISearchEntity", "searchRange", 16.0D).getDouble(16.0D);

            EntityDollAIReportEntity.searchHeight =
            EntityDollAIAttackEnemy.searchHeight =
            EntityDollAIStealItem.searchHeight =
                cfg.get("DollAISearchEntity", "searchHeight", 8.0D).getDouble(8.0D);

            EntityDollAIReportEntity.searchEntityRegex =
                cfg.get("DollAIReportEntity", "searchEntityRegex", "\\A(?!AliceDoll).*").getString();
            
            EntityDollAIAttackEnemy.targetEntityRegex =
                cfg.get("DollAIAttackEnemy", "targetEntityRegex",
                        "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|LavaSlime").getString();

            EntityDollAIAttackEnemy.attackStrength =
                cfg.get("DollAIAttackEnemy", "attackStrength", 2).getInt();
            
            EntityDollAIExplosion.searchRange =
                cfg.get("DollAIExplosion", "searchRange", 16.0D).getDouble(16.0D);

            EntityDollAIExplosion.searchHeight =
                cfg.get("DollAIExplosion", "searchHeight", 8.0D).getDouble(8.0D);

            EntityDollAIExplosion.targetEntityRegex =
                cfg.get("DollAIExplosion", "targetEntityRegex",
                        "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|Blaze|LavaSlime|Witch").getString();

            EntityDollAIExplosion.explodeStrength =
                (float)(cfg.get("DollAIExplosion", "explodeStrength", 3.0D).getDouble(3.0D));

            EntityDollAIExplosion.mobGriefing = 
                cfg.get("DollAIExplosion", "mobGriefing", true).getBoolean(true);

            EntityDollAICollectItem.searchRange =
                cfg.get("DollAICollectItem", "searchRange", 16.0D).getDouble(16.0D);
            EntityDollAICollectItem.searchHeight =
                cfg.get("DollAICollectItem", "searchHeight", 8.0D).getDouble(8.0D);
            EntityDollAICollectItem.canCollectRange =
                (float)(cfg.get("DollAICollectItem", "canCollectRange", 2.0D).getDouble(2.0D));

            EntityDollAITorcher.lightThreshold =
                cfg.get("DollAITorcher", "lightThreshold", 5).getInt();

            EntityDollAIReportBlock.targetBlockRegex =
            EntityDollAIMineBlock.targetBlockRegex = 
                cfg.get("DollAISearchBlock", "targetBlockRegex", "ore.+|netherquartz").getString();

            EntityDollAIReportBlock.mineRange =
            EntityDollAIMineBlock.mineRange = 
            EntityDollAIQuarry.mineRange =
                cfg.get("DollAISearchBlock", "mineRange", 3).getInt();

            EntityDollAIQuarry.levelingBlockRegex =
                cfg.get("DollAIQuarry", "levelingBlockRegex", "^stone|grass|dirt|sand|gravel|sandstone").getString();

            EntityDollAIMineBlock.mineSpeed =
            EntityDollAIQuarry.mineSpeed =
                cfg.get("DollAIMineBlock", "mineSpeed", 2.5D).getDouble(2.5D);

            EntityDollAICutTree.logBlockRegex =
                cfg.get("DollAICutTree", "logBlockRegex", "log|mushroom").getString();
            
            EntityDollAICutTree.leavesBlockRegex =
                cfg.get("DollAICutTree", "leavesBlockRegex", "leaves|mushroom|vine").getString();
            
            EntityDollAICutTree.cutRange =
                cfg.get("DollAICutTree", "cutRange", 5).getInt();
            
            EntityDollAICutTree.cutSpeed =
                cfg.get("DollAICutTree", "cutSpeed", 2.5D).getDouble(2.5D);
         }
        catch (Exception e)
        {
        	FMLLog.severe("%s Configuration load error!");
        }
        finally
        {
            cfg.save();
        }
        
        // 人形の登録
        DollRegistry.addDoll(1, new DollShanghai());
        DollRegistry.addDoll(2, new DollHorai());
        DollRegistry.addDoll(3, new DollOoedo());
        DollRegistry.addDoll(4, new DollRussia());
        DollRegistry.addDoll(5, new DollGermany());
        DollRegistry.addDoll(6, new DollLondon());
        DollRegistry.addDoll(7, new DollGrandGuignol());
        
    }

    /**
     * 初期化処理
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }
}
