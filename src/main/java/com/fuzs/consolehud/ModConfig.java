package com.fuzs.consolehud;

import blue.endless.jankson.Comment;
import com.fuzs.consolehud.util.PaperDollPosition;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;

@ConfigFile(name = ConsoleHud.MODID)
public class ModConfig {
	@Comment("Enhances vanilla held item tooltips with information about enchantments, potions effects, shulker box contents, and more.")
	public boolean heldItemTooltips = true;

	@Comment("Shows a small player model in a configurable corner of the screen while the player is sprinting, sneaking, or flying.")
	public boolean paperDoll = true;

	/*@ConfigEntry.Category("selected_item")
	@ConfigEntry.Gui.TransitiveObject*/
	public SelectedItemConfig selectedItemConfig = new SelectedItemConfig();

	/*@ConfigEntry.Category("paper_doll")
	@ConfigEntry.Gui.TransitiveObject*/
	public PaperDollConfig paperDollConfig = new PaperDollConfig();

	public static class SelectedItemConfig {
		@Comment("Disables held item tooltips for specified items and mods, mainly to prevent custom tooltips from overlapping.")
		public String[] heldItemTooltipsBlacklist = new String[]{};

		//@ConfigEntry.BoundedDiscrete(min = 2, max = 7)
		@Comment("Maximum amount of rows to be displayed for held item tooltips.")
		public int heldItemTooltipsRows = 5;

		@Comment("Offset on x-axis from screen center.")
		public int heldItemTooltipsXOffset = 0;

		@Comment("Offset on y-axis from screen center.")
		public int heldItemTooltipsYOffset = 59;

		@Comment("Show three dots when the complete tooltip information can't be displayed like on Console Edition instead of the custom text.")
		public boolean heldItemTooltipsDots = false;
	}

	public static class PaperDollConfig {
		@Comment("Defines a screen corner to display the paper doll in.")
		public PaperDollPosition paperDollPosition = PaperDollPosition.TOP_LEFT;

		//@ConfigEntry.BoundedDiscrete(min = 1, max = 24)
		@Comment("Scale of the paper doll. This is additionally adjusted by the GUI Scale option in Video Settings.")
		public int paperDollScale = 4;

		@Comment("Offset on x-axis from original doll position.")
		public int paperDollXOffset = 0;

		@Comment("Offset on y-axis from original doll position.")
		public int paperDollYOffset = 0;

		@Comment("Disable the paper doll from being slightly rotated every so often depending on the player rotation.")
		public boolean blockRotation = false;

		@Comment("Shift the paper doll downwards when it would otherwise overlap with the potion icons. Only applicable when the \"Screen Corner\" is set to \"topright\".")
		public boolean potionShift = true;

		@Comment("Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Obviously has no effect when the doll is always displayed.")
		public int displayTime = 5;

		/*@ConfigEntry.Category("display_actions_config")
		@ConfigEntry.Gui.TransitiveObject*/
		public DisplayActionsConfig displayActionsConfig = new DisplayActionsConfig();

		public static class DisplayActionsConfig {
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
}
