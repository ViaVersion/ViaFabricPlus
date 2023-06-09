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

import net.minecraft.network.PacketByteBuf;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PacketSyncBase {
    public final static String PACKET_SYNC_IDENTIFIER = UUID.randomUUID() + ":" + UUID.randomUUID();
    private final static Map<String, Consumer<PacketByteBuf>> tasks = new ConcurrentHashMap<>();

    public static Consumer<PacketByteBuf> get(final String uuid) {
        final var task = tasks.get(uuid);
        tasks.remove(uuid);
        return task;
    }

    public static boolean has(final String uuid) {
        return tasks.containsKey(uuid);
    }

    public static String track(final Consumer<PacketByteBuf> task) {
        final String uuid = UUID.randomUUID().toString();
        tasks.put(uuid, task);
        return uuid;
    }
}
