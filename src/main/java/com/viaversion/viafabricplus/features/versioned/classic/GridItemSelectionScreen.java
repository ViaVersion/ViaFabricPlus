/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.features.versioned.classic;

import com.viaversion.viafabricplus.features2.item.filter_creative_tabs.ItemRegistryDiff;
import com.viaversion.viafabricplus.screen.VFPScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class GridItemSelectionScreen extends VFPScreen {

    public static final GridItemSelectionScreen INSTANCE = new GridItemSelectionScreen();

    private static final int MAX_ROW_DIVIDER = 9;
    private static final int ITEM_XY_BOX_DIMENSION_CLASSIC = 25;
    private static final int SIDE_OFFSET = 15;
    private static final int ITEM_XY_BOX_DIMENSION_MODERN = 16;

    public Item[][] itemGrid = null;
    public ItemStack selectedItem = null;

    public GridItemSelectionScreen() {
        super("Classic item selection", false);
    }

    @Override
    protected void init() {
        if (itemGrid != null) {
            return;
        }
        final List<Item> allowedItems = new ArrayList<>();
        // Calculate all visible items
        for (Item item : Registries.ITEM) {
            if (item == Items.AIR || !item.getRequiredFeatures().contains(FeatureFlags.VANILLA)) continue;
            if (ItemRegistryDiff.keepItem(item)) {
                allowedItems.add(item);
            }
        }

        itemGrid = new Item[MathHelper.ceil(allowedItems.size() / (double) MAX_ROW_DIVIDER)][MAX_ROW_DIVIDER];
        int x = 0;
        int y = 0;
        for (Item allowedItem : allowedItems) {
            itemGrid[y][x] = allowedItem;
            x++;
            if (x == MAX_ROW_DIVIDER) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedItem != null) {
            this.client.interactionManager.clickCreativeStack(selectedItem, client.player.getInventory().selectedSlot + 36); // Beta Inventory Tracker

            this.client.player.getInventory().main.set(client.player.getInventory().selectedSlot, selectedItem);
            this.client.player.playerScreenHandler.sendContentUpdates();

            playClickSound();
            this.close();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        final int halfWidth = this.width / 2;
        final int halfHeight = this.height / 2;

        final int boxWidth = ITEM_XY_BOX_DIMENSION_CLASSIC * MAX_ROW_DIVIDER + SIDE_OFFSET * 2;
        final int boxHeight = ITEM_XY_BOX_DIMENSION_CLASSIC * itemGrid.length + SIDE_OFFSET * 2 + SIDE_OFFSET;

        final int renderX = halfWidth - boxWidth / 2;
        final int renderY = halfHeight - boxHeight / 2;

        context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, Integer.MIN_VALUE);
        context.drawCenteredTextWithShadow(textRenderer, "Select block", renderX + boxWidth / 2, renderY + SIDE_OFFSET, -1);
        selectedItem = null;

        int y = SIDE_OFFSET + SIDE_OFFSET;
        for (Item[] items : itemGrid) {
            int x = SIDE_OFFSET;
            for (Item item : items) {
                if (item == null) continue;

                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    context.fill(renderX + x, renderY + y, renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC, Integer.MAX_VALUE);
                    selectedItem = item.getDefaultStack();
                }
                context.drawItem(item.getDefaultStack(), renderX + x + ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + ITEM_XY_BOX_DIMENSION_MODERN / 4);
                x += ITEM_XY_BOX_DIMENSION_CLASSIC;
            }
            y += ITEM_XY_BOX_DIMENSION_CLASSIC;
        }
    }

}
