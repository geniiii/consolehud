package com.fuzs.consolehud.mixin.client.gui.hud;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface InGameHudAccessorMixin {
	@Accessor
	int getHeldItemTooltipFade();

	@Accessor
	ItemStack getCurrentStack();

	@Accessor
	void setHeldItemTooltipFade(int value);

	@Accessor
	void setCurrentStack(ItemStack value);
}
