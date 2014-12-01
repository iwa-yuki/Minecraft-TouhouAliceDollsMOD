package mods.touhou_alice_core.client;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * 人形の手持ちアイテムを描画するレイヤー
 * @author iwa_yuki
 *
 */
public class LayerAliceDollHeldItem extends LayerHeldItem {
	
	RenderAliceDoll renderer;

	public LayerAliceDollHeldItem(RenderAliceDoll renderer) {
		super(renderer);
		
		this.renderer = renderer;
	}
	
	protected void doRenderLayer(EntityAliceDoll doll, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {

		ItemStack itemstack = doll.getHeldItem();

        if (itemstack != null)
        {
            GlStateManager.pushMatrix();

            (this.renderer.getCurrentMainModel()).postRenderHiddenArm(0.0625F);
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2)
            {
                GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
                GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f8 = 0.375F;
                GlStateManager.scale(-f8, -f8, f8);
            }

            minecraft.getItemRenderer().renderItem(doll, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
		
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
    {
		this.doRenderLayer((EntityAliceDoll)entity, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }

}
