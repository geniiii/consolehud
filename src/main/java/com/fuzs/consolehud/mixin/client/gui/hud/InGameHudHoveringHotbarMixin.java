package com.fuzs.consolehud.mixin.client.gui.hud;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudHoveringHotbarMixin {
	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"
		),
		method = "renderHotbar"
	)
	private void drawHotbarOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V"
		),
		method = "renderHotbar"
	)
	private void drawHotbarItemsOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"
		),
		method = "renderStatusBars"
	)
	private void drawStatusBarsOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"
		),
		method = "renderExperienceBar"
	)
	private void drawExperienceBarOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFI)I"
		),
		method = "renderExperienceBar"
	)
	private void drawExperienceBarTextOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(1, (int) args.get(1) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(2, (int) args.get(2) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;fill(IIIII)V"
		),
		method = "renderHeldItemTooltip"
	)
	private void drawItemTooltipOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
			args.set(2, (int) args.get(2) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(3, (int) args.get(3) + ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"
		),
		method = "renderHeldItemTooltip"
	)
	private void drawItemTooltipTextOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"
		),
		method = "renderMountJumpBar"
	)
	private void drawMountJumpBarOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}

	@ModifyArgs(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;blit(IIIIII)V"
		),
		method = "renderMountHealth"
	)
	private void drawMountHealthOffset(Args args) {
		if (ConsoleHud.CONFIG.hoveringHotbar) {
			args.set(0, (int) args.get(0) + ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset);
			args.set(1, (int) args.get(1) - ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset);
		}
	}
}
