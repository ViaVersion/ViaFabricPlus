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

package com.viaversion.viafabricplus.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SyncTasks {

    /**
     * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
     */
    private static final Map<String, Consumer<RegistryByteBuf>> PENDING_EXECUTION_TASKS = new ConcurrentHashMap<>();

    /**
     * This identifier is an internal identifier used to identify packets that are sent by ViaFabricPlus
     */
    public static final String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();

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

    /**
     * Handles a sync task that was sent from the networking thread
     *
     * @param buf The packet buffer
     */
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
