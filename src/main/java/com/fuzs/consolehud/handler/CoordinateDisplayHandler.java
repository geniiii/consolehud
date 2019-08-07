package com.fuzs.consolehud.handler;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.util.ConsoleHudRender;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

public class CoordinateDisplayHandler implements ConsoleHudRender {
	private final EventHandler eventHandler = new EventHandler();

	@SuppressWarnings("unused")
	private void onInGameHudDraw(float delta) {
		if (!ConsoleHud.CONFIG.coordinateDisplay || ConsoleHud.CLIENT.options.debugEnabled) {
			return;
		}

		Text text;
		double d = Math.pow(10, ConsoleHud.CONFIG.coordinateDisplayConfig.decimalPlaces);
		double posX = Math.round(ConsoleHud.CLIENT.player.x * d) / d;
		double posY = Math.round(ConsoleHud.CLIENT.player.getBoundingBox().minY * d) / d;
		double posZ = Math.round(ConsoleHud.CLIENT.player.z * d) / d;

		final boolean decimalPlaces = ConsoleHud.CONFIG.coordinateDisplayConfig.decimalPlaces == 0;
		text = new TranslatableText("screen.coordinates", decimalPlaces ? (int) posX : posX, decimalPlaces ? (int) posY : posY, decimalPlaces ? (int) posZ : posZ);

		int f = (int) ((ConsoleHud.CLIENT.options.chatOpacity * 0.9f + 0.1f) * 255.0f);
		int width = ConsoleHud.CLIENT.textRenderer.getStringWidth(text.getString());
		int x = ConsoleHud.CONFIG.coordinateDisplayConfig.xOffset;
		int y = ConsoleHud.CONFIG.coordinateDisplayConfig.yOffset;
		int i = ConsoleHud.CONFIG.coordinateDisplayConfig.backgroundBorder;

		if (ConsoleHud.CONFIG.coordinateDisplayConfig.background) {
			Screen.fill(x, y, x + width + i * 2, y + 7 + i * 2, f / 2 << 24);
		}

		ConsoleHud.CLIENT.textRenderer.drawWithShadow(text.asFormattedString(), x + i, y + i, 0xffffff + (f << 24));

	}

	@Override
	public EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public final class EventHandler implements ConsoleHudRender.EventHandler {
		private void registerInGameHudDrawEvent() {
			InGameHudDrawCallback.Pre.EVENT.register(
				CoordinateDisplayHandler.this::onInGameHudDraw
			);
		}

		@Override
		public void registerEvents() {
			this.registerInGameHudDrawEvent();
		}
	}
}
