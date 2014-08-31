////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.*;
import mods.touhou_alice_dolls.dolls.*;

@SideOnly(Side.CLIENT)
public class ModelAliceDoll extends ModelBiped
{
    /** モデルのY座標オフセット */
    private float offsetY;

    /** スニーク時の足のY座標調整用変数 */
    private float legY;

    /** スニーク時の足のZ座標調整用変数 */
    private float legZ;
    
    /** ボックス拡張係数 */
    private float expansion;

    /** モデル種別 */
    private EnumRenderType renderType = EnumRenderType.BIPED;

    public ModelAliceDoll()
    {
        this(0.0F);
    }

    public ModelAliceDoll(float expand)
    {
        this(expand, 0.0F, 64, 32);
    }

    public ModelAliceDoll(
        float expand, float offset, int texWidth, int texHeight)
    {
        this.expansion = expand;
        this.offsetY = offset;
        this.textureWidth = texWidth;
        this.textureHeight = texHeight;

        this.bipedCloak.isHidden = true;
        this.bipedEars.isHidden = true;
        this.bipedHead.isHidden = true;
        this.bipedHeadwear.isHidden = true;
        this.bipedBody.isHidden = true;
        this.bipedRightArm.isHidden = true;
        this.bipedLeftArm.isHidden = true;
        this.bipedRightLeg.isHidden = true;
        this.bipedLeftLeg.isHidden = true;
    }
    
    private float legYOffset(boolean isSneak)
    {
        return isSneak ? legY - 1 : legY;
    }

    private float legZOffset(boolean isSneak)
    {
        return isSneak ? 2.0F + legZ : 0.1F + legZ;
    }

    public void setRenderType(EnumRenderType type)
    {
        renderType = type;
    }
    
    /**
     * モデルにボックスを追加する
     * @param texX テクスチャのX座標
     * @param texY テクスチャのY座標
     * @param mirror テクスチャを反転するかどうか
     * @param posX ボックスのX座標
     * @param posY ボックスのY座標
     * @param posZ ボックスのZ座標
     * @param cx ボックスのX方向の大きさ
     * @param cy ボックスのY方向の大きさ
     * @param cz ボックスのZ方向の大きさ
     * @param scale ボックスの拡張係数(default=0.0F)
     * @param rotX 回転中心のX座標
     * @param rotY 回転中心のY座標
     * @param rotZ 回転中心のZ座標
     * @param dirX X軸回転角
     * @param dirY Y軸回転角
     * @param dirZ Z軸回転角
     * @param name 追加するボックスの名前
     * @param parent 親となるボックス(なければnull)
     */
    public void addBox(int texX, int texY, boolean mirror,
                       float posX, float posY, float posZ,
                       int cx, int cy, int cz, float scale,
                       float rotX, float rotY, float rotZ,
                       float dirX, float dirY, float dirZ,
                       String name, String parent)
    {
        if(parent == null)
        {
            EnumModelBox type;

            if("head".equals(name))
            {
                type = EnumModelBox.HEAD;
            }
            else if("headwear".equals(name))
            {
                type = EnumModelBox.HEADWEAR;
            }
            else if("body".equals(name))
            {
                type = EnumModelBox.BODY;
            }
            else if("rightarm".equals(name))
            {
                type = EnumModelBox.RIGHT_ARM;
            }
            else if("leftarm".equals(name))
            {
                type = EnumModelBox.LEFT_ARM;
            }
            else if("rightleg".equals(name))
            {
                type = EnumModelBox.RIGHT_LEG;
            }
            else if("leftleg".equals(name))
            {
                type = EnumModelBox.LEFT_LEG;
            }
            else if("cloak".equals(name))
            {
                type = EnumModelBox.CLOAK;
            }
            else if("EARS".equals(name))
            {
                type = EnumModelBox.EARS;
            }
            else
            {
                FMLLog.info("Ignored box \"%s\".", name);
                return;
            }
            setBaseModel(texX, texY, mirror,
                         posX, posY, posZ,
                         cx, cy, cz, scale + expansion,
                         rotX, rotY + offsetY, rotZ,
                         dirX, dirY, dirZ,
                         type);
        }
        else
        {
            EnumModelBox type;

            if("head".equals(parent))
            {
                type = EnumModelBox.HEAD;
            }
            else if("headwear".equals(parent))
            {
                type = EnumModelBox.HEADWEAR;
            }
            else if("body".equals(parent))
            {
                type = EnumModelBox.BODY;
            }
            else if("rightarm".equals(parent))
            {
                type = EnumModelBox.RIGHT_ARM;
            }
            else if("leftarm".equals(parent))
            {
                type = EnumModelBox.LEFT_ARM;
            }
            else if("rightleg".equals(parent))
            {
                type = EnumModelBox.RIGHT_LEG;
            }
            else if("leftleg".equals(parent))
            {
                type = EnumModelBox.LEFT_LEG;
            }
            else if("cloak".equals(parent))
            {
                type = EnumModelBox.CLOAK;
            }
            else if("ears".equals(parent))
            {
                type = EnumModelBox.EARS;
            }
            else
            {
                FMLLog.info("Ignored box \"%s/%s\".", parent, name);
                return;
            }
            addAccessary(texX, texY, mirror,
                         posX, posY, posZ,
                         cx, cy, cz, scale + expansion,
                         rotX, rotY, rotZ,
                         dirX, dirY, dirZ, type);
        }
    }
    
