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
			
	        this.framebufferMc = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
	        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

	        this.framebufferMc.bindFramebuffer(true);
	        mc.renderViewEntity = doll;
	        mc.entityRenderer.updateCameraAndRender(20f);
	        mc.renderViewEntity = null;
	        //this.render(mc, doll);
	        this.framebufferMc.unbindFramebuffer();
	        this.framebufferMc.framebufferRender(mc.displayWidth, mc.displayHeight);
			
			IChatComponent icc = ScreenShotHelper.saveScreenshot(mc.mcDataDir, this.getSSFileName(doll), mc.displayWidth, mc.displayHeight, this.framebufferMc);
			doll.chatMessage(icc.getFormattedText(), 3);
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
	private void render(Minecraft mc, EntityAliceDoll doll) {

//        boolean flag = Display.isActive();
//
//        if (mc.inGameHasFocus && flag)
//        {
//            this.mc.mouseHelper.mouseXYChange();
//            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
//            float f2 = f1 * f1 * f1 * 8.0F;
//            float f3 = (float)this.mc.mouseHelper.deltaX * f2;
//            float f4 = (float)this.mc.mouseHelper.deltaY * f2;
//            byte b0 = 1;
//
//            if (this.mc.gameSettings.invertMouse)
//            {
//                b0 = -1;
//            }
//
//            mc.thePlayer.setAngles(f3, f4 * (float)b0);
//        }
		double prevPosX = mc.thePlayer.posX;
		double prevPosY = mc.thePlayer.posY;
		double prevPosZ = mc.thePlayer.posZ;
		float prevYaw = mc.thePlayer.rotationYaw;
		float prevYawHead = mc.thePlayer.rotationYawHead;
		float prevPitch = mc.thePlayer.rotationPitch;
		
		mc.thePlayer.posX = doll.posX;
		mc.thePlayer.posY = doll.posY;
		mc.thePlayer.posZ = doll.posZ;
		mc.thePlayer.rotationYaw = doll.rotationYaw;
		mc.thePlayer.rotationYawHead = doll.rotationYawHead;
		mc.thePlayer.rotationPitch = doll.rotationPitch;

        if (!mc.skipRenderWorld)
        {
            boolean anaglyphEnable = mc.gameSettings.anaglyph;
            final ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            final int k = Mouse.getX() * i / mc.displayWidth;
            final int l = j - Mouse.getY() * j / mc.displayHeight - 1;
            int i1 = mc.gameSettings.limitFramerate;

            if (mc.theWorld != null)
            {
//                if (mc.isFramerateLimitBelowMax())
//                {
//                    mc.entityRenderer.renderWorld(20, this.renderEndNanoTime + (long)(1000000000 / i1));
//                }
//                else
//                {
            		mc.entityRenderer.renderWorld(20f, 0L);
//                }

                if (OpenGlHelper.shadersSupported)
                {
                    if (mc.entityRenderer.theShaderGroup != null)
                    {
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        mc.entityRenderer.theShaderGroup.loadShaderGroup(20f);
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

            if (mc.currentScreen != null)
            {
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

                try
                {
                    if (!MinecraftForge.EVENT_BUS.post(new DrawScreenEvent.Pre(mc.currentScreen, k, l, 20f))) {
                        mc.currentScreen.drawScreen(k, l, 20f);
                    }
                    MinecraftForge.EVENT_BUS.post(new DrawScreenEvent.Post(mc.currentScreen, k, l, 20f));
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
//                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
//                    crashreportcategory.addCrashSectionCallable("Screen name", new Callable()
//                    {
//                        private static final String __OBFID = "CL_00000948";
//                        public String call()
//                        {
//                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
//                        }
//                    });
//                    crashreportcategory.addCrashSectionCallable("Mouse location", new Callable()
//                    {
//                        private static final String __OBFID = "CL_00000950";
//                        public String call()
//                        {
//                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] {Integer.valueOf(k), Integer.valueOf(l), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
//                        }
//                    });
//                    crashreportcategory.addCrashSectionCallable("Screen size", new Callable()
//                    {
//                        private static final String __OBFID = "CL_00000951";
//                        public String call()
//                        {
//                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[] {Integer.valueOf(scaledresolution.getScaledWidth()), Integer.valueOf(scaledresolution.getScaledHeight()), Integer.valueOf(EntityRenderer.this.mc.displayWidth), Integer.valueOf(EntityRenderer.this.mc.displayHeight), Integer.valueOf(scaledresolution.getScaleFactor())});
//                        }
//                    });
                    throw new ReportedException(crashreport);
                }
            }

    		mc.thePlayer.posX = prevPosX;
    		mc.thePlayer.posY = prevPosY;
    		mc.thePlayer.posZ = prevPosZ;
    		mc.thePlayer.rotationYaw = prevYaw;
    		mc.thePlayer.rotationYawHead = prevYawHead;
    		mc.thePlayer.rotationPitch = prevPitch;
        }

	}
}
