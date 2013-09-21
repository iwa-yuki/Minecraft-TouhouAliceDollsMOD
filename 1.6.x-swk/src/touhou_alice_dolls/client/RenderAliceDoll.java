////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.ForgeHooksClient;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLLog;

import com.google.common.collect.Maps;
import java.util.Map;
import org.lwjgl.opengl.GL11;

import mods.touhou_alice_dolls.*;
import mods.touhou_alice_dolls.dolls.*;

@SideOnly(Side.CLIENT)
public class RenderAliceDoll extends RenderBiped
{
    private ModelBiped[] mainModels;
    private ModelBiped[] armorLayer1Models;
    private ModelBiped[] armorLayer2Models;
    private static Map resourceMap = Maps.newHashMap();
    
    public RenderAliceDoll()
    {
        super(null, 0.5F);

        int length = DollRegistry.getDollListLength();
        mainModels = new ModelBiped[length];
        armorLayer1Models = new ModelBiped[length];
        armorLayer2Models = new ModelBiped[length];

        for(int i=0;i<length;++i)
        {
            mainModels[i] = DollRegistry.getModelInstance(i, 0.0F);
            armorLayer1Models[i] = DollRegistry.getModelInstance(i, 0.25F);
            armorLayer2Models[i] = DollRegistry.getModelInstance(i, 0.125F);
        }
    }

    @Override
    protected void func_82421_b()
    {
        this.field_82423_g = null;
        this.field_82425_h = null;
    }

    @Override
    protected ResourceLocation func_110775_a(Entity par1Entity)
    {
        return this.getResourceLocation((EntityAliceDoll)par1Entity);
    }

    /**
     * 人形のResourceLocationを取得する
     * @param par1EntityDoll ResourceLocationを取得する人形エンティティ
     */
    protected ResourceLocation getResourceLocation(
        EntityAliceDoll par1EntityDoll)
    {
        int id = par1EntityDoll.getDollID();
        return getResourceLocationFromPath(
            DollRegistry.getMainTexturePath(id));
    }

    /**
     * ハッシュマップからResourceLocationを取得
     * @param path テクスチャのパス
     */
    public static ResourceLocation getResourceLocationFromPath(String path)
    {
        if(path == null || path == "")
        {
            return null;
        }
        ResourceLocation rl = (ResourceLocation)resourceMap.get(path);

        if(rl == null)
        {
            rl = new ResourceLocation(path);
            resourceMap.put(path, rl);
            FMLLog.info("Loaded texture \"%s\".", path);
        }

        return rl;
    }

