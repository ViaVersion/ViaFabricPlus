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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DataCustomPayload(FriendlyByteBuf buf) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DataCustomPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.parse(SyncTasks.PACKET_SYNC_IDENTIFIER));

    public static void init() {
        PayloadTypeRegistry.playS2C().register(DataCustomPayload.ID, CustomPacketPayload.codec((value, buf) -> {
            throw new UnsupportedOperationException("DataCustomPayload is a read-only packet");
        }, buf -> new DataCustomPayload(new FriendlyByteBuf(Unpooled.copiedBuffer(buf.readSlice(buf.readableBytes()))))));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
