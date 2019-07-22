package com.fuzs.consolehud;

import com.fuzs.consolehud.renders.RenderPaperDoll;
import com.fuzs.consolehud.renders.RenderSelectedItem;
import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	static final String MODID = "consolehud";
	private static final RenderPaperDoll RENDER_PAPER_DOLL = new RenderPaperDoll();
	private static final RenderSelectedItem RENDER_SELECTED_ITEM = new RenderSelectedItem();

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		ConsoleHud.CONFIG = ConfigManager.loadConfig(ModConfig.class);

		ConsoleHud.RENDER_PAPER_DOLL.getEventHandler().registerEvents();
		ConsoleHud.RENDER_SELECTED_ITEM.getEventHandler().registerEvents();
	}
}