    @Override
    protected void func_130005_c(EntityLiving par1EntityLiving, float par2)
    {
        float f1 = 1.0F;
        GL11.glColor3f(f1, f1, f1);
        EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
        int dollID = entityDoll.getDollID();
        EnumRenderType renderType = DollRegistry.getRenderType(dollID);

        ItemStack itemstack = par1EntityLiving.getHeldItem();
        ItemStack itemstack1 = par1EntityLiving.func_130225_q(3);
        float f2;

        if (itemstack1 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(
                itemstack1, EQUIPPED);
            boolean is3D = (customRenderer != null
                            && customRenderer.shouldUseRenderHelper(
                                EQUIPPED, itemstack1, BLOCK_3D));

            if (itemstack1.getItem() instanceof ItemBlock)
            {
                if(renderType == EnumRenderType.TALL)
                {
                    GL11.glScalef(0.65F, 0.65F, 0.65F);
                }

                if (is3D || RenderBlocks.renderItemIn3d(
                        Block.blocksList[itemstack1.itemID].getRenderType()))
                {
                    f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack1, 0);
            }
            else if (itemstack1.getItem().itemID == Item.skull.itemID)
            {
                if(renderType == EnumRenderType.TALL)
                {
                    GL11.glScalef(0.73F, 0.73F, 0.73F);
                }
                else if(renderType == EnumRenderType.VENTI)
                {
                    GL11.glScalef(0.995F, 0.995F, 0.995F);
                }

                f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                String s = "";

                if (itemstack1.hasTagCompound() && itemstack1.getTagCompound().hasKey("SkullOwner"))
                {
                    s = itemstack1.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack1.getItemDamage(), s);
            }

            GL11.glPopMatrix();
        }

        if (itemstack != null)
        {
            GL11.glPushMatrix();

            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            if(renderType == EnumRenderType.BIPED)
            {
                GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            }
            else if(renderType == EnumRenderType.DOLL)
            {
                GL11.glTranslatef(-0.0625F, 0.1F, -0.03125F);
                GL11.glScalef(0.4F, 0.4F, 0.4F);
            }
            else if(renderType == EnumRenderType.TALL)
            {
                GL11.glTranslatef(-0.0625F, 0.25F, 0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            else if(renderType == EnumRenderType.GRANDE)
            {
                GL11.glTranslatef(-0.0625F, 0.3F, 0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            else if(renderType == EnumRenderType.VENTI)
            {
                GL11.glTranslatef(-0.0625F, 0.7F, 0.0F);
                //GL11.glScalef(0.75F, 0.75F, 0.75F);
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

            if (itemstack.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType())))
            {
                f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            }
            else if (itemstack.itemID == Item.bow.itemID)
            {
                f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[itemstack.itemID].isFull3D())
            {
                f2 = 0.625F;

                if (Item.itemsList[itemstack.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.func_82422_c();
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, 0);

            if (itemstack.getItem().requiresMultipleRenderPasses())
            {
                for (int x = 1; x < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); x++)
                {
                    this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, x);
                }
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    protected int func_130006_a(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack itemstack = par1EntityLiving.func_130225_q(3 - par2);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
                int dollID = entityDoll.getDollID();
                ItemArmor itemarmor = (ItemArmor)item;
                this.func_110776_a(getResourceLocationFromPath(
                                       DollRegistry.getArmorTexturePath(
                                           dollID,
                                           itemarmor.renderIndex,
                                           par2, null)));
                ModelBiped modelbiped = par2 == 2 ? this.field_82425_h : this.field_82423_g;
                modelbiped.bipedHead.showModel = par2 == 0;
                modelbiped.bipedHeadwear.showModel = par2 == 0;
                modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
                modelbiped.bipedRightArm.showModel = par2 == 1;
                modelbiped.bipedLeftArm.showModel = par2 == 1;
                modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped = ForgeHooksClient.getArmorModel(par1EntityLiving, itemstack, par2, modelbiped);
                this.setRenderPassModel(modelbiped);
                modelbiped.onGround = this.mainModel.onGround;
                modelbiped.isRiding = this.mainModel.isRiding;
                modelbiped.isChild = this.mainModel.isChild;
                float f1 = 1.0F;

                //Move out of if to allow for more then just CLOTH to have color
                int j = itemarmor.getColor(itemstack);
                if (j != -1)
                {
                    float f2 = (float)(j >> 16 & 255) / 255.0F;
                    float f3 = (float)(j >> 8 & 255) / 255.0F;
                    float f4 = (float)(j & 255) / 255.0F;
                    GL11.glColor3f(f1 * f2, f1 * f3, f1 * f4);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(f1, f1, f1);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    @Override
    protected void func_130013_c(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack itemstack = par1EntityLiving.func_130225_q(3 - par2);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
                int dollID = entityDoll.getDollID();
                ItemArmor itemarmor = (ItemArmor)item;
                this.func_110776_a(getResourceLocationFromPath(
                                       DollRegistry.getArmorTexturePath(
                                           dollID,
                                           itemarmor.renderIndex,
                                           par2,
                                           "_overlay")));
                float f1 = 1.0F;
                GL11.glColor3f(f1, f1, f1);
            }
        }
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.mainModel.isRiding = this.field_82423_g.isRiding = this.field_82425_h.isRiding =
            (par1EntityLivingBase.ridingEntity != null)
            && !(par1EntityLivingBase.ridingEntity instanceof EntityPlayer);
        if (this.renderPassModel != null)
        {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }
    }
    
    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
        int dollID = entityDoll.getDollID();
        this.mainModel = this.modelBipedMain = mainModels[dollID];
        this.field_82423_g = armorLayer1Models[dollID];
        this.field_82425_h = armorLayer2Models[dollID];

        super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
    }
}
