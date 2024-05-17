/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.versioned.classic;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.buffer.ByteBuf;
import net.lenni0451.reflect.Enums;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.packet.ClientboundPacketsc0_30cpe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CPEAdditions {

    public final static List<ClassicProtocolExtension> ALLOWED_EXTENSIONS = Arrays.asList(ClassicProtocolExtension.ENV_WEATHER_TYPE);
    public final static Map<Integer, ClientboundPacketsc0_30cpe> CUSTOM_PACKETS = new HashMap<>();

    public static ClientboundPacketsc0_30cpe EXT_WEATHER_TYPE;

    public static void modifyMappings() {
        EXT_WEATHER_TYPE = createNewPacket(ClassicProtocolExtension.ENV_WEATHER_TYPE, 31, (user, buf) -> buf.readByte());
    }

    public static void allowExtension(final ClassicProtocolExtension classicProtocolExtension) {
        ALLOWED_EXTENSIONS.add(classicProtocolExtension);
    }

    public static ClientboundPacketsc0_30cpe createNewPacket(final ClassicProtocolExtension classicProtocolExtension, final int packetId, final BiConsumer<UserConnection, ByteBuf> packetSplitter) {
        final ClientboundPacketsc0_30cpe packet = Enums.newInstance(ClientboundPacketsc0_30cpe.class, classicProtocolExtension.getName(), ClassicProtocolExtension.values().length, new Class[] { int.class, BiConsumer.class }, new Object[] { packetId, packetSplitter });
        Enums.addEnumInstance(ClientboundPacketsc0_30cpe.class, packet);
        CUSTOM_PACKETS.put(packetId, packet);

        return packet;
    }

}
