/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.features.c0_28;

import com.viaversion.viafabricplus.ViaFabricPlus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

@SuppressWarnings("DataFlowIssue")
public final class GridItemSelectionScreen extends Screen {

    private static final int MAX_ROW_DIVIDER = 9;
    private static final int ITEM_XY_BOX_DIMENSION_CLASSIC = 25;
    private static final int SIDE_OFFSET = 15;
    private static final int ITEM_XY_BOX_DIMENSION_MODERN = 16;
    private static final int WOOL_PANEL_COLUMNS = 7;
    private static final List<Item> WOOL_COLORS = Items.WOOL.asList();

    public Item[][] itemGrid = null;
    public ItemStack selectedItem = null;
    public boolean woolSelectionMode = false;
    public Item selectedWoolColor = null;

    public GridItemSelectionScreen() {
        super(Component.nullToEmpty("Classic item selection"));
    }

    @Override
    protected void init() {
        woolSelectionMode = false;
        selectedWoolColor = null;
        if (itemGrid != null) {
            return;
        }
        final List<Item> allowedItems = new ArrayList<>();
        // Calculate all visible items
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR || !item.requiredFeatures().contains(FeatureFlags.VANILLA)) {
                continue;
            }
            if (ViaFabricPlus.api().limitations().itemExistsInConnection(item)) {
                allowedItems.add(item);
            }
        }

        itemGrid = new Item[Mth.ceil(allowedItems.size() / (double) MAX_ROW_DIVIDER)][MAX_ROW_DIVIDER];
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
    public boolean mouseClicked(final MouseButtonEvent click, final boolean doubled) {
        if (woolSelectionMode) {
            if (isInsideWoolPanel(click.x(), click.y())) {
                if (selectedWoolColor != null) {
                    selectItem(selectedWoolColor);
                }
            } else {
                woolSelectionMode = false;
                selectedWoolColor = null;
            }
            return true;
        }

        if (selectedItem != null) {
            if (shouldOpenWoolSelector(selectedItem.getItem())) {
                woolSelectionMode = true;
                selectedItem = null;
                return true;
            }

            selectItem(selectedItem.getItem());
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(final KeyEvent input) {
        if (minecraft.options.keyInventory.matches(input)) {
            if (woolSelectionMode) {
                woolSelectionMode = false;
                selectedWoolColor = null;
                return true;
            }

            this.onClose();
            return true;
        }
        if (input.isEscape() && woolSelectionMode) {
            woolSelectionMode = false;
            selectedWoolColor = null;
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        if (woolSelectionMode) {
            renderWoolColors(context, mouseX, mouseY);
            return;
        }

        final int halfWidth = this.width / 2;
        final int halfHeight = this.height / 2;

        final int boxWidth = ITEM_XY_BOX_DIMENSION_CLASSIC * MAX_ROW_DIVIDER + SIDE_OFFSET * 2;
        final int boxHeight = ITEM_XY_BOX_DIMENSION_CLASSIC * itemGrid.length + SIDE_OFFSET * 2 + SIDE_OFFSET;

        final int renderX = halfWidth - boxWidth / 2;
        final int renderY = halfHeight - boxHeight / 2;

        context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, Integer.MIN_VALUE);
        context.centeredText(font, "Select block", renderX + boxWidth / 2, renderY + SIDE_OFFSET, -1);
        selectedItem = null;

        int y = SIDE_OFFSET + SIDE_OFFSET;
        for (Item[] items : itemGrid) {
            int x = SIDE_OFFSET;
            for (Item item : items) {
                if (item == null) {
                    continue;
                }

                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    context.fill(renderX + x, renderY + y, renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC, Integer.MAX_VALUE);
                    selectedItem = item.getDefaultInstance();
                }
                context.item(item.getDefaultInstance(), renderX + x + ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + ITEM_XY_BOX_DIMENSION_MODERN / 4);
                x += ITEM_XY_BOX_DIMENSION_CLASSIC;
            }
            y += ITEM_XY_BOX_DIMENSION_CLASSIC;
        }
    }

    private void renderWoolColors(final GuiGraphicsExtractor context, final int mouseX, final int mouseY) {
        final int renderX = getWoolPanelX();
        final int renderY = getWoolPanelY();
        final int boxWidth = getWoolPanelWidth();
        final int boxHeight = getWoolPanelHeight();

        context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, Integer.MIN_VALUE);
        context.centeredText(font, "Select wool color", renderX + boxWidth / 2, renderY + SIDE_OFFSET, -1);
        selectedWoolColor = null;

        int woolIndex = 0;
        int y = SIDE_OFFSET + SIDE_OFFSET;
        for (int row = 0; row < Mth.ceil(WOOL_COLORS.size() / (double) WOOL_PANEL_COLUMNS); row++) {
            int x = SIDE_OFFSET;
            for (int column = 0; column < WOOL_PANEL_COLUMNS; column++) {
                if (woolIndex >= WOOL_COLORS.size()) {
                    break;
                }

                final Item item = WOOL_COLORS.get(woolIndex);
                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    context.fill(renderX + x, renderY + y, renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC, Integer.MAX_VALUE);
                    selectedWoolColor = item;
                }
                context.item(item.getDefaultInstance(), renderX + x + ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + ITEM_XY_BOX_DIMENSION_MODERN / 4);
                x += ITEM_XY_BOX_DIMENSION_CLASSIC;
                woolIndex++;
            }
            y += ITEM_XY_BOX_DIMENSION_CLASSIC;
        }
    }

    private void selectItem(final Item item) {
        final ItemStack stack = item.getDefaultInstance();
        this.minecraft.gameMode.handleCreativeModeItemAdd(stack, minecraft.player.getInventory().getSelectedSlot() + 36); // Beta Inventory Tracker
        this.minecraft.player.getInventory().setSelectedItem(stack);
        this.minecraft.player.inventoryMenu.broadcastChanges();

        AbstractWidget.playButtonClickSound(this.minecraft.getSoundManager());
        this.onClose();
    }

    private static boolean shouldOpenWoolSelector(final Item item) {
        return item == Items.WOOL.white() && supportsClassicWoolColors();
    }

    private static boolean supportsClassicWoolColors() {
        return ViaFabricPlus.api().targetVersion().equals(LegacyProtocolVersion.c0_30cpe)
            || ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30);
    }

    private int getWoolPanelWidth() {
        return ITEM_XY_BOX_DIMENSION_CLASSIC * WOOL_PANEL_COLUMNS + SIDE_OFFSET * 2;
    }

    private int getWoolPanelHeight() {
        return ITEM_XY_BOX_DIMENSION_CLASSIC * Mth.ceil(WOOL_COLORS.size() / (double) WOOL_PANEL_COLUMNS) + SIDE_OFFSET * 2 + SIDE_OFFSET;
    }

    private int getWoolPanelX() {
        return this.width / 2 - getWoolPanelWidth() / 2;
    }

    private int getWoolPanelY() {
        return this.height / 2 - getWoolPanelHeight() / 2;
    }

    private boolean isInsideWoolPanel(final double mouseX, final double mouseY) {
        final int renderX = getWoolPanelX();
        final int renderY = getWoolPanelY();
        return mouseX > renderX && mouseY > renderY && mouseX < renderX + getWoolPanelWidth() && mouseY < renderY + getWoolPanelHeight();
    }

}
