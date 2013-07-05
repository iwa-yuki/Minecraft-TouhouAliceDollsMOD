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

/**
 * アリスの人形MOD メインクラス
 */
@Mod(
    modid = "touhou_alice_dolls",
    name = "Alice's Dolls MOD",
    version = "1.6.x-mmj",
    dependencies="after:mod_thKaguya"
    )
@NetworkMod(
    clientSideRequired = true,
    serverSideRequired = false
    )
public class TouhouAliceDolls
{
    @Instance("touhou_alice_dolls")
    public TouhouAliceDolls instance;
    
    @SidedProxy(
        clientSide = "mods.touhou_alice_dolls.client.ClientProxy",
        serverSide = "mods.touhou_alice_dolls.CommonProxy"
        )
    public static CommonProxy proxy;

    @EventHandler
    public void  preInit(FMLPreInitializationEvent event)
    {
    }
    @EventHandler
    public void  init(FMLInitializationEvent event)
    {
        proxy.registerRenderers();
    }
    @EventHandler
    public void  postInit(FMLPostInitializationEvent event)
    {
    }
}
