package site.geni.renderevents.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

@Mixin(InGameHud.class)
public abstract class InGameHudDrawMixin {
	@Inject(
		at = @At(
			value = "HEAD"
		),
		method = "render"
	)
	private void drawPre(float partialTicks, CallbackInfo ci) {
		if (!ci.isCancelled()) {
			InGameHudDrawCallback.Pre.EVENT.invoker().draw(partialTicks);
		}
	}

	@Inject(
		at = @At(
			value = "RETURN"
		),
		method = "render"
	)
	private void drawPost(float partialTicks, CallbackInfo ci) {
		if (!ci.isCancelled()) {
			InGameHudDrawCallback.Post.EVENT.invoker().draw(partialTicks);
		}
	}
}
