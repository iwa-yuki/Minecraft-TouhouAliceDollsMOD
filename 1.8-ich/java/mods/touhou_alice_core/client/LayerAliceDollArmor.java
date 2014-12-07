package mods.touhou_alice_core.client;

import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.doll.DollRegistry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerAliceDollArmor extends LayerBipedArmor {

	private RenderAliceDoll renderer;
	
	public LayerAliceDollArmor(RenderAliceDoll renderer) {
		super(renderer);

		this.renderer = renderer;
	}

	@Override
    public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
        this.field_177186_d = renderer.getCurrentArmorModelLayer1();
        this.field_177189_c = renderer.getCurrentArmorModelLayer2();
		
		super.doRenderLayer(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }
	
	@Override
    public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, int slot, String type)
    {
//        ItemArmor item = (ItemArmor)stack.getItem();
//        String s1 = String.format("textures/models/armor/%s_layer_%d%s.png",
//                ((ItemArmor)stack.getItem()).getArmorMaterial().func_179242_c(), (slot == 2 ? 2 : 1), type == null ? "" : String.format("_%s", type));
//
//        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
//        ResourceLocation resourcelocation = (ResourceLocation)field_177191_j.get(s1);
//
//        if (resourcelocation == null)
//        {
//            resourcelocation = new ResourceLocation(s1);
//            field_177191_j.put(s1, resourcelocation);
//        }

		int id = ((EntityAliceDoll)entity).getDollID();
        return this.renderer.getResourceLocationFromPath(DollRegistry.getArmorTexturePath(id, stack, slot, type));
    }
}
