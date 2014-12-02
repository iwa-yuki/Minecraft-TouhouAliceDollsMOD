package mods.touhou_alice_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import mods.touhou_alice_core.CommonProxy;
import mods.touhou_alice_core.EntityAliceDoll;

public class ClientProxy extends CommonProxy {
    /**
     * レンダラの登録
     */
	@Override
    public void registerRenderers()
    {
        RenderAliceDoll renderer = new RenderAliceDoll(Minecraft.getMinecraft().getRenderManager());
		RenderingRegistry.registerEntityRenderingHandler(EntityAliceDoll.class, renderer);
    }

    /**
     * 人形モデルの登録
     */
	@Override
    public void registerDollModel(int id)
    {
        // 何もしない
    }

    /**
     * アイテムモデルの登録
     */
	@Override
    public void registerItemModel(Item item, int meta, String source)
    {
		ModelBakery.addVariantName(item, item.getUnlocalizedName(), source);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(source, "inventory"));
    }
	
}
