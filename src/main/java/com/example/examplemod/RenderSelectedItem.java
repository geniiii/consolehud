package com.example.examplemod;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RenderSelectedItem extends GuiIngame {

    public RenderSelectedItem(Minecraft mcIn) {
        super(mcIn);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.mc.isGamePaused() || event.phase != TickEvent.Phase.END)
            return;

        if (this.mc.player != null)
        {
            ItemStack itemstack = this.mc.player.inventory.getCurrentItem();

            if (itemstack.isEmpty())
            {
                this.remainingHighlightTicks = 0;
            }
            else if (!this.highlightingItemStack.isEmpty() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            }
            else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    @SubscribeEvent
    public void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (mc.gameSettings.heldItemTooltips) {
            mc.gameSettings.heldItemTooltips = false;
        }
        if (this.mc.playerController.isSpectator()) {
            return;
        }

        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.isEmpty())
        {
            String s = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName())
            {
                s = TextFormatting.ITALIC + s;
            }

            int i = event.getResolution().getScaledWidth() / 2;
            int j = event.getResolution().getScaledHeight() - 59;

            if (!this.mc.playerController.shouldDrawHUD())
            {
                j += 14;
            }

            int k = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255)
            {
                k = 255;
            }

            if (k > 0)
            {
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                List<String> textLines = getItemToolTip(this.highlightingItemStack);
                int listsize = textLines.size();

                if (listsize > 5) {
                    listsize = 5;
                }

                j -= listsize > 1 ? (listsize - 1) * 10 + 2 : (listsize - 1) * 10;

                for (int k1 = 0; k1 < listsize; ++k1)
                {
                    drawCenteredString(textLines.get(k1), (float)i, (float)j, -1); // + (k << 24)

                    if (k1 == 0)
                    {
                        j += 2;
                    }

                    j += 10;
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(ICONS);
            }
        }
        //drawEntityOnScreen(51, 75, 30, this.mc.player);
    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(ent.rotationPitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(ent.renderYawOffset / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(ent.rotationYaw / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(ent.rotationPitch / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private List<String> removeEmptyLines(List<String> list) {
        for (int k1 = 0; k1 < list.size(); ++k1)
        {
            if (list.get(k1).isEmpty()) {
                list.remove(k1);
            }
        }
        return list;
    }

    private List<String> getItemToolTip(ItemStack stack) {
        List<String> list = removeEmptyLines(getTooltip(this.mc.player, stack));

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            } else if (i == 4 && list.size() > 5) {
                list.set(i, TextFormatting.GRAY + "...");
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        return list;
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    private void drawCenteredString(String text, float x, float y, int color)
    {
        this.getFontRenderer().drawStringWithShadow(text, (x - this.getFontRenderer().getStringWidth(text) / 2), y, color);
    }

    /**
     * Return a list of strings containing information about the item
     */
    @SideOnly(Side.CLIENT)
    private List<String> getTooltip(@Nullable EntityPlayer playerIn, ItemStack stack)
    {
        List<String> list = Lists.<String>newArrayList();
        String s = stack.getDisplayName();

        if (stack.hasDisplayName())
        {
            s = TextFormatting.ITALIC + s;
        }

        s = s + TextFormatting.RESET;

        if (!stack.hasDisplayName() && stack.getItem() == Items.FILLED_MAP)
        {
            s = s + " #" + stack.getItemDamage();
        }

        list.add(s);

        stack.getItem().addInformation(stack, playerIn == null ? null : playerIn.world, list, ITooltipFlag.TooltipFlags.NORMAL);

        if (stack.hasTagCompound())
        {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for (int j = 0; j < nbttaglist.tagCount(); ++j)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                int k = nbttagcompound.getShort("id");
                int l = nbttagcompound.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                if (enchantment != null)
                {
                    list.add(enchantment.getTranslatedName(l));
                }
            }
        }
        return list;
    }
}