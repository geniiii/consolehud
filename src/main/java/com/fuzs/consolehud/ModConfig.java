package com.fuzs.consolehud;

import com.fuzs.consolehud.util.PaperDollPosition;
import com.fuzs.consolehud.util.TextColor;
import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1.shadowed.blue.endless.jankson.Comment;

@Config(name = ConsoleHud.MODID)
public class ModConfig implements ConfigData {
	@Comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and more.")
	public boolean heldItemTooltips = true;

	@Comment("Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.")
	public boolean paperDoll = true;

	@Comment("Enables the hotbar to hover anywhere on the screen. By default, it moves it up 18px from its original position.")
	public boolean hoveringHotbar = true;

	@Comment("Always show player coordinates on screen.")
	public boolean coordinateDisplay = true;

	@ConfigEntry.Category("selected_item")
	@ConfigEntry.Gui.TransitiveObject
	public SelectedItemConfig heldItemTooltipsConfig = new SelectedItemConfig();

	@ConfigEntry.Category("paper_doll")
	@ConfigEntry.Gui.TransitiveObject
	public PaperDollConfig paperDollConfig = new PaperDollConfig();

	@ConfigEntry.Category("hovering_hotbar")
	@ConfigEntry.Gui.TransitiveObject
	public HoveringHotbarConfig hoveringHotbarConfig = new HoveringHotbarConfig();

	@ConfigEntry.Category("coordinate_display")
	@ConfigEntry.Gui.TransitiveObject
	public CoordinateDisplayConfig coordinateDisplayConfig = new CoordinateDisplayConfig();

	public static class SelectedItemConfig implements ConfigData {
		@ConfigEntry.Gui.CollapsibleObject
		public SelectedItemAppearanceConfig appearanceConfig = new SelectedItemAppearanceConfig();

		@Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
		public String[] blacklist = new String[]{};

		@ConfigEntry.BoundedDiscrete(min = 2, max = 7)
		@Comment("Maximum amount of rows to be displayed for held item tooltips.")
		public int rows = 5;

		@Comment("Offset on x-axis from screen center.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from screen center.")
		public int yOffset = 59;

		@Comment("Amount of ticks the held item tooltip will be displayed for.")
		public int displayTime = 40;

		@Comment("Cache the tooltip so it doesn't have to be remade every tick. This will prevent it from updating stats like durability while it is displayed.")
		public boolean cacheTooltip = true;

		@Comment("Tie held item tooltips position to the hovering hotbar feature.")
		public boolean tied = true;

		public static class SelectedItemAppearanceConfig implements ConfigData {
			@Comment("Enables tooltip information added by other mods like Hwyla to be displayed as a held item tooltip.")
			public boolean moddedTooltips = false;

			@Comment("Displays the item's durability as part of its held item tooltip.")
			public boolean showDurability = true;

			@Comment("Force the durability to always be on the tooltip. \"Show Durability\" has to be enabled for this to have any effect.")
			public boolean forceDurability = true;

			@Comment("Show how many more lines there are that currently don't fit the tooltip.")
			public boolean showLastLine = true;

			@Comment("Default text color. Only applied when the text doesn't already have a color assigned internally.")
			public TextColor textColor = TextColor.SILVER;
		}
	}

	public static class PaperDollConfig implements ConfigData {
		@Comment("Defines a screen corner to display the paper doll in.")
		public PaperDollPosition position = PaperDollPosition.TOP_LEFT;

		@ConfigEntry.BoundedDiscrete(min = 1, max = 24)
		@Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
		public int scale = 4;

		@Comment("Offset on x-axis from original doll position.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from original doll position.")
		public int yOffset = 0;

		@Comment("Disable the paper doll from being slightly rotated every so often depending on the player rotation.")
		public boolean blockRotation = false;

		@Comment("Shift the paper doll downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".")
		public boolean potionShift = true;

		@Comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Obviously has no effect when the doll is always displayed.")
		public int displayTime = 5;

		@ConfigEntry.Gui.CollapsibleObject
		public DisplayActionsConfig displayActionsConfig = new DisplayActionsConfig();

		public static class DisplayActionsConfig implements ConfigData {
			@Comment("Always displays the paper doll, no matter what action the player is performing.")
			public boolean always = false;

			@Comment("Enables the paper doll while the player is sprinting.")
			public boolean sprinting = true;

			@Comment("Enables the paper doll while the player is crouching.")
			public boolean crouching = true;

			@Comment("Displays the paper doll when the player is using creative mode flight.")
			public boolean flying = true;

			@Comment("Shows the paper doll while the player is flying with an elytra.")
			public boolean elytraFlying = true;

			@Comment("Disables flame overlay on the HUD when on fire and displays the burning paper doll instead.")
			public boolean burning = false;

			@Comment("Shows the paper doll while the player is riding any entity.")
			public boolean riding = false;

			@Comment("Shows the paper doll while the player is in the swimming pose (e.g. crawling, swimming).")
			public boolean swimmingPose = true;
		}
	}

	public static class HoveringHotbarConfig implements ConfigData {
		@Comment("Offset on x-axis from screen center.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from screen bottom.")
		public int yOffset = 18;
	}

	public static class CoordinateDisplayConfig implements ConfigData {
		//@Config.Name("X-Offset")
		@Comment("Offset on x-axis from screen left.")
		public int xOffset = 0;

		//@Config.Name("Y-Offset")
		@Comment("Offset on y-axis from top.")
		public int yOffset = 60;

		//@Config.Name("Show Background")
		@Comment("Show black chat background behind coordinate display for better visibility.")
		public boolean background = true;

		//@Config.Name("Decimal Places")
		@Comment("Amount of decimal places for the three coordinates.")
		public int decimalPlaces = 0;

		//@Config.Name("Background Border")
		@Comment("Thickness of the background border in pixels. Only has an effect when \"Show Background\" is enabled.")
		public int backgroundBorder = 2;
	}
}
