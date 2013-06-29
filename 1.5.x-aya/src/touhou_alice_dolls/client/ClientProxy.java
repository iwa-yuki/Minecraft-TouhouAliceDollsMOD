package mods.touhou_alice_dolls.client;

import mods.touhou_alice_dolls.CommonProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mods.touhou_alice_dolls.EntityAliceDoll;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{
    @Override
	public void registerTextures()
	{
	}

    @Override
    public void registerRenderers(boolean isBiped)
    {
        if(isBiped)
        {
            RenderingRegistry.registerEntityRenderingHandler(
                EntityAliceDoll.class, new RenderAliceDollBiped());
        }
        else
        {
            RenderingRegistry.registerEntityRenderingHandler(
                EntityAliceDoll.class, new RenderAliceDoll());
        }
    }
}
