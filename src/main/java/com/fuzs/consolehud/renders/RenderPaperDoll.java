package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;

public class RenderPaperDoll {
	private MinecraftClient mc;
	private int remainingTicks = 0;
	private int remainingRidingTicks = 0;
	private float rotationYawPrev;
	private float renderYawOffsetPrev;
	private float positionOnScreen;
	private boolean wasActive;

	public RenderPaperDoll(MinecraftClient mcIn) {
		mc = mcIn;
	}

	public void onClientTick() {
		ClientTickCallback.EVENT.register(
			event -> {
				if (this.mc.isPaused() || !ConsoleHud.CONFIG.paperDoll)
					return;
				if (this.mc.player != null) {
					boolean sprinting = mc.player.isSprinting() && ConsoleHud.CONFIG.paperDollSprinting;
					boolean crouching = mc.player.isSneaking() && remainingRidingTicks == 0 && ConsoleHud.CONFIG.paperDollCrouching;
					boolean flying = mc.player.abilities.flying && ConsoleHud.CONFIG.paperDollFlying;
					boolean elytra = mc.player.isFallFlying() && ConsoleHud.CONFIG.paperDollElytraFlying;
					boolean burning = mc.player.isOnFire() && ConsoleHud.CONFIG.paperDollBurning;
					boolean mounting = mc.player.isRiding() && ConsoleHud.CONFIG.paperDollRiding;

					if (ConsoleHud.CONFIG.paperDollAlways || crouching || sprinting || burning || elytra || flying || mounting) {
						remainingTicks = 20;
					} else if (remainingTicks > 0) {
						remainingTicks--;
					}

					if (mc.player.isRiding()) {
						remainingRidingTicks = 10;
					} else if (remainingRidingTicks > 0) {
						remainingRidingTicks--;
					}
				}
			}
		);
	}

	public void renderGameOverlayText(float partialTicks) {
		if (this.mc.player != null && ConsoleHud.CONFIG.paperDoll) {
			positionOnScreen = ConsoleHud.CONFIG.paperDollPosition > 1 ? 22.5F : -22.5F;
			if (!mc.player.isInvisible() && !mc.player.isSpectator() && (!mc.player.isRiding() || ConsoleHud.CONFIG.paperDollRiding || ConsoleHud.CONFIG.paperDollAlways) && remainingTicks > 0) {
				if (!wasActive) {
					rotationYawPrev = positionOnScreen;
					renderYawOffsetPrev = mc.player.field_6283;
					wasActive = true;
				}
				int scale = ConsoleHud.CONFIG.paperDollScale * 5;
				int positionScale = (int) (scale * 1.5F);
				int scaledWidth = this.mc.window.getScaledWidth();
				int scaledHeight = this.mc.window.getScaledHeight();
				int xMargin = ConsoleHud.CONFIG.paperDollXOffset / (int) this.mc.window.getScaleFactor();
				int yMargin = ConsoleHud.CONFIG.paperDollYOffset / (int) this.mc.window.getScaleFactor();
				int x = ConsoleHud.CONFIG.paperDollPosition > 1 ? scaledWidth - positionScale - xMargin : positionScale + xMargin;
				int y = ConsoleHud.CONFIG.paperDollPosition % 2 == 0 ? (int) (scale * 2.5F) + yMargin : scaledHeight - positionScale - yMargin;
				drawEntityOnScreen((x % scaledWidth + scaledWidth) % scaledWidth, (y % scaledHeight + scaledWidth) % scaledWidth, scale, mc.player, partialTicks);
			} else if (wasActive) {
				wasActive = false;
			}
		}
	}

	/**
	 * Draws an entity on the screen looking toward the cursor.
	 */
	private void drawEntityOnScreen(int posX, int posY, int scale, LivingEntity ent, float partialTicks) {
		GlStateManager.enableDepthTest();
		GlStateManager.enableColorMaterial();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) posX, (float) posY, 50.0F);
		GlStateManager.scalef((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.field_6283;
		float f1 = ent.yaw;
		float f2 = ent.pitch;
		float f3 = ent.prevHeadYaw;
		float f4 = ent.headYaw;
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GuiLighting.enable();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-((float) Math.atan((double) (40 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		rotateEntity(ent.field_6283 - renderYawOffsetPrev, partialTicks);
		renderYawOffsetPrev = ent.field_6283;
		ent.field_6283 = rotationYawPrev;
		ent.headYaw = rotationYawPrev;
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		EntityRenderDispatcher rendermanager = MinecraftClient.getInstance().getEntityRenderManager();
		rendermanager.method_3945(180.0F);
		rendermanager.setRenderShadows(false);
		rendermanager.render(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadows(true);
		ent.field_6283 = f;
		ent.yaw = f1;
		ent.pitch = f2;
		ent.prevHeadYaw = f3;
		ent.headYaw = f4;
		GlStateManager.popMatrix();
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.disableDepthTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	}

	/**
	 * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
	 */
	private void rotateEntity(float renderYawOffsetDiff, float partialTicks) {
		if (rotationYawPrev < -positionOnScreen) {
			rotationYawPrev -= renderYawOffsetDiff;
		} else {
			rotationYawPrev += renderYawOffsetDiff;
		}
		if (rotationYawPrev > positionOnScreen + 45F) {
			rotationYawPrev = positionOnScreen + 45F;
		} else if (rotationYawPrev < positionOnScreen - 45F) {
			rotationYawPrev = positionOnScreen - 45F;
		}
		if (rotationYawPrev > positionOnScreen + 0.5F) {
			rotationYawPrev -= partialTicks * 2F;
		} else if (rotationYawPrev < positionOnScreen - 0.5F) {
			rotationYawPrev += partialTicks * 2F;
		}
		rotationYawPrev = Math.round(rotationYawPrev * 50F) / 50F;
	}
}
