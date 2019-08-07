package com.fuzs.consolehud;

import com.fuzs.consolehud.handler.CoordinateDisplayHandler;
import com.fuzs.consolehud.handler.PaperDollHandler;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class ConsoleHud implements ClientModInitializer {
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static final String MODID = "consolehud";
	private static final PaperDollHandler PAPER_DOLL_HANDLER = new PaperDollHandler();
	private static final CoordinateDisplayHandler COORDINATE_DISPLAY_HANDLER = new CoordinateDisplayHandler();

	public static ModConfig CONFIG = null;

	@Override
	public void onInitializeClient() {
		ConsoleHud.CONFIG = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new).getConfig();

		ConsoleHud.PAPER_DOLL_HANDLER.getEventHandler().registerEvents();
		ConsoleHud.COORDINATE_DISPLAY_HANDLER.getEventHandler().registerEvents();
	}
}