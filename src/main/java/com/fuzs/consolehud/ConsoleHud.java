package com.fuzs.consolehud;

import com.fuzs.consolehud.renders.RenderPaperDoll;
import com.fuzs.consolehud.renders.RenderSelectedItem;
import io.github.cottonmc.cotton.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	public static final String MODID = "consolehud";

	public static RenderPaperDoll RENDER_PAPER_DOLL;
	public static RenderSelectedItem RENDER_SELECTED_ITEM;

	public static final Config CONFIG = ConfigManager.loadConfig(Config.class);

	@Override
	public void onInitializeClient() {
		RENDER_PAPER_DOLL = new RenderPaperDoll(MinecraftClient.getInstance());
		RENDER_SELECTED_ITEM = new RenderSelectedItem(MinecraftClient.getInstance());

		RENDER_PAPER_DOLL.onClientTick();
		RENDER_SELECTED_ITEM.onClientTick();
	}
}