/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.screen.impl;

import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class ClassicItemSelectionScreen extends VFPScreen {

    public static ClassicItemSelectionScreen INSTANCE;

    public static void create() {
        INSTANCE = new ClassicItemSelectionScreen();

        ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> {
            if (protocolVersion.isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                INSTANCE.reload(protocolVersion, false);
            }
        });
    }

    private final static int MAX_ROW_DIVIDER = 9;
    private final static int ITEM_XY_BOX_DIMENSION_CLASSIC = 25;
    private final static int SIDE_OFFSET = 15;
    private final static int ITEM_XY_BOX_DIMENSION_MODERN = 16;

    public Item[][] itemGrid = null;
    public ItemStack selectedItem = null;

    public ClassicItemSelectionScreen() {
        super("Classic item selection", false);
    }

    public void reload(final VersionEnum version, final boolean hasCustomBlocksV1) {
        final List<Item> allowedItems = new ArrayList<>();
        allowedItems.add(Items.OAK_LOG);
        allowedItems.add(Items.OAK_PLANKS);
        allowedItems.add(Items.STONE);
        allowedItems.add(Items.COBBLESTONE);
        allowedItems.add(Items.MOSSY_COBBLESTONE);
        allowedItems.add(Items.BRICKS);
        allowedItems.add(Items.IRON_BLOCK);
        allowedItems.add(Items.GOLD_BLOCK);
        allowedItems.add(Items.GLASS);
        allowedItems.add(Items.DIRT);
        allowedItems.add(Items.GRAVEL);
        allowedItems.add(Items.SAND);
        allowedItems.add(Items.OBSIDIAN);
        allowedItems.add(Items.COAL_ORE);
        allowedItems.add(Items.IRON_ORE);
        allowedItems.add(Items.GOLD_ORE);
        allowedItems.add(Items.OAK_LEAVES);
        allowedItems.add(Items.OAK_SAPLING);
        allowedItems.add(Items.BOOKSHELF);
        allowedItems.add(Items.TNT);
        if (version.isNewerThan(VersionEnum.c0_0_19a_06)) {
            allowedItems.add(Items.SPONGE);
            if (version.isNewerThan(VersionEnum.c0_0_20ac0_27)) {
                allowedItems.add(Items.WHITE_WOOL);
                allowedItems.add(Items.ORANGE_WOOL);
                allowedItems.add(Items.MAGENTA_WOOL);
                allowedItems.add(Items.LIGHT_BLUE_WOOL);
                allowedItems.add(Items.YELLOW_WOOL);
                allowedItems.add(Items.LIME_WOOL);
                allowedItems.add(Items.PINK_WOOL);
                allowedItems.add(Items.CYAN_WOOL);
                allowedItems.add(Items.BLUE_WOOL);
                allowedItems.add(Items.BROWN_WOOL);
                allowedItems.add(Items.GREEN_WOOL);
                allowedItems.add(Items.BROWN_MUSHROOM);
                allowedItems.add(Items.GRAY_WOOL);
                allowedItems.add(Items.LIGHT_GRAY_WOOL);
                allowedItems.add(Items.PURPLE_WOOL);
                allowedItems.add(Items.RED_WOOL);
                allowedItems.add(Items.BLACK_WOOL);
                allowedItems.add(Items.SMOOTH_STONE_SLAB);
                allowedItems.add(Items.POPPY);
                allowedItems.add(Items.DANDELION);
                allowedItems.add(Items.RED_MUSHROOM);
            }
        }
        if (hasCustomBlocksV1) {
            allowedItems.add(Items.MAGMA_BLOCK);
            allowedItems.add(Items.QUARTZ_PILLAR);
            allowedItems.add(Items.SANDSTONE);
            allowedItems.add(Items.STONE_BRICKS);
            allowedItems.add(Items.COBBLESTONE_SLAB);
            allowedItems.add(Items.ICE);
            allowedItems.add(Items.SNOW);
            allowedItems.add(Items.BEDROCK);
            allowedItems.add(Items.WATER_BUCKET);
            allowedItems.add(Items.LAVA_BUCKET);
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
        super.render(context, mouseX, mouseY, delta);
    }
}
