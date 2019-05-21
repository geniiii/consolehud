package com.fuzs.consolehud.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ConsoleHudRender {
	EventHandler getEventHandler();

	interface EventHandler {
		void registerEvents();
	}
}
