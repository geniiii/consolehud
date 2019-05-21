package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.util.ConsoleHudRender;
import com.fuzs.consolehud.util.PaperDollPosition;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

@Environment(EnvType.CLIENT)
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
			final boolean crouching = ConsoleHud.CLIENT.player.isSneaking() && this.remainingRidingTicks == 0 && ConsoleHud.CONFIG.paperDollConfig.paperDollCrouching;
			final boolean flying = ConsoleHud.CLIENT.player.abilities.flying && ConsoleHud.CONFIG.paperDollConfig.paperDollFlying;
			final boolean elytra = ConsoleHud.CLIENT.player.isFallFlying() && ConsoleHud.CONFIG.paperDollConfig.paperDollElytraFlying;
			final boolean burning = ConsoleHud.CLIENT.player.isOnFire() && ConsoleHud.CONFIG.paperDollConfig.paperDollBurning;
			final boolean mounting = ConsoleHud.CLIENT.player.isRiding() && ConsoleHud.CONFIG.paperDollConfig.paperDollRiding;
			final boolean swimmingPose = ConsoleHud.CLIENT.player.isInSwimmingPose() && ConsoleHud.CONFIG.paperDollConfig.paperDollSwimmingPose;

			if (ConsoleHud.CONFIG.paperDollConfig.paperDollAlways || crouching || sprinting || burning || elytra || flying || mounting || swimmingPose) {
				this.remainingTicks = 20;
			} else if (this.remainingTicks > 0) {
				this.remainingTicks--;
			}

			if (ConsoleHud.CLIENT.player.isRiding()) {
				this.remainingRidingTicks = 10;
			} else if (this.remainingRidingTicks > 0) {
				this.remainingRidingTicks--;
			}
		}
	}

	private void onInGameHudDraw(float partialTicks) {
		if (ConsoleHud.CLIENT.player != null && ConsoleHud.CONFIG.paperDoll) {
			if (!ConsoleHud.CLIENT.player.isInvisible() && !ConsoleHud.CLIENT.player.isSpectator() && (!ConsoleHud.CLIENT.player.isRiding() || ConsoleHud.CONFIG.paperDollConfig.paperDollRiding || ConsoleHud.CONFIG.paperDollConfig.paperDollAlways) && this.remainingTicks > 0) {
				this.positionOnScreen = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() > PaperDollPosition.BOTTOM_LEFT.ordinal() ? 22.5F : -22.5F;

				if (!this.wasActive) {
					this.rotationYawPrev = this.positionOnScreen;
					this.renderYawOffsetPrev = ConsoleHud.CLIENT.player.field_6283;

					this.wasActive = true;
				}

				final int scale = ConsoleHud.CONFIG.paperDollConfig.paperDollScale * 5;
				final int positionScale = (int) (scale * 1.5F);

				final int scaledWidth = ConsoleHud.CLIENT.window.getScaledWidth();
				final int scaledHeight = ConsoleHud.CLIENT.window.getScaledHeight();

				final int xMargin = ConsoleHud.CONFIG.paperDollConfig.paperDollXOffset / (int) ConsoleHud.CLIENT.window.getScaleFactor();
				final int yMargin = ConsoleHud.CONFIG.paperDollConfig.paperDollYOffset / (int) ConsoleHud.CLIENT.window.getScaleFactor();

				final int x = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() > PaperDollPosition.BOTTOM_LEFT.ordinal() ? scaledWidth - positionScale - xMargin : positionScale + xMargin;
				final int y = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition.ordinal() % 2 == 0 ? (int) (scale * 2.5F) + yMargin : scaledHeight - positionScale - yMargin;

				this.drawEntityOnScreen((x % scaledWidth + scaledWidth) % scaledWidth, (y % scaledHeight + scaledWidth) % scaledWidth, scale, ConsoleHud.CLIENT.player, partialTicks);
			} else if (this.wasActive) {
				this.wasActive = false;
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
			GlStateManager.translatef(posX, posY, 50.0F);
			GlStateManager.scalef(-scale, scale, scale);
			GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);

			final float prevRenderYawOffsetPrev = playerEntity.field_6283;
			final float prevHeadYaw = playerEntity.headYaw;

			GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
			GuiLighting.enable();
			GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(-((float) Math.atan(40 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);

			this.rotateEntity(playerEntity.field_6283 - this.renderYawOffsetPrev, partialTicks);
			this.renderYawOffsetPrev = playerEntity.field_6283;
			playerEntity.field_6283 = this.rotationYawPrev;
			playerEntity.headYaw = this.rotationYawPrev;

			GlStateManager.translatef(0.0F, 0.0F, 0.0F);
			entityRenderDispatcher.method_3945(180.0F);

			entityRenderDispatcher.setRenderShadows(false);
			entityRenderDispatcher.render(playerEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
			entityRenderDispatcher.setRenderShadows(true);

			playerEntity.field_6283 = prevRenderYawOffsetPrev;
			playerEntity.headYaw = prevHeadYaw;

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
		if (this.rotationYawPrev < -this.positionOnScreen) {
			this.rotationYawPrev -= renderYawOffsetDiff;
		} else {
			this.rotationYawPrev += renderYawOffsetDiff;
		}

		if (this.rotationYawPrev > this.positionOnScreen + 45F) {
			this.rotationYawPrev = this.positionOnScreen + 45F;
		} else if (this.rotationYawPrev < this.positionOnScreen - 45F) {
			this.rotationYawPrev = this.positionOnScreen - 45F;
		}

		if (this.rotationYawPrev > this.positionOnScreen + 0.5F) {
			this.rotationYawPrev -= partialTicks * 2F;
		} else if (this.rotationYawPrev < this.positionOnScreen - 0.5F) {
			this.rotationYawPrev += partialTicks * 2F;
		}

		this.rotationYawPrev = Math.round(this.rotationYawPrev * 50F) / 50F;
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
				RenderPaperDoll.this::onInGameHudDraw
			);
		}

		@Override
		public void registerEvents() {
			this.registerClientTickEvent();
			this.registerInGameHudDrawEvent();
		}
	}
}