    private void setBaseModel(int texX, int texY, boolean mirror,
                              float posX, float posY, float posZ,
                              int cx, int cy, int cz, float scale,
                              float rotX, float rotY, float rotZ,
                              float dirX, float dirY, float dirZ,
                              EnumModelBox type)
    {
        ModelRenderer model = new ModelRenderer(this, texX, texY);
        model.addBox(posX, posY, posZ, cx, cy, cz, scale);
        model.setRotationPoint(rotX, rotY, rotZ);
        this.setRotation(model, dirX, dirY, dirZ);
        model.mirror = mirror;

        if(type == EnumModelBox.HEAD)
        {
            this.bipedHead = model;
            this.bipedHead.isHidden = false;
        }
        else if(type == EnumModelBox.HEADWEAR)
        {
            this.bipedHeadwear = model;
            this.bipedHeadwear.isHidden = false;
        }
        else if(type == EnumModelBox.BODY)
        {
            this.bipedBody = model;
            this.bipedBody.isHidden = false;
        }
        else if(type == EnumModelBox.RIGHT_ARM)
        {
            this.bipedRightArm = model;
            this.bipedRightArm.isHidden = false;
        }
        else if(type == EnumModelBox.LEFT_ARM)
        {
            this.bipedLeftArm = model;
            this.bipedLeftArm.isHidden = false;
        }
        else if(type == EnumModelBox.RIGHT_LEG)
        {
            this.bipedRightLeg = model;
            this.bipedRightLeg.isHidden = false;
            legY = rotY;
            legZ = rotZ;
        }
        else if(type == EnumModelBox.LEFT_LEG)
        {
            this.bipedLeftLeg = model;
            this.bipedLeftLeg.isHidden = false;
            legY = rotY;
            legZ = rotZ;
        }
        else if(type == EnumModelBox.CLOAK)
        {
            this.bipedCloak = model;
            this.bipedCloak.isHidden = false;
        }
        else if(type == EnumModelBox.EARS)
        {
            this.bipedEars = model;
            this.bipedEars.isHidden = false;
        }
    }

    protected void addAccessary(int texX, int texY, boolean mirror,
                                float posX, float posY, float posZ,
                                int cx, int cy, int cz, float scale,
                                float rotX, float rotY, float rotZ,
                                float dirX, float dirY, float dirZ,
                                EnumModelBox parent)
    {
        ModelRenderer child = new ModelRenderer(this, texX, texY);
        child.addBox(posX, posY, posZ, cx, cy, cz, scale);
        this.setRotation(child, dirX, dirY, dirZ);
        child.mirror = mirror;

        if(parent == EnumModelBox.HEAD)
        {
            child.setRotationPoint(rotX - this.bipedHead.rotationPointX,
                                   rotY - this.bipedHead.rotationPointY,
                                   rotZ - this.bipedHead.rotationPointZ);
            this.bipedHead.addChild(child);
        }
        else if(parent == EnumModelBox.HEADWEAR)
        {
            child.setRotationPoint(rotX - this.bipedHeadwear.rotationPointX,
                                   rotY - this.bipedHeadwear.rotationPointY,
                                   rotZ - this.bipedHeadwear.rotationPointZ);
            this.bipedHeadwear.addChild(child);
        }
        else if(parent == EnumModelBox.BODY)
        {
            child.setRotationPoint(rotX - this.bipedBody.rotationPointX,
                                   rotY - this.bipedBody.rotationPointY,
                                   rotZ - this.bipedBody.rotationPointZ);
            this.bipedBody.addChild(child);
        }
        else if(parent == EnumModelBox.RIGHT_ARM)
        {
            child.setRotationPoint(rotX - this.bipedRightArm.rotationPointX,
                                   rotY - this.bipedRightArm.rotationPointY,
                                   rotZ - this.bipedRightArm.rotationPointZ);
            this.bipedRightArm.addChild(child);
        }
        else if(parent == EnumModelBox.LEFT_ARM)
        {
            child.setRotationPoint(rotX - this.bipedLeftArm.rotationPointX,
                                   rotY - this.bipedLeftArm.rotationPointY,
                                   rotZ - this.bipedLeftArm.rotationPointZ);
            this.bipedLeftArm.addChild(child);
        }
        else if(parent == EnumModelBox.RIGHT_LEG)
        {
            child.setRotationPoint(rotX - this.bipedRightLeg.rotationPointX,
                                   rotY - this.bipedRightLeg.rotationPointY,
                                   rotZ - this.bipedRightLeg.rotationPointZ);
            this.bipedRightLeg.addChild(child);
        }
        else if(parent == EnumModelBox.LEFT_LEG)
        {
            child.setRotationPoint(rotX - this.bipedLeftLeg.rotationPointX,
                                   rotY - this.bipedLeftLeg.rotationPointY,
                                   rotZ - this.bipedLeftLeg.rotationPointZ);
            this.bipedLeftLeg.addChild(child);
        }
        else if(parent == EnumModelBox.CLOAK)
        {
            child.setRotationPoint(rotX - this.bipedCloak.rotationPointX,
                                   rotY - this.bipedCloak.rotationPointY,
                                   rotZ - this.bipedCloak.rotationPointZ);
            this.bipedCloak.addChild(child);
        }
        else if(parent == EnumModelBox.EARS)
        {
            child.setRotationPoint(rotX - this.bipedEars.rotationPointX,
                                   rotY - this.bipedEars.rotationPointY,
                                   rotZ - this.bipedEars.rotationPointZ);
            this.bipedEars.addChild(child);
        }
    }

