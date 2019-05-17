package com.fuzs.consolehud.mixin.client.gui.hud;

import net.minecraft.client.gui.DrawableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawableHelper.class)
public interface DrawableHelperAccessorMixin {
	@Accessor
	int getBlitOffset();

	@Accessor
	void setBlitOffset(int value);

	@Invoker
	void callBlit(int a, int b, int c, int d, int e, int f);
}
