package mods.touhou_alice_dolls.client;

import net.minecraft.client.renderer.entity.RenderLiving;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.ForgeHooksClient;

@SideOnly(Side.CLIENT)
public class RenderAliceDollBiped extends RenderLiving
{
    private ModelBiped modelBipedMain;
    private ModelBiped modelArmorChestplate;
    private ModelBiped modelArmor;
    public static String[] armorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};
    public static float NAME_TAG_RANGE = 64.0f;
    public static float NAME_TAG_RANGE_SNEAK = 32.0f;

    public RenderAliceDollBiped()
    {
        super(new ModelBiped(0.0F), 0.5F);
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
    }

    /**
     * Set the specified armor model as the player model. Args: player, armorSlot, partialTick
     */
    protected int setArmorModel(EntityLiving entityliving, int par2, float par3)
    {
        ItemStack var4 = entityliving.getCurrentArmor(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)var5;
                this.loadTexture(ForgeHooksClient.getArmorTexture(var4, "/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + ".png"));
                ModelBiped var7 = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
                var7.bipedHead.showModel = par2 == 0;
                var7.bipedHeadwear.showModel = par2 == 0;
                var7.bipedBody.showModel = par2 == 1 || par2 == 2;
                var7.bipedRightArm.showModel = par2 == 1;
                var7.bipedLeftArm.showModel = par2 == 1;
                var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                this.setRenderPassModel(var7);

                if (var7 != null)
                {
                    var7.onGround = this.mainModel.onGround;
                }

                if (var7 != null)
                {
                    var7.isRiding = this.mainModel.isRiding;
                }

                if (var7 != null)
                {
                    var7.isChild = this.mainModel.isChild;
                }

                float var8 = 1.0F;

                if (var6.getArmorMaterial() == EnumArmorMaterial.CLOTH)
                {
                    int var9 = var6.getColor(var4);
                    float var10 = (float)(var9 >> 16 & 255) / 255.0F;
                    float var11 = (float)(var9 >> 8 & 255) / 255.0F;
                    float var12 = (float)(var9 & 255) / 255.0F;
                    GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);

                    if (var4.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(var8, var8, var8);

                if (var4.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void func_82439_b(EntityLiving entityliving, int par2, float par3)
    {
        ItemStack var4 = entityliving.getCurrentArmor(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)var5;
                this.loadTexture(ForgeHooksClient.getArmorTexture(var4, "/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + "_b.png"));
                float var7 = 1.0F;
                GL11.glColor3f(var7, var7, var7);
            }
        }
    }

    public void render(EntityLiving entityliving, double par2, double par4, double par6, float par8, float par9)
    {
        float var10 = 1.0F;
        GL11.glColor3f(var10, var10, var10);
        ItemStack var11 = entityliving.getHeldItem();
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = var11 != null ? 1 : 0;

        // if (var11 != null && entityliving.getItemInUseCount() > 0)
        // {
        //     EnumAction var12 = var11.getItemUseAction();

        //     if (var12 == EnumAction.block)
        //     {
        //         this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
        //     }
        //     else if (var12 == EnumAction.bow)
        //     {
        //         this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
        //     }
        // }

        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = entityliving.isSneaking();
        double var14 = par4 - (double)entityliving.yOffset;

        if (entityliving.isSneaking())
        {
            var14 -= 0.125D;
        }

        super.doRenderLiving(entityliving, par2, var14, par6, par8, par9);
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    /**
     * Method for adding special render rules
     */
    protected void renderSpecials(EntityLiving entityliving, float par2)
    {
        float var3 = 1.0F;
        GL11.glColor3f(var3, var3, var3);
        super.renderEquippedItems(entityliving, par2);
        super.renderArrowsStuckInEntity(entityliving, par2);
        ItemStack var4 = entityliving.getCurrentArmor(3);

        if (var4 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);
            float var5;

            if (var4 != null && var4.getItem() instanceof ItemBlock)
            {
                IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var4, EQUIPPED);
                boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var4, BLOCK_3D));

                if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType()))
                {
                    var5 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var5, -var5, -var5);
                }

                this.renderManager.itemRenderer.renderItem(entityliving, var4, 0);
            }
            else if (var4.getItem().itemID == Item.skull.itemID)
            {
                var5 = 1.0625F;
                GL11.glScalef(var5, -var5, -var5);
                String var6 = "";

                if (var4.hasTagCompound() && var4.getTagCompound().hasKey("SkullOwner"))
                {
                    var6 = var4.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.getItemDamage(), var6);
            }

            GL11.glPopMatrix();
        }

        float var7;
        float var8;

        // if (entityliving.username.equals("deadmau5") && this.loadDownloadableImageTexture(entityliving.skinUrl, (String)null))
        // {
        //     for (int var20 = 0; var20 < 2; ++var20)
        //     {
        //         float var25 = entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * par2 - (entityliving.prevRenderYawOffset + (entityliving.renderYawOffset - entityliving.prevRenderYawOffset) * par2);
        //         var7 = entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * par2;
        //         GL11.glPushMatrix();
        //         GL11.glRotatef(var25, 0.0F, 1.0F, 0.0F);
        //         GL11.glRotatef(var7, 1.0F, 0.0F, 0.0F);
        //         GL11.glTranslatef(0.375F * (float)(var20 * 2 - 1), 0.0F, 0.0F);
        //         GL11.glTranslatef(0.0F, -0.375F, 0.0F);
        //         GL11.glRotatef(-var7, 1.0F, 0.0F, 0.0F);
        //         GL11.glRotatef(-var25, 0.0F, 1.0F, 0.0F);
        //         var8 = 1.3333334F;
        //         GL11.glScalef(var8, var8, var8);
        //         this.modelBipedMain.renderEars(0.0625F);
        //         GL11.glPopMatrix();
        //     }
        // }

        float var11;

        // if (this.loadDownloadableImageTexture(entityliving.playerCloakUrl, (String)null) && !entityliving.getHasActivePotion() && !entityliving.getHideCape())
        // {
        //     GL11.glPushMatrix();
        //     GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        //     double var22 = entityliving.field_71091_bM + (entityliving.field_71094_bP - entityliving.field_71091_bM) * (double)par2 - (entityliving.prevPosX + (entityliving.posX - entityliving.prevPosX) * (double)par2);
        //     double var24 = entityliving.field_71096_bN + (entityliving.field_71095_bQ - entityliving.field_71096_bN) * (double)par2 - (entityliving.prevPosY + (entityliving.posY - entityliving.prevPosY) * (double)par2);
        //     double var9 = entityliving.field_71097_bO + (entityliving.field_71085_bR - entityliving.field_71097_bO) * (double)par2 - (entityliving.prevPosZ + (entityliving.posZ - entityliving.prevPosZ) * (double)par2);
        //     var11 = entityliving.prevRenderYawOffset + (entityliving.renderYawOffset - entityliving.prevRenderYawOffset) * par2;
        //     double var12 = (double)MathHelper.sin(var11 * (float)Math.PI / 180.0F);
        //     double var14 = (double)(-MathHelper.cos(var11 * (float)Math.PI / 180.0F));
        //     float var16 = (float)var24 * 10.0F;

        //     if (var16 < -6.0F)
        //     {
        //         var16 = -6.0F;
        //     }

        //     if (var16 > 32.0F)
        //     {
        //         var16 = 32.0F;
        //     }

        //     float var17 = (float)(var22 * var12 + var9 * var14) * 100.0F;
        //     float var18 = (float)(var22 * var14 - var9 * var12) * 100.0F;

        //     if (var17 < 0.0F)
        //     {
        //         var17 = 0.0F;
        //     }

        //     // float var19 = entityliving.prevCameraYaw + (entityliving.cameraYaw - entityliving.prevCameraYaw) * par2;
        //     // var16 += MathHelper.sin((entityliving.prevDistanceWalkedModified + (entityliving.distanceWalkedModified - entityliving.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * var19;

        //     if (entityliving.isSneaking())
        //     {
        //         var16 += 25.0F;
        //     }

        //     GL11.glRotatef(6.0F + var17 / 2.0F + var16, 1.0F, 0.0F, 0.0F);
        //     GL11.glRotatef(var18 / 2.0F, 0.0F, 0.0F, 1.0F);
        //     GL11.glRotatef(-var18 / 2.0F, 0.0F, 1.0F, 0.0F);
        //     GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        //     this.modelBipedMain.renderCloak(0.0625F);
        //     GL11.glPopMatrix();
        // }

        ItemStack var21 = entityliving.getHeldItem();

        if (var21 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            // if (entityliving.fishEntity != null)
            // {
            //     var21 = new ItemStack(Item.stick);
            // }

            EnumAction var23 = null;

            // if (entityliving.getItemInUseCount() > 0)
            // {
            //     var23 = var21.getItemUseAction();
            // }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var21, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var21, BLOCK_3D));

            if (var21.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var21.itemID].getRenderType())))
            {
                var7 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var7 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var7, -var7, var7);
            }
            else if (var21.itemID == Item.bow.itemID)
            {
                var7 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var7, -var7, var7);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[var21.itemID].isFull3D())
            {
                var7 = 0.625F;

                if (Item.itemsList[var21.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                // if (entityliving.getItemInUseCount() > 0 && var23 == EnumAction.block)
                // {
                //     GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                //     GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                //     GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                //     GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                // }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(var7, -var7, var7);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var7 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var7, var7, var7);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            float var10;
            int var27;
            float var28;

            if (var21.getItem().requiresMultipleRenderPasses())
            {
                for (var27 = 0; var27 < var21.getItem().getRenderPasses(var21.getItemDamage()); ++var27)
                {
                    int var26 = var21.getItem().getColorFromItemStack(var21, var27);
                    var28 = (float)(var26 >> 16 & 255) / 255.0F;
                    var10 = (float)(var26 >> 8 & 255) / 255.0F;
                    var11 = (float)(var26 & 255) / 255.0F;
                    GL11.glColor4f(var28, var10, var11, 1.0F);
                    this.renderManager.itemRenderer.renderItem(entityliving, var21, var27);
                }
            }
            else
            {
                var27 = var21.getItem().getColorFromItemStack(var21, 0);
                var8 = (float)(var27 >> 16 & 255) / 255.0F;
                var28 = (float)(var27 >> 8 & 255) / 255.0F;
                var10 = (float)(var27 & 255) / 255.0F;
                GL11.glColor4f(var8, var28, var10, 1.0F);
                this.renderManager.itemRenderer.renderItem(entityliving, var21, 0);
            }

            GL11.glPopMatrix();
        }
    }

    public void func_82441_a(EntityLiving entityliving)
    {
        float var2 = 1.0F;
        GL11.glColor3f(var2, var2, var2);
        this.modelBipedMain.onGround = 0.0F;
        this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityliving);
        this.modelBipedMain.bipedRightArm.render(0.0625F);
    }

    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        float var3 = 0.9375F;
        GL11.glScalef(var3, var3, var3);
    }

    protected void func_82408_c(EntityLiving par1EntityLiving, int par2, float par3)
    {
        this.func_82439_b((EntityLiving)par1EntityLiving, par2, par3);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.setArmorModel((EntityLiving)par1EntityLiving, par2, par3);
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        this.renderSpecials((EntityLiving)par1EntityLiving, par2);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.render((EntityLiving)par1EntityLiving, par2, par4, par6, par8, par9);
    }
}
