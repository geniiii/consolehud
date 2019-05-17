package com.fuzs.consolehud;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ConsoleHudMenuApiImpl implements ModMenuApi {
	@Override
	public String getModId() {
		return ConsoleHud.MODID;
	}

	@Override
	public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
		return Optional.of(AutoConfig.getConfigScreen(ModConfig.class, screen));
	}
}
