package com.fuzs.consolehud;

import com.fuzs.consolehud.handler.PaperDollHandler;
import com.fuzs.consolehud.handler.SelectedItemHandler;
import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	static final String MODID = "consolehud";
	private static final PaperDollHandler RENDER_PAPER_DOLL = new PaperDollHandler();
	private static final SelectedItemHandler RENDER_SELECTED_ITEM = new SelectedItemHandler();

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		ConsoleHud.CONFIG = ConfigManager.loadConfig(ModConfig.class);

		ConsoleHud.RENDER_PAPER_DOLL.getEventHandler().registerEvents();
		ConsoleHud.RENDER_SELECTED_ITEM.getEventHandler().registerEvents();
	}
}