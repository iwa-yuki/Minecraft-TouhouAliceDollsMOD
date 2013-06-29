package mods.touhou_alice_dolls.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelAliceDoll extends ModelBiped
{
    public ModelRenderer ribbon;
    public ModelRenderer hat1;
    public ModelRenderer hat2;
    public ModelRenderer hat3;
    public ModelRenderer skirt1;
    public ModelRenderer skirt2;

    public ModelAliceDoll()
    {
        this(0.0F);
    }

    public ModelAliceDoll(float par1)
    {
        this(par1, 0.0F, 64, 32);
    }

    // モデルの定義
    public ModelAliceDoll(float par1, float par2, int width, int height)
    {
        this.heldItemLeft = 0;
        this.heldItemRight = 0;
        this.isSneak = false;
        this.aimedBow = false;
        this.textureWidth = width;
        this.textureHeight = height;

        float offsetY = 14F;

		bipedHead = new ModelRenderer(this, 0, 4);
		bipedHead.addBox(-2F, -4F, -2F, 4, 4, 4, par1);
		bipedHead.setRotationPoint(0.0F, 0.0F + offsetY, 0.0F);
		
		ribbon = new ModelRenderer(this, 0, 0);
		ribbon.addBox(-3F, -1.2F, -0.8F, 6, 3, 1, par1 + 0.1F);
		ribbon.setRotationPoint(0.0F, -4.0F, 2.0F);
		ribbon.rotateAngleX = 0.2F*1.570796F;
		bipedHead.addChild(ribbon);

        hat1 = new ModelRenderer(this, 16, 20);
        hat1.addBox(-4F, 1F, -4F, 8, 1, 8, par1 + 0.1F);
        hat1.setRotationPoint(0.0F, -4.0F, 0.0F);
        hat1.rotateAngleX = 0.0F;
		bipedHead.addChild(hat1);

        hat2 = new ModelRenderer(this, 0, 24);
        hat2.addBox(-1.5F, -1.2F, -1.4F, 3, 2, 3, par1 + 0.1F);
        hat2.setRotationPoint(0.0F, -4.0F, 0.0F);
        hat2.rotateAngleX = -(5.0F/90.0F)*1.570796F;
		bipedHead.addChild(hat2);

        hat3 = new ModelRenderer(this, 2, 26);
        hat3.addBox(-0.5F, -2.2F, -0.0F, 1, 1, 2, par1 + 0.1F);
        hat3.setRotationPoint(0.0F, -4.0F, 0.0F);
        hat3.rotateAngleX = -(10.0F/90.0F)*1.570796F;
		bipedHead.addChild(hat3);

		bipedBody = new ModelRenderer(this, 16, 0);
		bipedBody.addBox(-2.0F, 0.0F, -1.0F, 4, 4, 2, par1);
		bipedBody.setRotationPoint(0.0F, 0.0F + offsetY, 0.0F);
		
		skirt1 = new ModelRenderer(this, 16, 6);
		skirt1.addBox(-3.0F, 4.0F, -2.0F, 6, 2, 4, par1);
		skirt1.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(skirt1);
		
		skirt2 = new ModelRenderer(this, 16, 12);
		skirt2.addBox(-4.0F, 6.0F, -3.0F, 8, 2, 6, par1);
		skirt2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(skirt2);
		
		bipedRightArm = new ModelRenderer(this, 0, 12);
		bipedRightArm.addBox(1.0F, -1.0F, -1.0F, 2, 4, 2, par1);
		bipedRightArm.setRotationPoint(-2.0F, 1.0F + offsetY, 0.0F);
		
		bipedLeftArm = new ModelRenderer(this, 8, 12);
		bipedLeftArm.addBox(-3.0F, -1.0F, -1.0F, 2, 4, 2, par1);
		bipedLeftArm.setRotationPoint(2.0F, 1.0F + offsetY, 0.0F);
		
		bipedRightLeg = new ModelRenderer(this, 0, 18);
		bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, par1);
		bipedRightLeg.setRotationPoint(-1.0F, 6.0F + offsetY, 0.0F);
		
		bipedLeftLeg = new ModelRenderer(this, 8, 18);
		bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, par1);
		bipedLeftLeg.setRotationPoint(1.0F, 6.0F + offsetY, 0.0F);
    }

    // モデルを描画
    @Override
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, entity);
        bipedHead.render(par7);
        bipedBody.render(par7);
        bipedRightArm.render(par7);
        bipedLeftArm.render(par7);
        bipedRightLeg.render(par7);
        bipedLeftLeg.render(par7);
    }

    // モデルの動きを計算
    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        bipedHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 2.0F * par2 * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;

        if (this.heldItemLeft != 0)
        {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemLeft;
        }

        if (this.heldItemRight != 0)
        {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemRight;
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;
        float var8;
        float var9;

        if (this.onGround > -9990.0F)
        {
            var8 = this.onGround;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(var8) * (float)Math.PI * 2.0F) * 0.2F;
            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            var8 = 1.0F - this.onGround;
            var8 *= var8;
            var8 *= var8;
            var8 = 1.0F - var8;
            var9 = MathHelper.sin(var8 * (float)Math.PI);
            float var10 = MathHelper.sin(this.onGround * (float)Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            bipedRightArm.rotateAngleX = (float)((double)bipedRightArm.rotateAngleX - ((double)var9 * 1.2D + (double)var10));
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            bipedRightArm.rotateAngleZ = MathHelper.sin(this.onGround * (float)Math.PI) * -0.4F;
        }

        if (this.isSneak)
        {
            bipedBody.rotateAngleX = 0.5F;
            bipedRightArm.rotateAngleX += 0.4F;
            bipedLeftArm.rotateAngleX += 0.4F;
            bipedRightLeg.rotationPointZ = 4.0F;
            bipedLeftLeg.rotationPointZ = 4.0F;
            bipedRightLeg.rotationPointY -= 1.0F;
            bipedLeftLeg.rotationPointY -= 1.0F;
        }
        else
        {
            bipedBody.rotateAngleX = 0.0F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;

        if (this.aimedBow)
        {
            var8 = 0.0F;
            var9 = 0.0F;
            bipedRightArm.rotateAngleZ = 0.0F;
            bipedLeftArm.rotateAngleZ = 0.0F;
            bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F) + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F + bipedHead.rotateAngleY + 0.4F;
            bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
            bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
            bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
        }
    }
}
