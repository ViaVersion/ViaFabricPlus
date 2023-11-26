/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes;

import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.DisconnectCallback;
import de.florianmichael.viafabricplus.event.LoadClassicProtocolExtensionCallback;
import de.florianmichael.viafabricplus.event.PostGameLoadCallback;
import de.florianmichael.viafabricplus.fixes.classic.CustomClassicProtocolExtensions;
import de.florianmichael.viafabricplus.fixes.classic.screen.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus.injection.ViaFabricPlusMixinPlugin;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.network.PacketByteBuf;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * This class contains random fields and methods that are used to fix bugs on the client side
 */
public class ClientsideFixes {

    /**
     * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
     */
    private static final Map<String, Consumer<PacketByteBuf>> PENDING_EXECUTION_TASKS = new ConcurrentHashMap<>();

    /**
     * This identifier is an internal identifier that is used to identify packets that are sent by ViaFabricPlus
     */
    public static final String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();

    /**
     * The current chat limit
     */
    private static int currentChatLength = 256;

    public static void init() {
        CustomClassicProtocolExtensions.create();
        PostGameLoadCallback.EVENT.register(() -> {
            EntityHitboxUpdateListener.init();
            ArmorUpdateListener.init();
            BlockFixes.init();
        });

        DisconnectCallback.EVENT.register(() -> {
            // Reset the MP-pass
            ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = null;
            // Remove all previous unacked player actions
            ClientPlayerInteractionManager1_18_2.clearUnackedActions();
        });

        // Reloads some clientside stuff when the protocol version changes
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> {
            // Calculates the current chat length limit
            if (newVersion.isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                currentChatLength = 64 - (MinecraftClient.getInstance().getSession().getUsername().length() + 2);
            } else if (newVersion.equals(VersionEnum.bedrockLatest)) {
                currentChatLength = 512;
            } else if (newVersion.isOlderThanOrEqualTo(VersionEnum.r1_9_3tor1_9_4)) {
                currentChatLength = 100;
            } else {
                currentChatLength = 256;
            }

            // Text Renderer invisible character fix
            if (!ViaFabricPlusMixinPlugin.DASH_LOADER_PRESENT) {
                for (FontStorage storage : MinecraftClient.getInstance().fontManager.fontStorages.values()) {
                    storage.glyphRendererCache.clear();
                    storage.glyphCache.clear();
                }
            }

            if (newVersion.isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                ClassicItemSelectionScreen.INSTANCE.reload(newVersion, false);
            }
        }));

        // Calculates the current chat limit, since it changes depending on the protocol version
        LoadClassicProtocolExtensionCallback.EVENT.register(classicProtocolExtension -> {
            if (classicProtocolExtension == ClassicProtocolExtension.LONGER_MESSAGES) {
                currentChatLength = Short.MAX_VALUE * 2;
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
        final var uuid = buf.readString();

        if (PENDING_EXECUTION_TASKS.containsKey(uuid)) {
            MinecraftClient.getInstance().execute(() -> { // Execute the task on the main thread
                final var task = PENDING_EXECUTION_TASKS.remove(uuid);
                task.accept(buf);
            });
        }
    }

    public static int getCurrentChatLength() {
        return currentChatLength;
    }

}
