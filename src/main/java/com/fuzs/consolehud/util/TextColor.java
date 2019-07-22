package com.fuzs.consolehud.util;


import net.minecraft.util.Formatting;

@SuppressWarnings("unused")
public enum TextColor {

	WHITE("white", Formatting.WHITE),
	ORANGE("orange", Formatting.GOLD),
	MAGENTA("magenta", Formatting.AQUA),
	LIGHT_BLUE("light_blue", Formatting.BLUE),
	YELLOW("yellow", Formatting.YELLOW),
	LIME("lime", Formatting.GREEN),
	PINK("pink", Formatting.LIGHT_PURPLE),
	GRAY("gray", Formatting.DARK_GRAY),
	SILVER("silver", Formatting.GRAY),
	CYAN("cyan", Formatting.DARK_AQUA),
	PURPLE("purple", Formatting.DARK_PURPLE),
	BLUE("blue", Formatting.DARK_BLUE),
	BROWN("brown", Formatting.RED),
	GREEN("green", Formatting.DARK_GREEN),
	RED("red", Formatting.DARK_RED),
	BLACK("black", Formatting.BLACK);

	private final String unlocalizedName;
	private final Formatting chatColor;

	TextColor(String unlocalizedNameIn, Formatting chatColorIn) {
		this.unlocalizedName = unlocalizedNameIn;
		this.chatColor = chatColorIn;
	}

	public String toString() {
		return this.unlocalizedName;
	}

	public Formatting getChatColor() {
		return this.chatColor;
	}

}