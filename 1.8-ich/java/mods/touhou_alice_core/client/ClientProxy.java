package mods.touhou_alice_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import mods.touhou_alice_core.CommonProxy;

public class ClientProxy extends CommonProxy {
    /**
     * レンダラの登録
     */
	@Override
    public void registerRenderers()
    {
        // 何もしない
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
    public void registerItemModel(Item item, String source)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(source, "inventory"));
    }
}
