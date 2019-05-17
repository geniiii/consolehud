package com.fuzs.consolehud;

import com.fuzs.consolehud.util.PaperDollPosition;
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

	@ConfigEntry.Category("selected_item")
	@ConfigEntry.Gui.TransitiveObject
	public SelectedItemConfig selectedItemConfig = new SelectedItemConfig();

	@ConfigEntry.Category("paper_doll")
	@ConfigEntry.Gui.TransitiveObject
	public PaperDollConfig paperDollConfig = new PaperDollConfig();

	@ConfigEntry.Category("hovering_hotbar")
	@ConfigEntry.Gui.TransitiveObject
	public HoveringHotbarConfig hoveringHotbarConfig = new HoveringHotbarConfig();

	public static class SelectedItemConfig implements ConfigData {
		@Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
		public String[] blacklist = new String[]{};

		@ConfigEntry.BoundedDiscrete(min = 2, max = 7)
		@Comment("Maximum amount of rows to be displayed for held item tooltips.")
		public int rows = 5;

		@Comment("Offset on x-axis from screen center.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from screen center.")
		public int yOffset = 59;

		@Comment("Show three dots when the complete tooltip information can't be displayed like on Console Edition instead of the custom text.")
		public boolean dots = false;
	}

	public static class PaperDollConfig {
		@Comment("Defines a screen corner to display the paper doll in.")
		public PaperDollPosition position = PaperDollPosition.TOP_LEFT;

		@ConfigEntry.BoundedDiscrete(min = 1, max = 24)
		@Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
		public int scale = 4;

		@Comment("Offset on x-axis from original doll position.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from original doll position.")
		public int yOffset = 0;

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

	public static class HoveringHotbarConfig {

		@Comment("Offset on x-axis from screen center.")
		public int xOffset = 0;

		@Comment("Offset on y-axis from screen bottom.")
		public int yOffset = 18;
	}
}
