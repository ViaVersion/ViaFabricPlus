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
package de.florianmichael.viafabricplus.definition.v1_19_0.storage;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.viafabricplus.definition.v1_19_0.JsonHelper;
import de.florianmichael.viafabricplus.definition.v1_19_2.model.MessageMetadataModel;
import de.florianmichael.viafabricplus.definition.v1_19_2.storage.AbstractChatSession;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.UUID;

public class ChatSession1_19_0 extends AbstractChatSession {

    public ChatSession1_19_0(UserConnection user, ProfileKey profileKey, PrivateKey privateKey) {
        super(user, profileKey, privateKey);
    }

    public byte[] sign(final UUID sender, final MessageMetadataModel messageMetadata) {
        return getSigner().sign(updater -> {
            final byte[] data = new byte[32];
            final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

            buffer.putLong(messageMetadata.salt());
            buffer.putLong(sender.getMostSignificantBits()).putLong(sender.getLeastSignificantBits());
            buffer.putLong(Instant.ofEpochMilli(messageMetadata.timestamp()).getEpochSecond());

            updater.update(data);
            updater.update(JsonHelper.toSortedString(TextComponentSerializer.V1_18.serializeJson(new StringComponent(messageMetadata.plain()))).getBytes(StandardCharsets.UTF_8));
        });
    }
}
