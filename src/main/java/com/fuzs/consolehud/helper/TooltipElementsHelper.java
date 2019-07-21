package com.fuzs.consolehud.helper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This is basically ItemStack#getTooltip split into separate functions to be modular (and completely customisable in the future)
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions", "SameParameterValue", "unused"})
public class TooltipElementsHelper {

	protected ItemStack itemstack = ItemStack.EMPTY;

	protected void getName(List<Text> list, Style style, TooltipContext tooltipflag) {

		list.add(new LiteralText("").append(this.itemstack.getName()).setStyle(new Style().setItalic(this.itemstack.hasCustomName()).setColor(this.itemstack.getRarity().formatting)));

	}

	protected void getInformation(List<Text> list, Style style, TooltipContext tooltipflag, World world) {

		List<Text> information = Lists.newArrayList();

		if (Block.getBlockFromItem(this.itemstack.getItem()) instanceof ShulkerBoxBlock) {

			TooltipShulkerBoxHelper.getContentsTooltip(information, this.itemstack, style, ConfigHandler.HELD_ITEM_TOOLTIPS_CONFIG.rows.get() - 1);

		} else {

			this.itemstack.getItem().appendTooltip(this.itemstack, world, information, tooltipflag);
			// remove empty lines from a list of strings
			information = information.stream().filter(it -> !Strings.isNullOrEmpty(it.getString())).collect(Collectors.toList());

		}

		list.addAll(information);

	}

	protected void getEnchantments(List<Text> list, Style style) {

		if (this.itemstack.hasTag()) {

			ListTag nbttaglist = this.itemstack.getEnchantments();

			for (int j = 0; j < nbttaglist.size(); ++j) {

				CompoundTag nbttagcompound = nbttaglist.getCompoundTag(j);
				Enchantment enchantment = Registry.ENCHANTMENT.get(Identifier.tryParse(nbttagcompound.getString("id")));

				if (enchantment != null) {
					list.add(enchantment.getName(nbttagcompound.getInt("lvl")));
				}

			}

		}

	}

	protected void getColorTag(List<String> list, Style style, TooltipContext tooltipflag) {

		if (this.itemstack.hasTag()) {
			if (this.itemstack.getTag().containsKey("display", 10)) {
				CompoundTag nbttagcompound = this.itemstack.getTag().getCompound("display");

				if (nbttagcompound.containsKey("color", 3)) {
					if (tooltipflag.isAdvanced()) {
						list.add(new TranslatableText("item.color", String.format(Locale.ROOT, "#%06X", nbttagcompound.getInt("color"))).setStyle(style).asFormattedString());
					} else {
						list.add(new TranslatableText("item.dyed").setStyle(style.setItalic(true)).asFormattedString());
					}
				}
			}
		}

	}

	protected void getLoreTag(List<Text> list, Style style) {

		if (this.itemstack.hasTag()) {
			if (this.itemstack.getTag().containsKey("display", 10)) {
				CompoundTag nbttagcompound = this.itemstack.getTag().getCompound("display");

				if (nbttagcompound.getType("Lore") == 9) {
					ListTag nbttaglist = nbttagcompound.getList("Lore", 8);

					if (!nbttaglist.isEmpty()) {
						for (int l1 = 0; l1 < nbttaglist.size(); ++l1) {
							list.add(new LiteralText(nbttaglist.getString(l1)).setStyle(style));
						}
					}
				}
			}
		}

	}

	protected void getUnbreakable(List<Text> list, Style style) {

		if (this.itemstack.hasTag() && this.itemstack.getTag().getBoolean("Unbreakable")) {
			list.add(new TranslatableText("item.unbreakable").setStyle(style));
		}

	}

	protected void getAdventureStats(List<String> list, Style style) {

		if (this.itemstack.hasTag()) {

			if (this.itemstack.getTag().containsKey("CanDestroy", 9)) {

				ListTag nbttaglist1 = this.itemstack.getTag().getList("CanDestroy", 8);

				if (!nbttaglist1.isEmpty()) {

					list.add(new TranslatableText("item.canBreak").setStyle(style).asFormattedString());

					TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist1);
				}
			}

			if (this.itemstack.getTag().containsKey("CanPlaceOn", 9)) {

				ListTag nbttaglist2 = this.itemstack.getTag().getList("CanPlaceOn", 8);

				if (!nbttaglist2.isEmpty()) {

					list.add(new TranslatableText("item.canPlace").setStyle(style).asFormattedString());

					TooltipHelper.getAdventureBlockInfo(list, style, nbttaglist2);
				}
			}

		}

	}

	protected void getDurability(List<Text> list, Style style, boolean force) {

		if ((!ConfigHandler.heldItemTooltipsConfig.appearanceConfig.showDurability || ConfigHandler.heldItemTooltipsConfig.appearanceConfig.forceDurability) && !force || !this.itemstack.isDamaged()) {
			return;
		}

		list.add(new TranslatableText("item.durability", this.itemstack.getMaxDamage() -
			this.itemstack.getDamage(), this.itemstack.getMaxDamage()).setStyle(style));

	}

	protected void getNameID(List<Text> list, Style style) {

		Identifier resource = Registry.ITEM.getId(this.itemstack.getItem());
		if (resource != null) {
			list.add(new LiteralText(resource.toString()).setStyle(style));
		}

	}

	protected void getNBTAmount(List<Text> list, Style style) {

		if (this.itemstack.hasTag()) {
			list.add(new TranslatableText("item.nbt_tags", this.itemstack.getTag().getKeys().size()).setStyle(style));
		}

	}

	protected void getLastLine(List<Text> list, Style style, int i) {

		list.add(new TranslatableText("container.shulkerBox.more", i).setStyle(style));

	}

}