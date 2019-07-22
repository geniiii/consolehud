package com.fuzs.consolehud.helper;


import com.fuzs.consolehud.ConsoleHud;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;

public class PaperDollHelper {
	public static boolean shouldDrawDoll(int remainingRidingTicks) {
		final boolean sprinting = ConsoleHud.CLIENT.player.isSprinting() && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.sprinting;
		final boolean crouching = ConsoleHud.CLIENT.player.isSneaking() && remainingRidingTicks == 0 && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.crouching;
		final boolean flying = ConsoleHud.CLIENT.player.abilities.flying && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.flying;
		final boolean elytra = ConsoleHud.CLIENT.player.isFallFlying() && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.elytraFlying;
		final boolean burning = ConsoleHud.CLIENT.player.isOnFire() && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.burning;
		final boolean mounting = ConsoleHud.CLIENT.player.isRiding() && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.riding;
		final boolean swimmingPose = ConsoleHud.CLIENT.player.isInSwimmingPose() && ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.swimmingPose;

		return crouching || sprinting || burning || elytra || flying || mounting || swimmingPose;

	}

	/**
	 * Draws an entity on the screen looking toward the cursor.
	 */
	public static float drawPlayer(int posX, int posY, int scale, ClientPlayerEntity playerEntity, float partialTicks, float prevRotationYaw) {

		GlStateManager.enableDepthTest();
		GlStateManager.enableColorMaterial();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();

		// set position and scale
		GlStateManager.translatef(posX, posY, 50.0F);
		GlStateManager.scalef(-scale, scale, scale);

		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GuiLighting.enable();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);

		// save rotation as we don't want to change the actual entity
		final float f = playerEntity.field_6283;
		final float f1 = playerEntity.headYaw;

		if (!ConsoleHud.CONFIG.paperDollConfig.blockRotation) {
			// head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
			prevRotationYaw = rotatePlayer(prevRotationYaw, playerEntity.headYaw - playerEntity.prevHeadYaw, partialTicks);
		} else {
			prevRotationYaw = 0;
		}

		playerEntity.field_6283 = playerEntity.headYaw = ConsoleHud.CONFIG.paperDollConfig.position.getRotation(22.5F) + prevRotationYaw;

		// do render
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		EntityRenderDispatcher entityRenderDispatcher = ConsoleHud.CLIENT.getEntityRenderManager();
		entityRenderDispatcher.method_3945(180.0F);
		entityRenderDispatcher.setRenderShadows(false);
		entityRenderDispatcher.render(playerEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		entityRenderDispatcher.setRenderShadows(true);

		// reset entity rotation
		playerEntity.field_6283 = f;
		playerEntity.headYaw = f1;

		GlStateManager.popMatrix();
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.disableDepthTest();
		GlStateManager.disableColorMaterial();

		return prevRotationYaw;

	}

	/**
	 * Rotate entity according to its yaw, slowly spin back to default when yaw stays constant for a while
	 */
	private static float rotatePlayer(float rotationYaw, float renderYawOffsetDiff, float partialTicks) {

		if (ConsoleHud.CLIENT.isPaused()) {
			return rotationYaw;
		}

		// apply rotation change from entity
		if (Math.abs(renderYawOffsetDiff) >= 0.05F) {
			rotationYaw += renderYawOffsetDiff;
		}

		rotationYaw = MathHelper.clamp(rotationYaw, -45.0F, 45.0F);

		// rotate back to origin, never
		// overshoot 0
		if (rotationYaw < -0.05F) {
			rotationYaw = Math.min(0, rotationYaw + partialTicks * 2.0F);
		} else if (rotationYaw > 0.05F) {
			rotationYaw = Math.max(0, rotationYaw - partialTicks * 2.0F);
		}

		return rotationYaw;

	}

	public static int getPotionShift(Collection<StatusEffectInstance> collection) {

		int shift = 0;
		final boolean renderInHUD = collection.stream().anyMatch(StatusEffectInstance::shouldShowIcon);
		final boolean doesShowParticles = collection.stream().anyMatch(StatusEffectInstance::shouldShowParticles);

		if (!collection.isEmpty() && renderInHUD && doesShowParticles) {
			shift += collection.stream().anyMatch(it -> !it.getEffectType().getType().equals(StatusEffectType.BENEFICIAL)) ? 50 : 25;
		}

		return shift;

	}

}
