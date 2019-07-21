package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.helper.PaperDollHelper;
import com.fuzs.consolehud.util.ConsoleHudRender;
import com.fuzs.consolehud.util.PaperDollPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

@Environment(EnvType.CLIENT)
public class RenderPaperDoll implements ConsoleHudRender {
	private int remainingTicks = 0;
	private int remainingRidingTicks = 0;
	private float rotationYawPrev;
	private final EventHandler eventHandler = new EventHandler();

	private void onClientTick() {
		if (!ConsoleHud.CLIENT.isPaused() && ConsoleHud.CLIENT.player != null) {
			if (ConsoleHud.CONFIG.paperDoll && (ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.always || PaperDollHelper.shouldDrawDoll(this.remainingRidingTicks))) {
				this.remainingTicks = ConsoleHud.CONFIG.paperDollConfig.displayTime;
			} else if (this.remainingTicks > 0) {
				this.remainingTicks--;
			} else {
				this.rotationYawPrev = 0;
			}

			// don't show paper doll in sneaking position after unmounting a vehicle / mount
			if (ConsoleHud.CONFIG.paperDoll && ConsoleHud.CLIENT.player.isRiding()) {
				this.remainingRidingTicks = 10;
			} else if (remainingRidingTicks > 0) {
				this.remainingRidingTicks--;
			}

		}
	}

	private void onInGameHudDraw(float partialTicks) {
		if (ConsoleHud.CLIENT.player != null) {
			final boolean riding = ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.always || ConsoleHud.CONFIG.paperDollConfig.displayActionsConfig.riding || !ConsoleHud.CLIENT.player.isRiding();

			if (!ConsoleHud.CLIENT.player.isInvisible() && !ConsoleHud.CLIENT.player.isSpectator() && ConsoleHud.CLIENT.options.perspective == 0 && riding && this.remainingTicks > 0) {

				final int scale = ConsoleHud.CONFIG.paperDollConfig.paperDollScale * 5;
				final PaperDollPosition position = ConsoleHud.CONFIG.paperDollConfig.paperDollPosition;

				final int x = position.getX(0, ConsoleHud.CLIENT.window.getScaledWidth(), (int) (scale * 1.5F) + ConsoleHud.CONFIG.paperDollConfig.paperDollXOffset);

				// can't use EnumPositionPreset#getY as the orientation point isn't in the top left corner of the image
				final int yOffset = ConsoleHud.CONFIG.paperDollConfig.paperDollYOffset;
				int y = position.isBottom() ? ConsoleHud.CLIENT.window.getScaledHeight() - scale - yOffset : (int) (scale * 2.5F) + yOffset;

				if (ConsoleHud.CONFIG.paperDollConfig.potionShift && position.shouldShift()) {
					y += PaperDollHelper.getPotionShift(ConsoleHud.CLIENT.player.getStatusEffects());
				}

				this.rotationYawPrev = PaperDollHelper.drawPlayer(x, y, scale, ConsoleHud.CLIENT.player, partialTicks, this.rotationYawPrev);

			}

		}
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
