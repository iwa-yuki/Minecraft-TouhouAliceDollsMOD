////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core.client;

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
import net.minecraft.init.Items;
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

import mods.touhou_alice_core.*;
import mods.touhou_alice_core.dolls.*;

@SideOnly(Side.CLIENT)
public class RenderAliceDoll extends RenderBiped
{
    // 本体モデル
    private ModelBiped[] mainModels;
    // 防具モデル(チェストプレート用)
    private ModelBiped[] armorLayer1Models;
    // 防具モデル(ヘルメット、レギンス、ブーツ用)
    private ModelBiped[] armorLayer2Models;
    private static Map resourceMap = Maps.newHashMap();
    
    public RenderAliceDoll()
    {
        super(null, 0.5F);

        int length = DollRegistry.getDollListLength();
        mainModels = new ModelBiped[length];
        armorLayer1Models = new ModelBiped[length];
        armorLayer2Models = new ModelBiped[length];
    }

    @Override
    protected void func_82421_b()
    {
        this.field_82423_g = null;
        this.field_82425_h = null;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getResourceLocation((EntityAliceDoll)par1Entity);
    }

    /**
     * 人形モデルを登録
     * @param id 登録する人形のID
     */
    public void registerDollModel(int id)
    {
        mainModels[id] = DollRegistry.getModelInstance(id, 0.0F);
        armorLayer1Models[id] = DollRegistry.getModelInstance(id, 0.25F);
        armorLayer2Models[id] = DollRegistry.getModelInstance(id, 0.125F);
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
    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        float f1 = 1.0F;
        GL11.glColor3f(f1, f1, f1);
        EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
        int dollID = entityDoll.getDollID();
        EnumDollRenderType renderType = DollRegistry.getRenderType(dollID);

        // 手持ちアイテム
        ItemStack itemHeld = par1EntityLiving.getHeldItem();
        // 頭に装備ができるアイテム(カボチャなど)
        ItemStack itemHelmet = par1EntityLiving.func_130225_q(3);
        float f2;

        if (itemHelmet != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(
                itemHelmet, EQUIPPED);
            boolean is3D = (customRenderer != null
                            && customRenderer.shouldUseRenderHelper(
                                EQUIPPED, itemHelmet, BLOCK_3D));

            if (itemHelmet.getItem() instanceof ItemBlock)
            {
                if(renderType == EnumDollRenderType.TALL)
                {
                    GL11.glScalef(0.65F, 0.65F, 0.65F);
                }

                if (is3D || RenderBlocks.renderItemIn3d(
                        Block.getBlockFromItem(itemHelmet.getItem()).getRenderType()))
                {
                    f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemHelmet, 0);
            }
            else if (itemHelmet.getItem() == Items.skull)
            {
                if(renderType == EnumDollRenderType.TALL)
                {
                    GL11.glScalef(0.73F, 0.73F, 0.73F);
                }
                else if(renderType == EnumDollRenderType.VENTI)
                {
                    GL11.glScalef(0.995F, 0.995F, 0.995F);
                }

                f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                String s = "";

                if (itemHelmet.hasTagCompound() && itemHelmet.getTagCompound().hasKey("SkullOwner"))
                {
                    s = itemHelmet.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.field_147536_b.func_147530_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemHelmet.getItemDamage(), s);
            }

            GL11.glPopMatrix();
        }

        if (itemHeld != null)
        {
            GL11.glPushMatrix();

            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            if(renderType == EnumDollRenderType.BIPED)
            {
                GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            }
            else if(renderType == EnumDollRenderType.DOLL)
            {
                GL11.glTranslatef(-0.0625F, 0.1F, -0.03125F);
                GL11.glScalef(0.4F, 0.4F, 0.4F);
            }
            else if(renderType == EnumDollRenderType.TALL)
            {
                GL11.glTranslatef(-0.0625F, 0.25F, 0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            else if(renderType == EnumDollRenderType.GRANDE)
            {
                GL11.glTranslatef(-0.0625F, 0.3F, 0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
            else if(renderType == EnumDollRenderType.VENTI)
            {
                GL11.glTranslatef(-0.0625F, 0.7F, 0.0F);
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemHeld, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemHeld, BLOCK_3D));

            if (itemHeld.getItem() instanceof ItemBlock && 
            		(is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemHeld.getItem()).getRenderType())))
            {
                f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            }
            else if (itemHeld.getItem() == Items.bow)
            {
                f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (itemHeld.getItem().isFull3D())
            {
                f2 = 0.625F;

                if (itemHeld.getItem().shouldRotateAroundWhenRendering())
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

            this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemHeld, 0);

            if (itemHeld.getItem().requiresMultipleRenderPasses())
            {
                for (int x = 1; x < itemHeld.getItem().getRenderPasses(itemHeld.getItemDamage()); x++)
                {
                    this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemHeld, x);
                }
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
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
                this.bindTexture(getResourceLocationFromPath(
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
    protected void func_82408_c(EntityLiving par1EntityLiving, int par2, float par3)
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
                this.bindTexture(getResourceLocationFromPath(
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
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        EntityAliceDoll entityDoll = (EntityAliceDoll)par1EntityLiving;
        int dollID = entityDoll.getDollID();
        this.mainModel = this.modelBipedMain = mainModels[dollID];
        this.field_82423_g = armorLayer1Models[dollID];
        this.field_82425_h = armorLayer2Models[dollID];

        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
    }
}
