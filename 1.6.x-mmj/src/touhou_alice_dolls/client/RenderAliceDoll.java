////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.client;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLLog;

import com.google.common.collect.Maps;
import java.util.Map;

import mods.touhou_alice_dolls.*;
import mods.touhou_alice_dolls.dolls.*;

@SideOnly(Side.CLIENT)
public class RenderAliceDoll extends RenderBiped
{
    private ModelAliceDoll[] mainModels;
    private ModelAliceDoll[] armorLayer1Models;
    private ModelAliceDoll[] armorLayer2Models;
    private Map resourceMap;
    
    public RenderAliceDoll()
    {
        super(null, 0.5F);

        int length = DollRegistry.getDollListLength();
        mainModels = new ModelAliceDoll[length];
        armorLayer1Models = new ModelAliceDoll[length];
        armorLayer2Models = new ModelAliceDoll[length];
        resourceMap = Maps.newHashMap();

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
    protected ResourceLocation getResourceLocationFromPath(String path)
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
