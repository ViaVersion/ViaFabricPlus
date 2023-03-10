/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.definition.c0_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.buffer.ByteBuf;
import net.lenni0451.reflect.Enums;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.ClientboundPacketsc0_30cpe;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CustomClassicProtocolExtensions {
    public final static List<ClassicProtocolExtension> ALLOWED_EXTENSIONS = Arrays.asList(ClassicProtocolExtension.ENV_WEATHER_TYPE);

    public final static Map<Integer, ClientboundPacketsc0_30cpe> CUSTOM_PACKETS = new HashMap<>();
    public static ClientboundPacketsc0_30cpe EXT_WEATHER_TYPE;

    public static void reflect() {
        EXT_WEATHER_TYPE = createNewPacket(ClassicProtocolExtension.ENV_WEATHER_TYPE, 31, (user, buf) -> buf.readByte());
    }

    public static void onAddServerExtension(final ClassicProtocolExtension extension) {
        if (extension == ClassicProtocolExtension.LONGER_MESSAGES) ChatLengthDefinition.expand();
        if (extension == ClassicProtocolExtension.CUSTOM_BLOCKS) ClassicItemSelectionScreen.INSTANCE.reload(ViaLoadingBase.getClassWrapper().getTargetVersion(), true);
    }

    public static ClientboundPacketsc0_30cpe createNewPacket(final ClassicProtocolExtension classicProtocolExtension, final int packetId, final BiConsumer<UserConnection, ByteBuf> packetSplitter) {
        final ClientboundPacketsc0_30cpe packet = Enums.newInstance(ClientboundPacketsc0_30cpe.class, classicProtocolExtension.getName(), ClassicProtocolExtension.values().length, new Class[] { int.class, BiConsumer.class }, new Object[] { packetId, packetSplitter });
        Enums.addEnumInstance(ClientboundPacketsc0_30cpe.class, packet);
        CUSTOM_PACKETS.put(packetId, packet);
        return packet;
    }
}
