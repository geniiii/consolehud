package com.fuzs.consolehud;

import com.fuzs.consolehud.renders.RenderPaperDoll;
import com.fuzs.consolehud.renders.RenderSelectedItem;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	static final String MODID = "consolehud";

	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private static final RenderPaperDoll RENDER_PAPER_DOLL = new RenderPaperDoll();
	private static final RenderSelectedItem RENDER_SELECTED_ITEM = new RenderSelectedItem();

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		ConsoleHud.CONFIG = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new).getConfig();

		ConsoleHud.RENDER_PAPER_DOLL.getEventHandler().registerEvents();
		ConsoleHud.RENDER_SELECTED_ITEM.getEventHandler().registerEvents();
	}
}