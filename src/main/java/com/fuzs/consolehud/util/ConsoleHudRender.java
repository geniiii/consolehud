package com.fuzs.consolehud.util;

public interface ConsoleHudRender {
	EventHandler getEventHandler();

	interface EventHandler {
		void registerEvents();
	}
}
