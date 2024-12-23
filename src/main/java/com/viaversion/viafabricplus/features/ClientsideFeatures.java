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

package com.viaversion.viafabricplus.features;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.event.ChangeProtocolVersionCallback;
import com.viaversion.viafabricplus.event.PostGameLoadCallback;
import com.viaversion.viafabricplus.features.data.EntityDimensionDiff;
import com.viaversion.viafabricplus.features.data.ResourcePackHeaderDiff;
import com.viaversion.viafabricplus.features.data.recipe.Recipes1_11_2;
import com.viaversion.viafabricplus.features.versioned.EnchantmentAttributesEmulation1_20_6;
import com.viaversion.viafabricplus.features2.cpe_extensions.CPEAdditions;
import com.viaversion.viafabricplus.features.versioned.classic.GridItemSelectionScreen;
import com.viaversion.viafabricplus.features.versioned.visual.ArmorHudEmulation1_8;
import com.viaversion.viafabricplus.features2.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viafabricplus.features.versioned.visual.UnicodeFontFix1_12_2;
import com.viaversion.viafabricplus.injection.access.IClientConnection;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.BedrockSettings;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import com.viaversion.viafabricplus.util.DataCustomPayload;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * This class contains random fields and methods that are used to fix bugs on the client side
 */
public class ClientsideFeatures {

    /**
     * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
     */
    private static final Map<String, Consumer<RegistryByteBuf>> PENDING_EXECUTION_TASKS = new ConcurrentHashMap<>();

    /**
     * This identifier is an internal identifier used to identify packets that are sent by ViaFabricPlus
     */
    @ApiStatus.Internal
    public static final String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();

    /**
     * This is an incremental index used for tablist entries to implement FIFO behavior <= 1.7
     */
    @ApiStatus.Internal
    public static int globalTablistIndex = 0;

    static {
        // Register additional CPE features
        CPEAdditions.modifyMappings();

        // Check if the pack format mappings are correct
        ResourcePackHeaderDiff.checkOutdated();

        UnicodeFontFix1_12_2.init();

        PostGameLoadCallback.EVENT.register(() -> {
            // Handle clientside enchantment calculations in <= 1.20.6
            EnchantmentAttributesEmulation1_20_6.init();

            // Handles and updates entity dimension changes in <= 1.17
            EntityDimensionDiff.init();

            // Ticks the armor hud manually in <= 1.8.x
            ArmorHudEmulation1_8.init();
        });

        // Reloads some clientside stuff when the protocol version changes
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            VisualSettings.global().filterNonExistingGlyphs.onValueChanged();

            // Reloads all bounding boxes of the blocks that we changed
            for (Block block : Registries.BLOCK) {
                if (block instanceof AnvilBlock || block instanceof BedBlock || block instanceof BrewingStandBlock
                        || block instanceof CarpetBlock || block instanceof CauldronBlock || block instanceof ChestBlock
                        || block instanceof EnderChestBlock || block instanceof EndPortalBlock || block instanceof EndPortalFrameBlock
                        || block instanceof FarmlandBlock || block instanceof FenceBlock || block instanceof FenceGateBlock
                        || block instanceof HopperBlock || block instanceof LadderBlock || block instanceof LeavesBlock
                        || block instanceof LilyPadBlock || block instanceof PaneBlock || block instanceof PistonBlock
                        || block instanceof PistonHeadBlock || block instanceof SnowBlock || block instanceof WallBlock
                        || block instanceof CropBlock || block instanceof FlowerbedBlock
                ) {
                    for (BlockState state : block.getStateManager().getStates()) {
                        state.initShapeCache();
                    }
                }
            }

            // Rebuilds the item selection screen grid
            if (newVersion.olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                GridItemSelectionScreen.INSTANCE.itemGrid = null;
            }

            // Reloads the clientside recipes
            if (newVersion.olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                Recipes1_11_2.reset();
            }

            // Reload sound system when switching between 3D Shareware and normal versions
            if (oldVersion.equals(AprilFoolsProtocolVersion.s3d_shareware) || newVersion.equals(AprilFoolsProtocolVersion.s3d_shareware)) {
                MinecraftClient.getInstance().getSoundManager().reloadSounds();
            }
        }));

        // Register the footstep particle
        FootStepParticle1_12_2.init();

        // Register the custom payload packet for sync tasks
        DataCustomPayload.init();
    }

    public static void init() {
        // Calls the static block
    }

    /**
     * Replaces the default port when parsing a server address if the default port should be replaced
     *
     * @param address The original address of the server
     * @param version The protocol version
     * @return The server address with the replaced default port
     */
    public static String replaceDefaultPort(final String address, final ProtocolVersion version) {
        // If the default port for this entry should be replaced, check if the address already contains a port
        // We can't just replace vanilla's default port because a bedrock server might be running on the same port
        if (BedrockSettings.global().replaceDefaultPort.getValue() && Objects.equals(version, BedrockProtocolVersion.bedrockLatest) && !address.contains(":")) {
            return address + ":" + ProtocolConstants.BEDROCK_DEFAULT_PORT;
        } else {
            return address;
        }
    }

    /**
     * Executes a task synchronized with the main thread from networking threads
     *
     * @param task The task to execute
     * @return The uuid of the task
     */
    public static String executeSyncTask(final Consumer<RegistryByteBuf> task) {
        final String uuid = UUID.randomUUID().toString();
        PENDING_EXECUTION_TASKS.put(uuid, task);
        return uuid;
    }

    @ApiStatus.Internal
    public static void handleSyncTask(final PacketByteBuf buf) {
        final String uuid = buf.readString();

        if (PENDING_EXECUTION_TASKS.containsKey(uuid)) {
            MinecraftClient.getInstance().execute(() -> { // Execute the task on the main thread
                final Consumer<RegistryByteBuf> task = PENDING_EXECUTION_TASKS.remove(uuid);
                task.accept(new RegistryByteBuf(buf, MinecraftClient.getInstance().getNetworkHandler().getRegistryManager()));
            });
        }
    }

}