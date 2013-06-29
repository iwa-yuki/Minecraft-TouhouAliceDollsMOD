package mods.touhou_alice_dolls;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.lang.reflect.Field;

import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import mods.touhou_alice_dolls.AI.*;

@Mod(modid   = "touhou_alice_dolls",
     name    = "Alice's Dolls MOD",
     version = "1.5.x-aya",
     dependencies="after:mod_thKaguya")
@NetworkMod(clientSideRequired = true,
            serverSideRequired = false)
public class TouhouAliceDolls
{
    @Instance("touhou_alice_dolls")
    public static TouhouAliceDolls instance;

    @SidedProxy(clientSide = "mods.touhou_alice_dolls.client.ClientProxy",
                serverSide = "mods.touhou_alice_dolls.CommonProxy")
    public static CommonProxy proxy;

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration cfg = new Configuration(
            event.getSuggestedConfigurationFile());
        
        try
        {
            cfg.load();
            
            this.entityAliceDollID = cfg.get("entity", "EntityAliceDollID", 68).getInt();
            this.itemDollCoreID = cfg.getItem("DollCoreID", 5000).getInt();
            this.itemShanghaiID = cfg.getItem("ShanghaiID", 5001).getInt();
            this.itemHoraiID = cfg.getItem("HoraiID", 5002).getInt();
            this.itemOoedoID = cfg.getItem("OoedoID", 5003).getInt();
            this.itemRussiaID = cfg.getItem("RussiaID", 5004).getInt();
            this.itemGermanyID = cfg.getItem("GermanyID", 5005).getInt();
            this.itemLondonID = cfg.getItem("LondonID", 5006).getInt();
            this.itemMarisaID = cfg.getItem("MarisaID", 5007).getInt();
            this.itemBareID = cfg.getItem("BareID", 5008).getInt();

            EntityDollAISearchTarget.searchRange =
                cfg.get("DollAISearchTarget", "searchRange", 16.0D).getDouble(16.0D);
            EntityDollAISearchTarget.searchHeight =
                cfg.get("DollAISearchTarget", "searchHeight", 4.0D).getDouble(4.0D);
            EntityDollAISearchTarget.searchEntityRegex =
                cfg.get("DollAISearchTarget", "searchEntityRegex", "\\A(?!AliceDoll).*").getString();
            EntityDollAISearchTarget.targetEntityRegex =
                cfg.get("DollAISearchTarget", "targetEntityRegex", "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|Blaze|LavaSlime|Witch").getString();

            EntityDollAIAttackTarget.targetEntityRegex =
                cfg.get("DollAIAttackTarget", "targetEntityRegex", "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|Blaze|LavaSlime|Witch").getString();
            EntityDollAIAttackTarget.attackStrength =
                cfg.get("DollAIAttackTarget", "attackStrength", 2).getInt();

            EntityDollAIExplode.searchRange =
                cfg.get("DollAIExplode", "searchRange", 16.0D).getDouble(16.0D);
            EntityDollAIExplode.searchHeight =
                cfg.get("DollAIExplode", "searchHeight", 4.0D).getDouble(4.0D);
            EntityDollAIExplode.targetEntityRegex =
                cfg.get("DollAIExplode", "targetEntityRegex", "Zombie|Skeleton|Creeper|Spider|Slime|Enderman|Silverfish|Blaze|LavaSlime|Witch").getString();
            EntityDollAIExplode.explodeStrength =
                (float)(cfg.get("DollAIExplode", "explodeStrength", 3.0D).getDouble(3.0D));
            EntityDollAIExplode.mobGriefing = 
                cfg.get("DollAIExplode", "mobGriefing", true).getBoolean(true);

            EntityDollAICollectItem.searchRange =
                cfg.get("DollAICollectItem", "searchRange", 16.0D).getDouble(16.0D);
            EntityDollAICollectItem.searchHeight =
                cfg.get("DollAICollectItem", "searchHeight", 4.0D).getDouble(4.0D);
            EntityDollAICollectItem.canCollectRange =
                (float)(cfg.get("DollAICollectItem", "canCollectRange", 2.0D).getDouble(2.0D));

            EntityDollAITorcher.lightThreshold =
                cfg.get("DollAITorcher", "lightThreshold", 5).getInt();

            EntityDollAISearchBlock.targetBlockRegex =
                cfg.get("DollAISearchBlock", "targetBlockRegex", "ore.+|netherquartz").getString();
            EntityDollAISearchBlock.mineRange =
            EntityDollAILevelingBlock.mineRange =
                cfg.get("DollAISearchBlock", "mineRange", 3).getInt();

            EntityDollAILevelingBlock.levelingBlockRegex =
                cfg.get("DollAILevelingBlock", "levelingBlockRegex", "stone|grass|dirt|sand|gravel|sandstone").getString();

            EntityDollAIMineBlock.mineSpeed = 
                cfg.get("DollAIMineBlock", "mineSpeed", 2.5D).getDouble(2.5D);

            EntityDollAICutTree.logBlockRegex =
                cfg.get("DollAICutTree", "logBlockRegex", "log|mushroom").getString();
            EntityDollAICutTree.leavesBlockRegex =
                cfg.get("DollAICutTree", "leavesBlockRegex", "leaves|mushroom|vine").getString();
            EntityDollAICutTree.cutRange =
                cfg.get("DollAICutTree", "cutRange", 5).getInt();
            EntityDollAICutTree.cutSpeed =
                cfg.get("DollAICutTree", "cutSpeed", 2.5D).getDouble(2.5D);
            DollRegistry.isBipedModel =
                cfg.get("DollRenderer", "isBipedModel", false).getBoolean(false);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Configuration load error!");
        }
        finally
        {
            cfg.save();
        }
    }

    @Init
    public void load(FMLInitializationEvent event)
    {
        // エンティティの登録
        EntityRegistry.registerGlobalEntityID(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID);
        EntityRegistry.registerModEntity(
            EntityAliceDoll.class, "AliceDoll", this.entityAliceDollID,
            this, 64, 1, true);

        // ドールコア
        itemDollCore = new ItemDollCore(itemDollCoreID)
            .setUnlocalizedName("dollcore");
        LanguageRegistry.instance().addNameForObject(
            itemDollCore, "en_US", "Doll Core");
        LanguageRegistry.instance().addNameForObject(
            itemDollCore, "ja_JP", "ドールコア");
        GameRegistry.addRecipe(new ItemStack(this.itemDollCore),
                               "# $",
                               "#d$",
                               " # ",
                               '#', new ItemStack(Item.redstone),
                               '$', new ItemStack(Item.dyePowder, 1, 4),
                               'd', new ItemStack(Item.diamond));

        // 上海人形
        itemShanghai = new ItemDoll(itemShanghaiID)
            .setUnlocalizedName("shanghai");
        LanguageRegistry.instance().addNameForObject(
            itemShanghai, "en_US", "Shanghai Doll");
        LanguageRegistry.instance().addNameForObject(
            itemShanghai, "ja_JP", "上海人形");
        DollRegistry.registerDoll(
            itemShanghaiID + 256, "Shanghai",
            "shanghai.png",
            new ItemStack(Item.swordGold), 9);
        GameRegistry.addRecipe(new ItemStack(this.itemShanghai),
                               "SW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'S', new ItemStack(Item.swordGold));

        // 蓬莱人形
        itemHorai = new ItemDoll(itemHoraiID)
            .setUnlocalizedName("horai");
        LanguageRegistry.instance().addNameForObject(
            itemHorai, "en_US", "Horai Doll");
        LanguageRegistry.instance().addNameForObject(
            itemHorai, "ja_JP", "蓬莱人形");
        DollRegistry.registerDoll(
            itemHoraiID + 256, "Horai",
            "horai.png",
            new ItemStack(Item.pickaxeGold), 9);
        GameRegistry.addRecipe(new ItemStack(this.itemHorai),
                               "PW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'P', new ItemStack(Item.pickaxeGold));

        // 大江戸人形
        itemOoedo = new ItemDoll(itemOoedoID)
            .setUnlocalizedName("ooedo");
        LanguageRegistry.instance().addNameForObject(
            itemOoedo, "en_US", "Ooedo Doll");
        LanguageRegistry.instance().addNameForObject(
            itemOoedo, "ja_JP", "大江戸人形");
        DollRegistry.registerDoll(
            itemOoedoID + 256, "Ooedo",
            "ooedo.png",
            new ItemStack(Block.tnt), 9);
        GameRegistry.addRecipe(new ItemStack(this.itemOoedo),
                               "TW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'T', new ItemStack(Block.tnt));

        // 露西亜人形
        itemRussia = new ItemDoll(itemRussiaID)
            .setUnlocalizedName("russia");
        LanguageRegistry.instance().addNameForObject(
            itemRussia, "en_US", "Russia Doll");
        LanguageRegistry.instance().addNameForObject(
            itemRussia, "ja_JP", "露西亜人形");
        DollRegistry.registerDoll(
            itemRussiaID + 256, "Russia",
            "russia.png",
            new ItemStack(Block.chest), 36);
        GameRegistry.addRecipe(new ItemStack(this.itemRussia),
                               "CW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'C', new ItemStack(Block.chest));

        // 独逸人形
        itemGermany = new ItemDoll(itemGermanyID)
            .setUnlocalizedName("germany");
        LanguageRegistry.instance().addNameForObject(
            itemGermany, "en_US", "Germany Doll");
        LanguageRegistry.instance().addNameForObject(
            itemGermany, "ja_JP", "独逸人形");
        DollRegistry.registerDoll(
            itemGermanyID + 256, "Germany",
            "germany.png",
            new ItemStack(Item.axeGold), 9);
        GameRegistry.addRecipe(new ItemStack(this.itemGermany),
                               "AW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'A', new ItemStack(Item.axeGold));

        // 倫敦人形
        itemLondon = new ItemDoll(itemLondonID)
            .setUnlocalizedName("london");
        LanguageRegistry.instance().addNameForObject(
            itemLondon, "en_US", "London Doll");
        LanguageRegistry.instance().addNameForObject(
            itemLondon, "ja_JP", "倫敦人形");
        DollRegistry.registerDoll(
            itemLondonID + 256, "London",
            "london.png",
            new ItemStack(Item.eyeOfEnder), 9);
        GameRegistry.addRecipe(new ItemStack(this.itemLondon),
                               "EW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'E', new ItemStack(Item.eyeOfEnder));

        // 魔理沙人形
        itemMarisa = new ItemDoll(itemMarisaID)
            .setUnlocalizedName("marisa");
        LanguageRegistry.instance().addNameForObject(
            itemMarisa, "en_US", "Marisa Doll");
        LanguageRegistry.instance().addNameForObject(
            itemMarisa, "ja_JP", "魔理沙人形");
        // omake
        ItemStack mrsHeldItem = new ItemStack(Block.mushroomRed);
        {
            try
            {
                Class<?> c = Class.forName(
                    "net.minecraft.thKaguyaMod.mod_thKaguya");
                Field f = c.getField("hakkeroItem");
                Object obj = f.get(null);
                mrsHeldItem = new ItemStack((Item)obj);
                FMLLog.log(Level.INFO,"Found thKaguya MOD!");
            }
            catch(Exception e)
            {
                FMLLog.log(Level.INFO, e, "Not found thKaguya MOD.");                
            }
        }
        DollRegistry.registerDoll(
            itemMarisaID + 256, "Marisa",
            "mrs.png",
            mrsHeldItem, 9);
        GameRegistry.addRecipe(new ItemStack(this.itemMarisa),
                               "MW ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore),
                               'M', new ItemStack(Block.mushroomRed));

        // 素体人形
        itemBare = new ItemDoll(itemBareID)
            .setUnlocalizedName("bare");
        LanguageRegistry.instance().addNameForObject(
            itemBare, "en_US", "Bare Doll");
        LanguageRegistry.instance().addNameForObject(
            itemBare, "ja_JP", "素体人形");
        DollRegistry.registerDoll(
            itemBareID + 256, "Bare",
            "bare.png",
            null, 9);
        GameRegistry.addRecipe(new ItemStack(this.itemBare),
                               " W ",
                               "WHW",
                               " W ",
                               'W', new ItemStack(Block.cloth),
                               'H', new ItemStack(this.itemDollCore));

        // 人形のAIの登録
        DollRegistry.registerAI(0, "EntityDollAISwimming");
        DollRegistry.registerAI(1, "EntityDollAISearchTarget");
        DollRegistry.registerAI(2, "EntityDollAIAttackTarget");
        DollRegistry.registerAI(3, "EntityDollAIStealItem");
        DollRegistry.registerAI(4, "EntityDollAIExplode");
        DollRegistry.registerAI(5, "EntityDollAICollectItem");
        DollRegistry.registerAI(6, "EntityDollAITorcher");
        DollRegistry.registerAI(7, "EntityDollAISearchBlock");
        DollRegistry.registerAI(8, "EntityDollAILevelingBlock");
        DollRegistry.registerAI(9, "EntityDollAIMineBlock");
        DollRegistry.registerAI(10, "EntityDollAICutTree");
        DollRegistry.registerAI(11, "EntityDollAISearchSpawner");
        DollRegistry.registerAI(12, "EntityDollAIFollowOwner");
        DollRegistry.registerAI(13, "EntityDollAIWander");
        DollRegistry.registerAI(14, "EntityDollAIWatchOwner");
        DollRegistry.registerAI(15, "EntityDollAIWatchClosest");
        DollRegistry.registerAI(16, "EntityDollAILookIdle");

        // テクスチャとレンダラの登録
        proxy.registerTextures();
        proxy.registerRenderers(DollRegistry.isBipedModel);
    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    public int entityAliceDollID;

    public Item itemDollCore;
    public Item itemShanghai;
    public Item itemHorai;
    public Item itemOoedo;
    public Item itemRussia;
    public Item itemGermany;
    public Item itemLondon;
    public Item itemMarisa;
    public Item itemBare;

    public int itemDollCoreID;
    public int itemShanghaiID;
    public int itemHoraiID;
    public int itemOoedoID;
    public int itemRussiaID;
    public int itemGermanyID;
    public int itemLondonID;
    public int itemMarisaID;
    public int itemBareID;
}
