package mods.touhou_alice_dolls.client;

import net.minecraft.client.renderer.entity.RenderLiving;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.ForgeHooksClient;
import mods.touhou_alice_dolls.EntityAliceDoll;

@SideOnly(Side.CLIENT)
public class RenderAliceDoll extends RenderLiving
{
    private ModelAliceDoll modelDollMain;
    private ModelAliceDoll modelDollArmorChestplate;
    private ModelAliceDoll modelDollArmor;
    public static String[] armorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};
    
    public RenderAliceDoll()
    {
        super(new ModelAliceDoll(0.0F), 0.5F);
        this.modelDollMain = (ModelAliceDoll)(this.mainModel);
        this.modelDollArmorChestplate = new ModelAliceDoll(0.2F);
        this.modelDollArmor = new ModelAliceDoll(0.1F);
    }

    // Armorのレンダリング
    @Override
    protected int shouldRenderPass(
        EntityLiving entityliving, int armorSlot, float par3)
    {
        ItemStack itemstack = entityliving.getCurrentArmor(3 - armorSlot);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor armor = (ItemArmor)item;
                this.loadTexture(
                    ForgeHooksClient.getArmorTexture(
                        itemstack, "/mods/touhou_alice_dolls/dolls/armor/" + armorFilenamePrefix[armor.renderIndex] + "_" + (armorSlot >= 2 ? 2 : 1) + ".png"));
                ModelAliceDoll modelDoll = armorSlot == 2 ?
                    this.modelDollArmor : this.modelDollArmorChestplate;
                modelDoll.bipedHead.showModel = armorSlot == 0;
                modelDoll.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
                modelDoll.bipedRightArm.showModel = armorSlot == 1;
                modelDoll.bipedLeftArm.showModel = armorSlot == 1;
                modelDoll.bipedRightLeg.showModel = armorSlot == 3;
                modelDoll.bipedLeftLeg.showModel = armorSlot == 3;
                this.setRenderPassModel(modelDoll);

                if (modelDoll != null)
                {
                    modelDoll.onGround = this.mainModel.onGround;
                }

                if (modelDoll != null)
                {
                    modelDoll.isRiding = this.mainModel.isRiding;
                }

                if (modelDoll != null)
                {
                    modelDoll.isChild = this.mainModel.isChild;
                }

                float var8 = 1.0F;

                if (armor.getArmorMaterial() == EnumArmorMaterial.CLOTH)
                {
                    int var9 = armor.getColor(itemstack);
                    float var10 = (float)(var9 >> 16 & 255) / 255.0F;
                    float var11 = (float)(var9 >> 8 & 255) / 255.0F;
                    float var12 = (float)(var9 & 255) / 255.0F;
                    GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(var8, var8, var8);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    // Armorの染色されない部分のレンダリング
    @Override
    protected void func_82408_c(EntityLiving entityliving, int armorSlot, float par3)
    {
        ItemStack itemstack = entityliving.getCurrentArmor(3 - armorSlot);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor armor = (ItemArmor)item;
                this.loadTexture("/mods/touhou_alice_dolls/dolls/armor/trans.png");
                float var7 = 1.0F;
                GL11.glColor3f(var7, var7, var7);
            }
        }
    }

    // 人形のレンダリング
    public void renderDoll(EntityAliceDoll doll,
                           double par2, double par4, double par6,
                           float par8, float par9)
    {
        float var10 = 1.0F;
        GL11.glColor3f(var10, var10, var10);
        ItemStack heldItem = doll.getHeldItem();
        this.modelDollArmorChestplate.heldItemRight
            = this.modelDollArmor.heldItemRight
            = this.modelDollMain.heldItemRight = heldItem != null ? 1 : 0;
        this.modelDollArmorChestplate.isSneak
            = this.modelDollArmor.isSneak
            = this.modelDollMain.isSneak
            = doll.isSneaking();

        // if (heldItem != null)
        // {
        //     EnumAction var12 = heldItem.getItemUseAction();

        //     if (var12 == EnumAction.block)
        //     {
        //         this.modelDollArmorChestplate.heldItemRight
        //             = this.modelDollArmor.heldItemRight
        //             = this.modelDollMain.heldItemRight = 3;
        //     }
        //     else if (var12 == EnumAction.bow)
        //     {
        //         this.modelDollArmorChestplate.aimedBow
        //             = this.modelDollArmor.aimedBow
        //             = this.modelDollMain.aimedBow = true;
        //     }
        // }

        double var14 = par4 - (double)doll.yOffset;

        if (doll.isSneaking())
        {
            var14 -= 0.125D;
        }

        super.doRenderLiving(doll, par2, var14, par6, par8, par9);
        
        this.modelDollArmorChestplate.aimedBow
            = this.modelDollArmor.aimedBow
            = this.modelDollMain.aimedBow = false;
        this.modelDollArmorChestplate.isSneak
            = this.modelDollArmor.isSneak
            = this.modelDollMain.isSneak = false;
        this.modelDollArmorChestplate.heldItemRight
            = this.modelDollArmor.heldItemRight
            = this.modelDollMain.heldItemRight = 0;
    }

    // レンダリングが必要なときに呼ばれる
    public void doRenderLiving(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderDoll((EntityAliceDoll)par1Entity, par2, par4, par6, par8, par9);
    }

    // 手持ちアイテム・頭にかぶるもののレンダリング
    @Override
    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        float var3 = 1.0F;
        GL11.glColor3f(var3, var3, var3);
        ItemStack heldItem = par1EntityLiving.getHeldItem();
        ItemStack armorHead = par1EntityLiving.getCurrentArmor(3);
        float var6;
        float dollScale = 0.5F;

        if (armorHead != null)
        {
            GL11.glPushMatrix();
            this.modelDollMain.bipedHead.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(
                armorHead, EQUIPPED);
            boolean is3D = (customRenderer != null
                            && customRenderer.shouldUseRenderHelper(
                                EQUIPPED, armorHead, BLOCK_3D));

            if (armorHead.getItem() instanceof ItemBlock)
            {
                if (is3D || RenderBlocks.renderItemIn3d(
                        Block.blocksList[armorHead.itemID].getRenderType()))
                {
                    var6 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, -var6);
                }

                this.renderManager.itemRenderer.renderItem(
                    par1EntityLiving, armorHead, 0);
            }
            else if (armorHead.getItem().itemID == Item.skull.itemID)
            {
                var6 = 1.0625F;
                GL11.glScalef(var6, -var6, -var6);
                String var7 = "";

                if (armorHead.hasTagCompound()
                    && armorHead.getTagCompound().hasKey("SkullOwner"))
                {
                    var7 = armorHead.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(
                    -0.5F, 0.0F, -0.5F, 1, 180.0F, armorHead.getItemDamage(), var7);
            }

            GL11.glPopMatrix();
        }

        if (heldItem != null)
        {
            GL11.glPushMatrix();

            this.modelDollMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(0.125F, 0.1255F, 0.0625F);

            IItemRenderer customRenderer
                = MinecraftForgeClient.getItemRenderer(heldItem, EQUIPPED);
            boolean is3D = (customRenderer != null
                            && customRenderer.shouldUseRenderHelper(
                                EQUIPPED, heldItem, BLOCK_3D));

            if (heldItem.getItem() instanceof ItemBlock
                && (is3D || RenderBlocks.renderItemIn3d(
                        Block.blocksList[heldItem.itemID].getRenderType())))
            {
                var6 = 0.5F*dollScale;
                GL11.glTranslatef(0.0F, 0.12F, -0.15F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var6, -var6, var6);
            }
            else if (heldItem.itemID == Item.bow.itemID)
            {
                var6 = 0.625F*dollScale;
                GL11.glTranslatef(-0.0625F, 0.075F, 0.1F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[heldItem.itemID].isFull3D())
            {
                var6 = 0.625F*dollScale;

                if (Item.itemsList[heldItem.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.func_82422_c();
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F*dollScale;
                GL11.glTranslatef(0.11F, 0.11F, -0.1F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            this.renderManager.itemRenderer.renderItem(par1EntityLiving, heldItem, 0);

            if (heldItem.getItem().requiresMultipleRenderPasses())
            {
                for (int x = 1;
                     x < heldItem.getItem().getRenderPasses(heldItem.getItemDamage());
                     x++)
                {
                    this.renderManager.itemRenderer.renderItem(
                        par1EntityLiving, heldItem, x);
                }
            }

            GL11.glPopMatrix();
        }
    }

    // なんだろ…この関数
    protected void func_82422_c()
    {
        GL11.glTranslatef(0.0F, 0.0875F, -0.05F);
    }
}
