package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.util.ConsoleHudRender;
import com.fuzs.consolehud.util.PaperDollPosition;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

public class RenderPaperDoll implements ConsoleHudRender {
	private int remainingTicks = 0;
	private int remainingRidingTicks = 0;
	private float rotationYawPrev;
	private float renderYawOffsetPrev;
	private float positionOnScreen;
	private boolean wasActive;
	private final EventHandler eventHandler = new EventHandler();

	private void onClientTick() {
		if (ConsoleHud.CLIENT.player != null && !ConsoleHud.CLIENT.isPaused() && ConsoleHud.CONFIG.paperDoll) {
			final boolean sprinting = ConsoleHud.CLIENT.player.isSprinting() && ConsoleHud.CONFIG.paperDollConfig.paperDollSprinting;
			final boolean crouching = ConsoleHud.CLIENT.player.isSneaking() && remainingRidingTicks == 0 && ConsoleHud.CONFIG.paperDollConfig.paperDollCrouching;
			final boolean flying = ConsoleHud.CLIENT.player.abilities.flying && ConsoleHud.CONFIG.paperDollConfig.paperDollFlying;
			final boolean elytra = ConsoleHud.CLIENT.player.isFallFlying() && ConsoleHud.CONFIG.paperDollConfig.paperDollElytraFlying;
			final boolean burning = ConsoleHud.CLIENT.player.isOnFire() && ConsoleHud.CONFIG.paperDollConfig.paperDollBurning;
			final boolean mounting = ConsoleHud.CLIENT.player.isRiding() && ConsoleHud.CONFIG.paperDollConfig.paperDollRiding;
			final boolean swimmingPose = ConsoleHud.CLIENT.player.isInSwimmingPose() && ConsoleHud.CONFIG.paperDollConfig.paperDollSwimmingPose;

			if (ConsoleHud.CONFIG.paperDollConfig.paperDollAlways || crouching || sprinting || burning || elytra || flying || mounting || swimmingPose) {
				remainingTicks = 20;
			} else if (remainingTicks > 0) {
				remainingTicks--;
			}

			if (ConsoleHud.CLIENT.player.isRiding()) {
				remainingRidingTicks = 10;
			} else if (remainingRidingTicks > 0) {
				remainingRidingTicks--;
			}
		}
	}

	private void renderGameOverlayText(float partialTicks) {
		if (ConsoleHud.CLIENT.player != null && ConsoleHud.CONFIG.paperDoll) {
			positionOnScreen = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() > PaperDollPosition.BOTTOM_LEFT.ordinal() ? 22.5F : -22.5F;
			if (!ConsoleHud.CLIENT.player.isInvisible() && !ConsoleHud.CLIENT.player.isSpectator() && (!ConsoleHud.CLIENT.player.isRiding() || ConsoleHud.CONFIG.paperDollConfig.paperDollRiding || ConsoleHud.CONFIG.paperDollConfig.paperDollAlways) && remainingTicks > 0) {
				if (!wasActive) {
					rotationYawPrev = positionOnScreen;
					renderYawOffsetPrev = ConsoleHud.CLIENT.player.field_6283;
					wasActive = true;
				}

				int scale = ConsoleHud.CONFIG.paperDollConfig.paperDollScale * 5;
				int positionScale = (int) (scale * 1.5F);

				int scaledWidth = ConsoleHud.CLIENT.window.getScaledWidth();
				int scaledHeight = ConsoleHud.CLIENT.window.getScaledHeight();

				int xMargin = ConsoleHud.CONFIG.paperDollConfig.paperDollXOffset / (int) ConsoleHud.CLIENT.window.getScaleFactor();
				int yMargin = ConsoleHud.CONFIG.paperDollConfig.paperDollYOffset / (int) ConsoleHud.CLIENT.window.getScaleFactor();

				int x = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() > PaperDollPosition.BOTTOM_LEFT.ordinal() ? scaledWidth - positionScale - xMargin : positionScale + xMargin;
				int y = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() % 2 == 0 ? (int) (scale * 2.5F) + yMargin : scaledHeight - positionScale - yMargin;

				this.drawEntityOnScreen((x % scaledWidth + scaledWidth) % scaledWidth, (y % scaledHeight + scaledWidth) % scaledWidth, scale, ConsoleHud.CLIENT.player, partialTicks);
			} else if (wasActive) {
				wasActive = false;
			}
		}
	}

	/**
	 * Draws an entity on the screen looking toward the cursor.
	 */
	private void drawEntityOnScreen(int posX, int posY, int scale, ClientPlayerEntity playerEntity, float partialTicks) {
		EntityRenderDispatcher entityRenderDispatcher = ConsoleHud.CLIENT.getEntityRenderManager();
		if (entityRenderDispatcher.camera != null) {
			GlStateManager.enableDepthTest();
			GlStateManager.enableColorMaterial();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float) posX, (float) posY, 50.0F);
			GlStateManager.scalef((float) (-scale), (float) scale, (float) scale);
			GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);

			float f = playerEntity.field_6283;
			float f1 = playerEntity.yaw;
			float f2 = playerEntity.pitch;
			float f3 = playerEntity.prevHeadYaw;
			float f4 = playerEntity.headYaw;

			GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
			GuiLighting.enable();
			GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-((float) Math.atan((double) (40 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

			this.rotateEntity(playerEntity.field_6283 - renderYawOffsetPrev, partialTicks);
			renderYawOffsetPrev = playerEntity.field_6283;
			playerEntity.field_6283 = rotationYawPrev;
			playerEntity.headYaw = rotationYawPrev;

			GlStateManager.translatef(0.0F, 0.0F, 0.0F);
			entityRenderDispatcher.method_3945(180.0F);

			entityRenderDispatcher.setRenderShadows(false);
			entityRenderDispatcher.render(playerEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
			entityRenderDispatcher.setRenderShadows(true);

			playerEntity.field_6283 = f;
			playerEntity.yaw = f1;
			playerEntity.pitch = f2;
			playerEntity.prevHeadYaw = f3;
			playerEntity.headYaw = f4;

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

	@Override
	public EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public final class EventHandler implements ConsoleHudRender.EventHandler {
		private void registerClientTickEvent() {
			ClientTickCallback.EVENT.register(
				client -> RenderPaperDoll.this.onClientTick()
			);
		}

		private void registerInGameHudDrawEvent() {
			InGameHudDrawCallback.Pre.EVENT.register(
				RenderPaperDoll.this::renderGameOverlayText
			);
		}

		@Override
		public void registerEvents() {
			this.registerClientTickEvent();
			this.registerInGameHudDrawEvent();
		}
	}
}
