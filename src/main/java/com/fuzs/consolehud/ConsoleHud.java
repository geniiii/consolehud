package com.fuzs.consolehud;

import com.fuzs.consolehud.renders.RenderPaperDoll;
import com.fuzs.consolehud.renders.RenderSelectedItem;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	static final String MODID = "consolehud";

	public static RenderPaperDoll RENDER_PAPER_DOLL;
	public static RenderSelectedItem RENDER_SELECTED_ITEM;

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		CONFIG = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new).getConfig();

		RENDER_PAPER_DOLL = new RenderPaperDoll(MinecraftClient.getInstance());
		RENDER_SELECTED_ITEM = new RenderSelectedItem(MinecraftClient.getInstance());

		RENDER_PAPER_DOLL.registerOnClientTickEvent();
		RENDER_SELECTED_ITEM.registerOnClientTickEvent();
	}
}