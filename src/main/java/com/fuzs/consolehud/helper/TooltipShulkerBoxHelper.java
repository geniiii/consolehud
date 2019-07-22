package com.fuzs.consolehud.helper;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

import java.util.List;

public class TooltipShulkerBoxHelper {

	public static void getLootTableTooltip(List<Text> tooltip, ItemStack stack) {
		CompoundTag nbttagcompound = stack.getSubTag("BlockEntityTag");
		
		if (nbttagcompound != null) {
			if (nbttagcompound.containsKey("LootTable", 8)) {
				tooltip.add(new LiteralText("???????"));
			}
		}
	}

	public static void getContentsTooltip(List<Text> tooltip, ItemStack stack, Style style, int rows) {
		List<ItemStack> contents = contentsToList(stack);

		if (contents == null) {
			return;
		}

		if (contents.size() > rows) {
			for (ItemStack itemstack : contents.subList(0, rows - 1)) {
				tooltip.add(itemstack.getName().deepCopy().append(" x").append(String.valueOf(itemstack.getCount())).setStyle(style));
			}
			tooltip.add(new TranslatableText("container.shulkerBox.more", contents.size() - rows + 1).setStyle(style.setItalic(true)));
		} else {
			for (ItemStack itemstack : contents) {
				tooltip.add(itemstack.getName().deepCopy().append(" x").append(String.valueOf(itemstack.getCount())).setStyle(style));
			}
		}
	}

	private static List<ItemStack> contentsToList(ItemStack stack) {
		CompoundTag nbttagcompound = stack.getSubTag("BlockEntityTag");

		if (nbttagcompound != null) {
			if (nbttagcompound.containsKey("Items", 9)) {
				DefaultedList<ItemStack> nonnulllist = DefaultedList.ofSize(27, ItemStack.EMPTY);
				Inventories.fromTag(nbttagcompound, nonnulllist);

				return mergeInventory(nonnulllist);
			}
		}
		return null;
	}

	private static List<ItemStack> mergeInventory(List<ItemStack> list) {
		List<ItemStack> contents = Lists.newArrayList();

		for (ItemStack itemstack : list) {
			if (contents.stream().anyMatch(it -> ItemStack.areItemsEqual(it, itemstack))) {
				contents.forEach((it) -> {
					if (ItemStack.areItemsEqual(it, itemstack)) {
						it.setCount(it.getCount() + itemstack.getCount());
					}
				});
			} else if (!itemstack.isEmpty()) {
				contents.add(itemstack);
			}
		}

		return contents;
	}

}