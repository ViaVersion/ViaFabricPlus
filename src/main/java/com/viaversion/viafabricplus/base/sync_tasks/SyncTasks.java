/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.base.sync_tasks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class SyncTasks {

    /**
     * Contains all tasks that are waiting for a packet to be received, this system can be used to sync ViaVersion tasks with the correct thread
     */
    private static final Map<String, Consumer<RegistryFriendlyByteBuf>> PENDING_EXECUTION_TASKS = new ConcurrentHashMap<>();

    /**
     * This identifier is an internal identifier used to identify packets that are sent by ViaFabricPlus
     */
    public static final String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();

    public static void init() {
        DataCustomPayload.init();
    }

    /**
     * Executes a task synchronized with the main thread from networking threads
     *
     * @param task The task to execute
     * @return The uuid of the task
     */
    public static String executeSyncTask(final Consumer<RegistryFriendlyByteBuf> task) {
        final String uuid = UUID.randomUUID().toString();
        PENDING_EXECUTION_TASKS.put(uuid, task);
        return uuid;
    }

    /**
     * Handles a sync task that was sent from the networking thread
     *
     * @param buf The packet buffer
     */
    public static void handleSyncTask(final FriendlyByteBuf buf) {
        final String uuid = buf.readUtf();

        if (PENDING_EXECUTION_TASKS.containsKey(uuid)) {
            Minecraft.getInstance().execute(() -> { // Execute the task on the main thread
                final Consumer<RegistryFriendlyByteBuf> task = PENDING_EXECUTION_TASKS.remove(uuid);
                task.accept(new RegistryFriendlyByteBuf(buf, Minecraft.getInstance().getConnection().registryAccess()));
            });
        }
    }

}
