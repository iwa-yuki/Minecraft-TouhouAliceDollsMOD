package mods.touhou_alice_core.packet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAyaShotHandler implements IMessageHandler<MessageAyaShot, IMessage> {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    private Framebuffer framebufferMc;

	@Override
	public IMessage onMessage(MessageAyaShot message, MessageContext ctx) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		Entity e = mc.theWorld.getEntityByID(message.entityID);
		
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
			}
			catch(Exception ex) {
				doll.chatMessage(doll.getDollName() + " : Skipped screenShot generation.", 3);
				doll.chatMessage(doll.getDollName() + " :   " + ex.getMessage(), 3);
			}
			
	        doll.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY);
	        doll.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY);
	        doll.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY);
		}
		return null;
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

	    // 撮影用にパラメータを変更
	    double posY = doll.posY;
        doll.posY -= (1.62D - doll.getEyeHeight());
        float rotationYaw = doll.rotationYaw;
        doll.rotationYaw = doll.getRotationYawHead();
		
		// 人形をカメラマンに設定
		mc.func_175607_a(doll);
        
        int thirdPersonView = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        
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
                    mc.renderGlobal.func_174975_c();

                    if (mc.entityRenderer.getShaderGroup() != null)
                    {
                        GlStateManager.matrixMode(5890);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        mc.entityRenderer.getShaderGroup().loadShaderGroup(1f);
                        GlStateManager.popMatrix();
                    }

                    mc.getFramebuffer().bindFramebuffer(true);
                }
            }
            else
            {
                GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
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
        
        mc.gameSettings.thirdPersonView = thirdPersonView;

        doll.rotationYaw = rotationYaw;
        doll.posY = posY;

        mc.func_175607_a(mc.thePlayer);

	}
}
