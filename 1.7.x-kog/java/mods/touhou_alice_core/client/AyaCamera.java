package mods.touhou_alice_core.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class AyaCamera {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    private Framebuffer framebufferMc;
    
	public boolean shot(int entityID)
	{
		boolean result = false;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		Entity e = mc.theWorld.getEntityByID(entityID);
		
		if(e instanceof EntityAliceDoll) {
			EntityAliceDoll doll = (EntityAliceDoll)e;
			
			try{
		        this.framebufferMc = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
		        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
	
		        this.framebufferMc.bindFramebuffer(true);
		        this.renderScreenShot(mc, doll);
		        this.framebufferMc.unbindFramebuffer();
		        this.framebufferMc.framebufferRender(mc.displayWidth, mc.displayHeight);
				
				IChatComponent icc = ScreenShotHelper.saveScreenshot(mc.mcDataDir, this.getSSFileName(doll), mc.displayWidth, mc.displayHeight, this.framebufferMc);
				doll.chatMessage(icc.getFormattedText(), 3);
				result = true;
			}
			catch(Exception ex) {
				doll.chatMessage(doll.getDollName() + " : Skipped screenShot generation.", 3);
				doll.chatMessage(doll.getDollName() + " :   " + ex.getMessage(), 3);
			}
			
	        doll.spawnParticle("happyVillager");
	        doll.spawnParticle("happyVillager");
	        doll.spawnParticle("happyVillager");
		}
		return result;
	}
	
	/**
	 * スクリーンショットのファイル名を取得
	 * @return
	 */
	private String getSSFileName(EntityAliceDoll doll) {
		String s = dateFormat.format(new Date()).toString();
		return doll.getDollName() + "-camera_" + s + ".png";
	}
	
	/**
	 * スクリーンショット生成のためのレンダリング
	 */
	private void renderScreenShot(Minecraft mc, EntityAliceDoll doll) {
		
		// 人形をカメラマンに設定
	    mc.renderViewEntity = doll;

	    // 撮影用にパラメータを変更
	    double posY = mc.renderViewEntity.posY;
        mc.renderViewEntity.posY -= (1.62D - mc.renderViewEntity.getEyeHeight());
        float rotationYaw = mc.renderViewEntity.rotationYaw;
        doll.rotationYaw = mc.renderViewEntity.rotationYawHead;
        
        int thirdPersonView = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        boolean hideGUI = mc.gameSettings.hideGUI;
        mc.gameSettings.hideGUI = true;
        
        PotionEffect dollNightVisionEffect = null;
        PotionEffect dollBlindnessEffect = null;
        PotionEffect playerNightVisionEffect = null;
        PotionEffect playerBlindnessEffect = null;
        
        if(doll.isPotionActive(Potion.nightVision))
        {
        	dollNightVisionEffect = doll.getActivePotionEffect(Potion.nightVision);
        	doll.removePotionEffect(Potion.nightVision.getId());
        }
        if(mc.thePlayer.isPotionActive(Potion.nightVision))
        {
        	playerNightVisionEffect = mc.thePlayer.getActivePotionEffect(Potion.nightVision);
        	doll.addPotionEffect(playerNightVisionEffect);
        }

        if(doll.isPotionActive(Potion.blindness))
        {
        	dollBlindnessEffect = doll.getActivePotionEffect(Potion.blindness);
        	doll.removePotionEffect(Potion.blindness.getId());
        }
        if(mc.thePlayer.isPotionActive(Potion.blindness))
        {
        	playerBlindnessEffect = mc.thePlayer.getActivePotionEffect(Potion.blindness);
        	doll.addPotionEffect(playerBlindnessEffect);
        }
        
        // レンダー実行
        //   mc.entityRenderer.updateCameraAndRender(1F)から不要な処理を除いたもの
        if (!mc.skipRenderWorld)
        {
            if (mc.theWorld != null)
            {
            	mc.entityRenderer.renderWorld(1f, 0L);

                if (OpenGlHelper.shadersSupported)
                {
                    if (mc.entityRenderer.theShaderGroup != null)
                    {
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        mc.entityRenderer.theShaderGroup.loadShaderGroup(1f);
                        GL11.glPopMatrix();
                    }

                    mc.getFramebuffer().bindFramebuffer(true);
                }
            }
            else
            {
                GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                mc.entityRenderer.setupOverlayRendering();
            }
        }
        
        // パラメータを元の状態に戻す
        if(playerBlindnessEffect != null) {
        	doll.removePotionEffect(Potion.blindness.getId());
        }
        if(dollBlindnessEffect != null) {
        	doll.addPotionEffect(dollBlindnessEffect);
        }
        
        if(playerNightVisionEffect != null) {
        	doll.removePotionEffect(Potion.nightVision.getId());
        }
        if(dollNightVisionEffect != null) {
        	doll.addPotionEffect(dollNightVisionEffect);
        }
        
        mc.gameSettings.hideGUI = hideGUI;
        mc.gameSettings.thirdPersonView = thirdPersonView;

        mc.renderViewEntity.rotationYaw = rotationYaw;
        mc.renderViewEntity.posY = posY;

        mc.renderViewEntity = mc.thePlayer;

	}

}
