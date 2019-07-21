package com.fuzs.consolehud.helper;

import com.fuzs.consolehud.ConsoleHud;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipHelper extends TooltipElementsHelper {

	public List<Text> createTooltip(ItemStack stack, boolean simple) {

		this.itemstack = stack;
		List<Text> tooltip = Lists.newArrayList();

		this.getName(tooltip, new Style().setColor(Formatting.WHITE), TooltipContext.Default.NORMAL);

		if (simple) {
			return tooltip;
		}

		this.getInformation(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), TooltipContext.Default.ADVANCED, ConsoleHud.CLIENT.player.world);

		if (stack.getItem() instanceof Items.SHULKER_BOX && tooltip.size() == ConsoleHud.CONFIG.heldItemTooltips.rows) {
			return tooltip;
		}

		this.getEnchantments(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()));
		this.getColorTag(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), TooltipContext.Default.ADVANCED);
		this.getLoreTag(tooltip, new Style().setItalic(true).setColor(Formatting.DARK_PURPLE));
		//this.getUnbreakable(tooltip, new Style().setColor(TextFormatting.BLUE));
		//this.getAdventureStats(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.textColor.getChatColor()));
		this.getDurability(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), false);
		//this.getNameID(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.textColor.getChatColor()));
		//this.getNBTAmount(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.textColor.getChatColor()));
		//this.getForgeInformation(tooltip, TooltipContext.Default.NORMAL);

		this.applyLastLine(tooltip);

		return tooltip;

	}

	private void applyLastLine(List<Text> tooltip) {

		boolean flag = ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.showDurability && ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.forceDurability && this.itemstack.isDamaged();
		int i = 0, j = 0; // i counts the lines to be added afterwards, j is for counting how many lines to remove

		if (flag) {
			i++;
		}

		if (tooltip.size() + i > ConsoleHud.CONFIG.heldItemTooltipsConfig.rows) {

			if (ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.showLastLine) {
				i++;
			}

			j = tooltip.size() - ConsoleHud.CONFIG.heldItemTooltipsConfig.rows + i;

			if (j == tooltip.size()) {
				i--; // prevent item name from being removed
				j = this.itemstack.isDamaged() ? 0 : j; // prioritise durability over last line
			}

			tooltip.subList(ConsoleHud.CONFIG.heldItemTooltipsConfig.rows - i, tooltip.size()).clear();

		}

		if (flag) {
			this.getDurability(tooltip, new Style().setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), true);
		}

		if (j > 0 && ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.showLastLine) {
			this.getLastLine(tooltip, new Style().setItalic(true).setColor(ConsoleHud.CONFIG.heldItemTooltipsConfig.appearanceConfig.textColor.getChatColor()), j);
		}

	}

	@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
	protected static void getAdventureBlockInfo(List<Text> list, Style style, ListTag nbttaglist) {

		for (int i = 0; i < nbttaglist.size(); i++) {

			try {

				BlockArgumentParser blockstateparser = new BlockArgumentParser(new StringReader(nbttaglist.getString(i)), true).parse(true);
				BlockState blockstate = blockstateparser.getBlockState();
				Identifier resourcelocation = blockstateparser.getTagId();
				boolean flag = blockstate != null;
				boolean flag1 = resourcelocation != null;

				if (flag || flag1) {
					if (flag) {
						list.addAll(Lists.newArrayList(blockstate.getBlock().getName().setStyle(style)));
					}

					Tag<Block> tag = BlockTags.getContainer().get(resourcelocation);
					if (tag != null) {
						Collection<Block> collection = tag.values();
						if (!collection.isEmpty()) {
							list.addAll(collection.stream().map(Block::getName).map(it -> it.setStyle(style)).collect(Collectors.toList()));
						}
					}
				}

			} catch (CommandSyntaxException ignored) {

			}

		}

	}
}