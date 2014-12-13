package mods.touhou_alice_core.client;

import java.util.HashMap;

import com.google.common.collect.Lists;

import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import mods.touhou_alice_core.dolls.DollRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderAliceDoll extends RenderBiped {
	
	private int currentDollID = -1;
	
    public RenderAliceDoll(RenderManager rendermanager) {
    	this(rendermanager, 0.5F, 1.0F);
    }
    
	public RenderAliceDoll(RenderManager rendermanager, float p_i46169_3_, float p_i46169_4_) {
		super(rendermanager, getMainModel(0), p_i46169_3_, p_i46169_4_);

		setCurrentDollID(0);
	}

	@Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return getAliceDollTexture((EntityAliceDoll)entity);
    }
	
	@Override
	public void doRender(EntityLiving p_76986_1_, double p_76986_2_,
			double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		
		doRenderAliceDoll((EntityAliceDoll)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
				p_76986_9_);
	}

	@Override
	public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_,
			double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		doRenderAliceDoll((EntityAliceDoll)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
				p_76986_9_);
	}

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_,
			double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		doRenderAliceDoll((EntityAliceDoll)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_,
				p_76986_9_);
	}

	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * レンダリング対象の人形のIDを取得
	 * @return 人形ID
	 */
	public int getCurrentDollID() {
		return currentDollID;
	}
	
	/**
	 * レンダリング対象の人形を設定
	 * @param id 人形ID
	 * @return
	 */
	public boolean setCurrentDollID(int id) {
		if(currentDollID == id) {
			return false;
		}
		currentDollID = id;

		this.mainModel = this.modelBipedMain = getMainModel(id);

		field_177097_h.clear();
        this.addLayer(new LayerAliceDollCustomHead(this.modelBipedMain.bipedHead));
        this.addLayer(new LayerAliceDollHeldItem(this));
        this.addLayer(new LayerAliceDollArmor(this));
		
		return true;
	}
	
	/**
	 * 人形のテクスチャを取得する
	 * @param doll 対象となる人形Entity
	 * @return テクスチャのResourceLocation
	 */
    protected ResourceLocation getAliceDollTexture(EntityAliceDoll doll)
    {
        return getResourceLocationFromPath(DollRegistry.getMainTexturePath(doll.getDollID()));
    }
    

    /**
     * 人形をレンダリングする
     */
	private void doRenderAliceDoll(EntityAliceDoll doll,
			double p_76986_2_, double p_76986_4_, double p_76986_6_,
			float p_76986_8_, float p_76986_9_) {
		
		this.setCurrentDollID(doll.getDollID());
		
		super.doRender(doll, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
    
	///////////////////////////////////////////////////////////////////////////
	// Texture管理
	
	private static final HashMap<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();
	
	public static ResourceLocation getResourceLocationFromPath(String path) {
		
		if(!textures.containsKey(path)) {
			textures.put(path, new ResourceLocation(path));
		}
		
		return textures.get(path);
	}

	///////////////////////////////////////////////////////////////////////////
	// Model管理
	
	private static final ModelAliceDoll[] mainModels = new ModelAliceDoll[256];
	private static final ModelAliceDoll[] armorLayer1Models = new ModelAliceDoll[256];
	private static final ModelAliceDoll[] armorLayer2Models = new ModelAliceDoll[256];

	private static ModelAliceDoll getMainModel(int id) {
		
		if(id<0 || id>=mainModels.length) {
			id = 0;
		}
		if(mainModels[id] == null) {
			mainModels[id] = DollRegistry.getModelInstance(id, 0.0F);
		}
		return mainModels[id];
	}
	
	public ModelAliceDoll getCurrentMainModel() {
		return getMainModel(getCurrentDollID());
	}

	private static ModelAliceDoll getArmorModelLayer1(int id) {
		
		if(id<0 || id>=armorLayer1Models.length) {
			id = 0;
		}
		if(armorLayer1Models[id] == null) {
			armorLayer1Models[id] = DollRegistry.getModelInstance(id, 0.25F);
		}
		return armorLayer1Models[id];
	}
	
	public ModelAliceDoll getCurrentArmorModelLayer1() {
		return getArmorModelLayer1(getCurrentDollID());
	}

	private static ModelAliceDoll getArmorModelLayer2(int id) {
		
		if(id<0 || id>=armorLayer2Models.length) {
			id = 0;
		}
		if(armorLayer2Models[id] == null) {
			armorLayer2Models[id] = DollRegistry.getModelInstance(id, 0.125F);
		}
		return armorLayer2Models[id];
	}
	
	public ModelAliceDoll getCurrentArmorModelLayer2() {
		return getArmorModelLayer2(getCurrentDollID());
	}
}
