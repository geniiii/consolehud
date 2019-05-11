package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.mixin.client.IngameHudAccessorMixin;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class RenderSelectedItem extends InGameHud {

	private final MinecraftClient client;

	public RenderSelectedItem(MinecraftClient mcIn) {
		super(mcIn);

		this.client = mcIn;
	}

	public void registerOnClientTickEvent() {
		ClientTickCallback.EVENT.register(
			event -> {
				if (this.client.player != null && ConsoleHud.CONFIG.heldItemTooltips && !this.client.isPaused()) {
					ItemStack itemstack = this.client.player.inventory.getMainHandStack();

					if (itemstack.isEmpty()) {
						((IngameHudAccessorMixin) this).setHeldItemTooltipFade(0);
					} else if (!((IngameHudAccessorMixin) this).getCurrentStack().isEmpty() && itemstack.getItem() == ((IngameHudAccessorMixin) this).getCurrentStack().getItem() && ItemStack.areEqual(itemstack, ((IngameHudAccessorMixin) this).getCurrentStack()) && (itemstack.hasDurability() || itemstack.getDamage() == ((IngameHudAccessorMixin) this).getCurrentStack().getDamage())) {
						if (((IngameHudAccessorMixin) this).getHeldItemTooltipFade() > 0) {
							((IngameHudAccessorMixin) this).setHeldItemTooltipFade(((IngameHudAccessorMixin) this).getHeldItemTooltipFade() - 1);
						}
					} else {
						((IngameHudAccessorMixin) this).setHeldItemTooltipFade(40);
					}

					((IngameHudAccessorMixin) this).setCurrentStack(itemstack);
				}
			}
		);
	}

	public void renderGameOverlayText() {
		if (this.client.player.isSpectator() || !ConsoleHud.CONFIG.heldItemTooltips) {
			client.options.heldItemTooltips = true;
			return;
		}

		Identifier resource = Registry.ITEM.getId(((IngameHudAccessorMixin) this).getCurrentStack().getItem());
		List<String> blacklist = Lists.newArrayList(ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsBlacklist);
		boolean flag = blacklist.contains(resource.toString()) || blacklist.contains(resource.getNamespace());

		if (flag) {
			client.options.heldItemTooltips = true;
			return;
		}

		if (((IngameHudAccessorMixin) this).getHeldItemTooltipFade() > 0 && !((IngameHudAccessorMixin) this).getCurrentStack().isEmpty()) {
			int tooltipXPosition = this.client.window.getScaledWidth() / 2;
			tooltipXPosition += ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsXOffset % tooltipXPosition;

			int tooltipYPosition = this.client.window.getScaledHeight();
			tooltipYPosition -= ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsYOffset % tooltipYPosition;

			if (!this.client.interactionManager.hasStatusBars()) {
				tooltipYPosition += 14;
			}

			int k = (int) ((float) ((IngameHudAccessorMixin) this).getHeldItemTooltipFade() * 256.0F / 10.0F);

			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				List<String> textLines = getToolTipColour(((IngameHudAccessorMixin) this).getCurrentStack());
				int listsize = textLines.size();

				if (listsize > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows) {
					listsize = ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows;
				}
				tooltipYPosition -= listsize > 1 ? (listsize - 1) * 10 + 2 : (listsize - 1) * 10;

				for (int k1 = 0; k1 < listsize; ++k1) {
					this.drawCenteredString(this.getFontRenderer(), textLines.get(k1), tooltipXPosition, tooltipYPosition, k << 24);

					if (k1 == 0) {
						tooltipYPosition += 2;
					}

					tooltipYPosition += 10;
				}
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

		}

		this.client.options.heldItemTooltips = false;

	}

	/**
	 * Removes empty lines from a list of strings
	 */
	private List<String> removeEmptyLines(List<String> list) {
		for (int k1 = 0; k1 < list.size(); ++k1) {
			if (list.get(k1).isEmpty()) {
				list.remove(k1);
			}
		}
		return list;
	}

	/**
	 * Colours first line in a list of strings according to its rarity, other lines that don't have a colour assigned
	 * will be coloured grey
	 */
	private List<String> getToolTipColour(ItemStack stack) {
		List<String> list = removeEmptyLines(getTooltip(this.client.player, stack));

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().formatting + list.get(i));
			} else if (i == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows && ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsDots) {
				list.set(i, ChatFormat.GRAY + "..." + ChatFormat.RESET);
			} else if (stack.getItem().equals(Items.SHULKER_BOX) && list.size() == 7 && i == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1) {
				list.set(i, ChatFormat.GRAY + "" + ChatFormat.ITALIC + ChatFormat.stripFormatting(new TranslatableComponent("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows + getShulkerBoxExcess(list.get(6))).getFormattedText()) + ChatFormat.RESET);
			} else if (i == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows) {
				list.set(i, ChatFormat.GRAY + "" + ChatFormat.ITALIC + ChatFormat.stripFormatting(new TranslatableComponent("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows + 1).getFormattedText()) + ChatFormat.RESET);
			} else {
				list.set(i, ChatFormat.GRAY + list.get(i) + ChatFormat.RESET);
			}
		}

		return list;
	}

	/**
	 * Returns the contents of the textbox as float
	 */
	private int getShulkerBoxExcess(String line) {
		line = line.replaceAll("[^0-9]", "");
		if (line.isEmpty()) {
			line = "0";
		}
		return Integer.valueOf(line);
	}

	/**
	 * Return a list of strings containing information about the item
	 */
	@Environment(EnvType.CLIENT)
	private List<String> getTooltip(PlayerEntity playerIn, ItemStack stack) {
		List<String> list = Lists.newArrayList();
		String itemName = stack.getDisplayName().getFormattedText();

		if (stack.hasDisplayName()) {
			itemName = ChatFormat.ITALIC + itemName;
		}

		if (!stack.hasDisplayName() && stack.getItem() == Items.FILLED_MAP) {
			itemName = itemName + " #" + stack.getDamage();
		}

		itemName = itemName + ChatFormat.RESET;
		list.add(itemName);

		List<Component> textComponentList = Lists.newArrayList();
		stack.getItem().buildTooltip(stack, playerIn == null ? null : playerIn.world, textComponentList, TooltipContext.Default.NORMAL);
		textComponentList.forEach(component -> list.add(component.getFormattedText()));

		if (stack.hasTag()) {
			ListTag enchantmentListTag = stack.getEnchantmentList();

			enchantmentListTag.forEach(tag -> {
				CompoundTag compoundTag = (CompoundTag) tag;
				String id = compoundTag.getString("id");
				int lvl = compoundTag.getShort("lvl");
				Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(id));

				if (enchantment != null) {
					list.add(enchantment.getTextComponent(lvl).getFormattedText());
				}
			});

			if (stack.getTag() != null && stack.getTag().containsKey("display", 10)) {
				CompoundTag compoundTag = stack.getTag().getCompound("display");

				if (compoundTag.containsKey("color", 3)) {
					list.add(ChatFormat.ITALIC + new TranslatableComponent("item.dyed").getFormattedText());
				}

				if (compoundTag.getType("Lore") == 9) {
					ListTag loreListTag = compoundTag.getList("Lore", 8);

					if (!loreListTag.isEmpty()) {
						for (Tag tag : loreListTag) {
							list.add(ChatFormat.DARK_PURPLE + "" + ChatFormat.ITALIC + tag);
						}
					}
				}
			}
		}
		return list;
	}
}
