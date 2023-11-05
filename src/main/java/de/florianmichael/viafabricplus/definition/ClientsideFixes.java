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
package de.florianmichael.viafabricplus.definition;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.FinishMinecraftLoadCallback;
import de.florianmichael.viafabricplus.event.LoadClassicProtocolExtensionCallback;
import de.florianmichael.viafabricplus.injection.MixinPlugin;
import de.florianmichael.viafabricplus.injection.access.IFontStorage;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * This class contains random fields and methods that are used to fix bugs on the client side
 */
public class ClientsideFixes {

    /**
     * A list of blocks that need to be reloaded when the protocol version changes to change bounding boxes
     */
    private static List<Block> RELOADABLE_BLOCKS;

    /**
     * Legacy versions do not support SRV records, so we need to resolve them manually
     */
    public final static VersionRange LEGACY_SRV_RESOLVE = VersionRange.andOlder(VersionEnum.r1_2_4tor1_2_5).add(VersionRange.single(VersionEnum.bedrockLatest));

    /**
     * Contains the armor points of all armor items in legacy versions (<= 1.8.x)
     */
    private final static Map<Item, Integer> LEGACY_ARMOR_POINTS = new HashMap<>();

    /**
     * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
     */
    private final static Map<String, Consumer<PacketByteBuf>> PENDING_EXECUTION_TASKS = new ConcurrentHashMap<>();

    /**
     * This identifier is an internal identifier that is used to identify packets that are sent by ViaFabricPlus
     */
    public final static String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();

    /**
     * The current chat limit
     */
    private static int currentChatLimit = 256;

    public static void init() {
        FinishMinecraftLoadCallback.EVENT.register(() -> {
            // Loads the armor points of all armor items in legacy versions (<= 1.8.x)
            for (Item armorItem : Arrays.asList(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_BOOTS,
                    Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS,
                    Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.DIAMOND_HELMET,
                    Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.GOLDEN_HELMET,
                    Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS)) {
                LEGACY_ARMOR_POINTS.put(armorItem, ArmorType.findByType(Registries.ITEM.getId(armorItem).toString()).getArmorPoints());
            }

            RELOADABLE_BLOCKS = Arrays.asList(Blocks.ANVIL, Blocks.WHITE_BED, Blocks.ORANGE_BED,
                    Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED,
                    Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED,
                    Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BREWING_STAND, Blocks.CAULDRON, Blocks.CHEST, Blocks.PITCHER_CROP,
                    Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.FARMLAND, Blocks.OAK_FENCE, Blocks.HOPPER, Blocks.LADDER,
                    Blocks.LILY_PAD, Blocks.GLASS_PANE, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE,
                    Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE,
                    Blocks.LIME_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE,
                    Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE,
                    Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE,
                    Blocks.RED_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.PISTON, Blocks.PISTON_HEAD,
                    Blocks.SNOW, Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL
            );
        });

        // Reloads some clientside stuff when the protocol version changes
        ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> {
            // Reloads all bounding boxes
            if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) { // Make sure that the game is loaded when reloading the cache
                for (Block block : RELOADABLE_BLOCKS) {
                    block.getDefaultState().initShapeCache();
                }
            }

            // Calculates the current chat limit, since it changes depending on the protocol version
            currentChatLimit = 256;
            if (protocolVersion.isOlderThanOrEqualTo(VersionEnum.r1_10)) {
                currentChatLimit = 100;
                if (protocolVersion.isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                    currentChatLimit = 64 - MinecraftClient.getInstance().getSession().getUsername().length() - 2;
                }
            }

            if (!MixinPlugin.DASH_LOADER_PRESENT) {
                // Reloads all font storages to fix the font renderer
                for (FontStorage storage : MinecraftClient.getInstance().fontManager.fontStorages.values()) {
                    RenderSystem.recordRenderCall(() -> ((IFontStorage) storage).viafabricplus_clearCaches());
                }
            }
        });

        // Calculates the current chat limit, since it changes depending on the protocol version
        LoadClassicProtocolExtensionCallback.EVENT.register(classicProtocolExtension -> {
            if (classicProtocolExtension == ClassicProtocolExtension.LONGER_MESSAGES) {
                currentChatLimit = Short.MAX_VALUE * 2;
            }
        });
    }

    /**
     * Executes a sync task and returns the uuid of the task
     *
     * @param task The task to execute
     * @return The uuid of the task
     */
    public static String executeSyncTask(final Consumer<PacketByteBuf> task) {
        final var uuid = UUID.randomUUID().toString();
        PENDING_EXECUTION_TASKS.put(uuid, task);
        return uuid;
    }

    public static void handleSyncTask(final PacketByteBuf buf) {
        buf.resetReaderIndex();

        final var uuid = buf.readString();

        if (PENDING_EXECUTION_TASKS.containsKey(uuid)) {
            MinecraftClient.getInstance().execute(() -> { // Execute the task on the main thread
                final var task = PENDING_EXECUTION_TASKS.get(uuid);
                PENDING_EXECUTION_TASKS.remove(uuid);

                task.accept(buf);
            });
        }
    }

    /**
     * Returns the armor points of an armor item in legacy versions (<= 1.8.x)
     *
     * @param itemStack The item stack to get the armor points from
     * @return The armor points of the item stack
     */
    public static int getLegacyArmorPoints(final ItemStack itemStack) {
        if (!LEGACY_ARMOR_POINTS.containsKey(itemStack.getItem())) return 0; // Just in case

        return LEGACY_ARMOR_POINTS.get(itemStack.getItem());
    }

    /**
     * Sets the result slot of a crafting screen handler to the correct item stack. In MC <= 1.11.2 the result slot
     * is not updated when the input slots change, so we need to update it manually, Spigot and Paper re-syncs the slot,
     * so we don't notice this bug on servers that use Spigot or Paper
     *
     * @param syncId        The sync id of the screen handler
     * @param screenHandler The screen handler
     * @param inventory     The inventory of the screen handler
     */
    public static void setCraftingResultSlot(final int syncId, final ScreenHandler screenHandler, final RecipeInputInventory inventory) {
        final var network = MinecraftClient.getInstance().getNetworkHandler();
        if (network == null) return;

        final var world = MinecraftClient.getInstance().world;

        final var result = network.getRecipeManager().
                getFirstMatch(RecipeType.CRAFTING, inventory, world). // Get the first matching recipe
                map(recipe -> recipe.value().craft(inventory, world.getRegistryManager())). // Craft the recipe to get the result
                orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateS2CPacket(syncId, screenHandler.getRevision(), 0, result));
    }

    public static int getCurrentChatLimit() {
        return currentChatLimit;
    }
}
