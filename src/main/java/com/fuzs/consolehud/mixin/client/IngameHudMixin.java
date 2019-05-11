package com.fuzs.consolehud.mixin.client;

import com.fuzs.consolehud.ConsoleHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(InGameHud.class)
public class IngameHudMixin {
	@Inject(
		at = @At(
			value = "HEAD"
		),
		method = "draw"
	)
	private void draw(float partialTicks, CallbackInfo ci) {
		ConsoleHud.RENDER_SELECTED_ITEM.renderGameOverlayText();
		ConsoleHud.RENDER_PAPER_DOLL.renderGameOverlayText(partialTicks);
	}
}