    /**
     * 回転角の設定
     * @param model 設定対象のボックス
     * @param x X軸回転角
     * @param y Y軸回転角
     * @param z Z軸回転角
     */
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x / (180F / (float)Math.PI);
        model.rotateAngleY = y / (180F / (float)Math.PI);
        model.rotateAngleZ = z / (180F / (float)Math.PI);
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        float legAngle = 1.4F;
        if(renderType == EnumRenderType.VENTI)
        {
            legAngle = 1.0F;
        }
        this.bipedHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.bipedHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedRightArm.rotateAngleX =
            MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 2.0F * par2 * 0.5F;
        this.bipedLeftArm.rotateAngleX =
            MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * legAngle * par2;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * legAngle * par2;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;

        if (this.isRiding)
        {
            if(renderType == EnumRenderType.DOLL)
            {
                // 座るモーションは無効
            }
            else if(renderType == EnumRenderType.VENTI)
            {
                this.bipedRightArm.rotateAngleX += -((float)Math.PI / 10F);
                this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 10F);
                this.bipedRightLeg.rotateAngleX = -((float)Math.PI / 5F);
                this.bipedLeftLeg.rotateAngleX = -((float)Math.PI / 5F);
                this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
                this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
            }
            else
            {
                this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
                this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
                this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
                this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
                this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
                this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
            }
        }

        if (this.heldItemLeft != 0)
        {
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F
                - ((float)Math.PI / 10F) * (float)this.heldItemLeft;
        }

        if (this.heldItemRight != 0)
        {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F
                - ((float)Math.PI / 10F) * (float)this.heldItemRight;
        }

        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedLeftArm.rotateAngleY = 0.0F;
        float f6;
        float f7;
        float shoulderWidth = 3;

        if(renderType == EnumRenderType.DOLL)
        {
            shoulderWidth = 2.0F;
        }
        else if(renderType == EnumRenderType.TALL)
        {
            shoulderWidth = 3.0F;
        }
        else if(renderType == EnumRenderType.VENTI)
        {
            shoulderWidth = 4.0F;
        }

        if (this.onGround > -9990.0F)
        {
            // System.out.println(this.onGround);
            
            f6 = this.onGround;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * shoulderWidth;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * shoulderWidth;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * shoulderWidth;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * shoulderWidth;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f6 = 1.0F - this.onGround;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;
            f7 = MathHelper.sin(f6 * (float)Math.PI);
            float f8 = MathHelper.sin(this.onGround * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX - ((double)f7 * 1.2D + (double)f8));
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.onGround * (float)Math.PI) * -0.4F;
        }

        this.bipedBody.rotateAngleX = 0.0F;
        if (this.isSneak)
        {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
        }

        this.bipedRightLeg.rotationPointZ = legZOffset(this.isSneak);
        this.bipedLeftLeg.rotationPointZ = legZOffset(this.isSneak);
        this.bipedRightLeg.rotationPointY = legYOffset(this.isSneak);
        this.bipedLeftLeg.rotationPointY = legYOffset(this.isSneak);
        if(isRiding && renderType == EnumRenderType.GRANDE)
        {
            this.bipedRightLeg.rotationPointZ += 3F;
            this.bipedLeftLeg.rotationPointZ += 3F;
        }
        
        this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;

        if (this.aimedBow)
        {
            f6 = 0.0F;
            f7 = 0.0F;
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F - f6 * 0.6F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
            this.bipedLeftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
            this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
            this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
        }
    }
    
    /**
     * レンダリングを行う
     */
    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);

        if(this.bipedHead != null)
        {
            this.bipedHead.render(par7);
        }
        if(this.bipedBody != null)
        {
            this.bipedBody.render(par7);
        }
        if(this.bipedRightArm != null)
        {
            this.bipedRightArm.render(par7);
        }
        if(this.bipedLeftArm != null)
        {
            this.bipedLeftArm.render(par7);
        }
        if(this.bipedRightLeg != null)
        {
            this.bipedRightLeg.render(par7);
        }
        if(this.bipedLeftLeg != null)
        {
            this.bipedLeftLeg.render(par7);
        }
        if(this.bipedHeadwear != null)
        {
            this.bipedHeadwear.render(par7);
        }
        if(this.bipedCloak != null)
        {
            this.bipedCloak.render(par7);
        }
        if(this.bipedEars != null)
        {
            this.bipedEars.render(par7);
        }
    }
}
