package com.fuzs.consolehud;

import com.fuzs.consolehud.handler.PaperDollHandler;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	static final String MODID = "consolehud";
	private static final PaperDollHandler RENDER_PAPER_DOLL = new PaperDollHandler();

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		ConsoleHud.CONFIG = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new).getConfig();

		ConsoleHud.RENDER_PAPER_DOLL.getEventHandler().registerEvents();
	}
}