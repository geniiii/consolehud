package com.fuzs.consolehud;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ConsoleHudMenuApiImpl implements ModMenuApi {
	@Override
	public String getModId() {
		return ConsoleHud.MODID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> AutoConfig.getConfigScreen(ModConfig.class, screen).get();
	}
}
