package mods.touhou_alice_core.packet;

import mods.touhou_alice_core.TouhouAliceCore;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	
    public static final SimpleNetworkWrapper INSTANCE =
    		NetworkRegistry.INSTANCE.newSimpleChannel(TouhouAliceCore.MODID);
    
    public static void init() {
        INSTANCE.registerMessage(MessageAyaShotHandler.class, MessageAyaShot.class, 0, Side.CLIENT);
    }
}
