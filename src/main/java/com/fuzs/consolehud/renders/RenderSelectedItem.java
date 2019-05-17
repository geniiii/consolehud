package com.fuzs.consolehud.renders;

import com.fuzs.consolehud.ConsoleHud;
import com.fuzs.consolehud.mixin.client.gui.hud.InGameHudAccessorMixin;
import com.fuzs.consolehud.util.ConsoleHudRender;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.ChatFormat;
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import site.geni.renderevents.callbacks.client.InGameHudDrawCallback;

import java.util.List;

public class RenderSelectedItem extends InGameHud implements ConsoleHudRender {

	private boolean vanillaHeldItemTooltips = false;
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
			} else if (!this.mixin.getCurrentStack().isEmpty() && itemstack.getItem() == this.mixin.getCurrentStack().getItem() && ItemStack.areEqual(itemstack, this.mixin.getCurrentStack()) && (itemstack.hasDurability() || itemstack.getDamage() == this.mixin.getCurrentStack().getDamage())) {
				if (this.mixin.getHeldItemTooltipFade() > 0) {
					this.mixin.setHeldItemTooltipFade(this.mixin.getHeldItemTooltipFade() - 1);
				}
			} else {
				this.mixin.setHeldItemTooltipFade(40);
			}

			if (!this.vanillaHeldItemTooltips && this.mixin.getHeldItemTooltipFade() > 38) {
				this.mixin.setHeldItemTooltipFade(0);
			}

			this.mixin.setCurrentStack(itemstack);
		}
	}

	private void renderGameOverlayText() {
		if (ConsoleHud.CLIENT.player.isSpectator() || !ConsoleHud.CONFIG.heldItemTooltips) {
			this.vanillaHeldItemTooltips = true;
			return;
		}

		Identifier resource = Registry.ITEM.getId(this.mixin.getCurrentStack().getItem());
		List<String> blacklist = Lists.newArrayList(ConsoleHud.CONFIG.selectedItemConfig.blacklist);
		boolean flag = blacklist.contains(resource.toString()) || blacklist.contains(resource.getNamespace());

		if (flag) {
			this.vanillaHeldItemTooltips = true;
			return;
		}

		if (this.mixin.getHeldItemTooltipFade() > 0 && !this.mixin.getCurrentStack().isEmpty()) {
			int tooltipXPosition = ConsoleHud.CLIENT.window.getScaledWidth() / 2;
			tooltipXPosition += ConsoleHud.CONFIG.selectedItemConfig.xOffset % tooltipXPosition;

			int tooltipYPosition = ConsoleHud.CLIENT.window.getScaledHeight();
			tooltipYPosition -= ConsoleHud.CONFIG.selectedItemConfig.yOffset % tooltipYPosition;

			if (ConsoleHud.CONFIG.hoveringHotbar) {
				tooltipXPosition -= ConsoleHud.CONFIG.hoveringHotbarConfig.xOffset;
				tooltipYPosition -= ConsoleHud.CONFIG.hoveringHotbarConfig.yOffset;
			}

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
				List<String> textLines = getToolTipColour(this.mixin.getCurrentStack());
				int listSize = textLines.size();

				if (listSize > ConsoleHud.CONFIG.selectedItemConfig.rows) {
					listSize = ConsoleHud.CONFIG.selectedItemConfig.rows;
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
	private List<String> removeEmptyLines(List<String> list) {
		list.forEach(line -> list.removeIf(String::isEmpty));

		return list;
	}

	/**
	 * Colours first line in a list of strings according to its rarity, other lines that don't have a colour assigned
	 * will be coloured grey
	 */
	private List<String> getToolTipColour(ItemStack stack) {
		List<String> list = removeEmptyLines(getTooltip(ConsoleHud.CLIENT.player, stack));

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().formatting + list.get(i));
			} else if (i == ConsoleHud.CONFIG.selectedItemConfig.rows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.rows && ConsoleHud.CONFIG.selectedItemConfig.dots) {
				list.set(i, ChatFormat.GRAY + "..." + ChatFormat.RESET);
			} else if (stack.getItem().equals(Items.SHULKER_BOX) && list.size() == 7 && i == ConsoleHud.CONFIG.selectedItemConfig.rows - 1) {
				list.set(i, ChatFormat.GRAY + "" + ChatFormat.ITALIC + ChatFormat.stripFormatting(new TranslatableComponent("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.rows + getShulkerBoxExcess(list.get(6))).getFormattedText()) + ChatFormat.RESET);
			} else if (i == ConsoleHud.CONFIG.selectedItemConfig.rows - 1 && list.size() > ConsoleHud.CONFIG.selectedItemConfig.rows) {
				list.set(i, ChatFormat.GRAY + "" + ChatFormat.ITALIC + ChatFormat.stripFormatting(new TranslatableComponent("container.shulkerBox.more", list.size() - ConsoleHud.CONFIG.selectedItemConfig.rows + 1).getFormattedText()) + ChatFormat.RESET);
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


			// hasTag() already ensures that it's not null
			// noinspection ConstantConditions
			if (stack.getTag().containsKey("display", 10)) {
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

	@Override
	public EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public final class EventHandler implements ConsoleHudRender.EventHandler {
		private void registerIngameHudDrawEvent() {
			InGameHudDrawCallback.Pre.EVENT.register(
				partialTicks -> RenderSelectedItem.this.renderGameOverlayText()
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
			this.registerIngameHudDrawEvent();
		}
	}
}
