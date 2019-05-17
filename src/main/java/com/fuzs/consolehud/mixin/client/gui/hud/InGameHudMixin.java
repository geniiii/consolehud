package com.fuzs.consolehud.mixin.client.gui.hud;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
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
}
