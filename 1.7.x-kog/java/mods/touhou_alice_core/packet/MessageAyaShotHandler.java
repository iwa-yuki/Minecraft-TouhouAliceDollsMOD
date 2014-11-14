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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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

	    mc.renderViewEntity = doll;

	    double posY = mc.renderViewEntity.posY;
        mc.renderViewEntity.posY -= (1.62D - mc.renderViewEntity.getEyeHeight());
        float rotationYaw = mc.renderViewEntity.rotationYaw;
        doll.rotationYaw = mc.renderViewEntity.rotationYawHead;
        
        int thirdPersonView = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        
//        mc.entityRenderer.updateCameraAndRender(1F);
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
        
        mc.gameSettings.thirdPersonView = thirdPersonView;

        mc.renderViewEntity.rotationYaw = rotationYaw;
        mc.renderViewEntity.posY = posY;

        mc.renderViewEntity = mc.thePlayer;

	}
}
