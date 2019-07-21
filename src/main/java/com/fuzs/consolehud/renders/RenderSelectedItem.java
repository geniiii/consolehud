package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.mixin.client.gui.hud.InGameHudAccessorMixin;
import com.fuzs.consolehud.util.ConsoleHudRender;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderSelectedItem extends InGameHud implements ConsoleHudRender {
	private final InGameHudAccessorMixin mixin = (InGameHudAccessorMixin) this;
	private final EventHandler eventHandler = new EventHandler();

	public RenderSelectedItem() {
		super(ConsoleHud.CLIENT);
	}

	private void onClientTick() {
		if (ConsoleHud.CLIENT.player != null && ConsoleHud.CONFIG.heldItemTooltips && !ConsoleHud.CLIENT.isPaused()) {
			ItemStack itemstack = ConsoleHud.CLIENT.player.inventory.getMainHandStack();

			if (itemstack.isEmpty()) {
				this.mixin.setHeldItemTooltipFade(0);
			} else if (!this.mixin.getCurrentStack().isEmpty() && itemstack.getItem() == this.mixin.getCurrentStack().getItem() && ItemStack.areEqualIgnoreDamage(itemstack, this.mixin.getCurrentStack()) && (itemstack.isDamageable() || itemstack.getDamage() == this.mixin.getCurrentStack().getDamage())) {
				if (this.mixin.getHeldItemTooltipFade() > 0) {
					this.mixin.setHeldItemTooltipFade(this.mixin.getHeldItemTooltipFade() - 1);
				}
			} else {
				this.mixin.setHeldItemTooltipFade(40);
			}

			this.mixin.setCurrentStack(itemstack);
		}
	}

	private void onInGameHudDraw() {
		if (ConsoleHud.CLIENT.player.isSpectator() || !ConsoleHud.CONFIG.heldItemTooltips) {
			ConsoleHud.CLIENT.options.heldItemTooltips = true;

			return;
		}

		final Identifier resource = Registry.ITEM.getId(this.mixin.getCurrentStack().getItem());
		final List<String> blacklist = Lists.newArrayList(ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsBlacklist);

		if (blacklist.contains(resource.toString()) || blacklist.contains(resource.getNamespace())) {
			ConsoleHud.CLIENT.options.heldItemTooltips = true;

			return;
		}

		if (this.mixin.getHeldItemTooltipFade() > 0 && !this.mixin.getCurrentStack().isEmpty()) {
			int tooltipXPosition = ConsoleHud.CLIENT.window.getScaledWidth() / 2;
			tooltipXPosition += ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsXOffset % tooltipXPosition;

			int tooltipYPosition = ConsoleHud.CLIENT.window.getScaledHeight();
			tooltipYPosition -= ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsYOffset % tooltipYPosition;

			if (!ConsoleHud.CLIENT.interactionManager.hasStatusBars()) {
				tooltipYPosition += 14;
			}

			int k = (this.mixin.getHeldItemTooltipFade() * 256 / 10);

			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				List<String> textLines = setTooltipColor(this.mixin.getCurrentStack());
				int listSize = textLines.size();

				if (listSize > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows) {
					listSize = ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows;
				}
				tooltipYPosition -= listSize > 1 ? (listSize - 1) * 10 + 2 : (listSize - 1) * 10;

				for (int lineIndex = 0; lineIndex < listSize; ++lineIndex) {
					this.drawCenteredString(this.getFontRenderer(), textLines.get(lineIndex), tooltipXPosition, tooltipYPosition, k << 24);

					if (lineIndex == 0) {
						tooltipYPosition += 2;
					}

					tooltipYPosition += 10;
				}
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

		}

		ConsoleHud.CLIENT.options.heldItemTooltips = false;

	}

	/**
	 * Removes empty lines from a list of strings
	 */
	private List<String> removeEmptyLines(final List<String> list) {
		list.removeIf(String::isEmpty);

		return list;
	}

	/**
	 * Colours first line in a list of strings according to its rarity, other lines that don't have a colour assigned
	 * will be coloured grey
	 */
	private List<String> setTooltipColor(final ItemStack stack) {
		List<String> list = removeEmptyLines(getTooltip(ConsoleHud.CLIENT.player, stack));

		for (int index = 0; index < list.size(); ++index) {
			if (index == 0) {
				list.set(index, new LiteralText(list.get(index)).formatted(stack.getRarity().formatting).asFormattedString());
			} else if (index == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows && ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsDots) {
				list.set(index, Formatting.GRAY + "..." + Formatting.RESET);
			} else if (stack.getItem().equals(Items.SHULKER_BOX) && list.size() == 7 && index == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1) {
				list.set(index, Formatting.GRAY + "" + Formatting.ITALIC + Formatting.strip(new TranslatableText("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows + getShulkerBoxExcess(list.get(6))).asFormattedString()) + Formatting.RESET);
			} else if (index == ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows) {
				list.set(index, Formatting.GRAY + "" + Formatting.ITALIC + Formatting.strip(new TranslatableText("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.heldItemTooltipsRows + 1).asFormattedString()) + Formatting.RESET);
			} else {
				list.set(index, Formatting.GRAY + list.get(index) + Formatting.RESET);
			}
		}

		return list;
	}

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
	private List<String> getTooltip(final PlayerEntity playerIn, final ItemStack stack) {
		final List<String> list = Lists.newArrayList();
		String itemName = stack.getName().asFormattedString();

		if (stack.hasCustomName()) {
			itemName = Formatting.ITALIC + itemName;
		}

		if (!stack.hasCustomName() && stack.getItem() == Items.FILLED_MAP) {
			itemName += " #" + stack.getDamage();
		}

		itemName += Formatting.RESET;
		list.add(itemName);

		final List<Text> textComponentList = Lists.newArrayList();
		stack.getItem().appendTooltip(stack, playerIn == null ? null : playerIn.world, textComponentList, TooltipContext.Default.NORMAL);
		textComponentList.forEach(component -> list.add(component.asFormattedString()));

		if (stack.hasTag()) {
			final ListTag enchantmentListTag = stack.getEnchantments();

			enchantmentListTag.forEach(tag -> {
				final CompoundTag compoundTag = (CompoundTag) tag;
				final String id = compoundTag.getString("id");
				final int lvl = compoundTag.getShort("lvl");
				Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(id));

				if (enchantment != null) {
					list.add(enchantment.getName(lvl).asFormattedString());
				}
			});


			// hasTag() already ensures that it's not null
			// noinspection ConstantConditions
			if (stack.getTag().containsKey("display", 10)) {
				final CompoundTag compoundTag = stack.getTag().getCompound("display");

				if (compoundTag.containsKey("color", 3)) {
					list.add(Formatting.ITALIC + new TranslatableText("item.dyed").asFormattedString());
				}

				if (compoundTag.getType("Lore") == 9) {
					final ListTag loreListTag = compoundTag.getList("Lore", 8);

					if (!loreListTag.isEmpty()) {
						for (Tag tag : loreListTag) {
							try {
								StringTag stringTag = (StringTag) tag;

								Text lore = Text.Serializer.fromJson(stringTag.asString());
								if (lore != null) {
									list.add(lore.asFormattedString());
								}
							} catch (Exception e) {
								list.add(tag.asString());
							}
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	public com.fuzs.consolehud.renders.RenderSelectedItem.EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public final class EventHandler implements ConsoleHudRender.EventHandler {
		private void registerInGameHudDrawEvent() {
			InGameHudDrawCallback.Pre.EVENT.register(
				partialTicks -> RenderSelectedItem.this.onInGameHudDraw()
			);
		}

		private void registerClientTickEvent() {
			ClientTickCallback.EVENT.register(
				client -> RenderSelectedItem.this.onClientTick()
			);
		}

		@Override
		public void registerEvents() {
			this.registerClientTickEvent();
			this.registerInGameHudDrawEvent();
		}
	}
}
