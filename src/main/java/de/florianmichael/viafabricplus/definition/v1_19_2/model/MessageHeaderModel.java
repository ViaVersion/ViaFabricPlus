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
package de.florianmichael.viafabricplus.definition.v1_19_2.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public record MessageHeaderModel(UUID sender, byte[] precedingSignature) {

    public byte[] toByteArray(final UUID uuid) {
        final byte[] data = new byte[16];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return data;
    }

    public void updater(final byte[] bodyDigest, final SignatureUpdaterModel updater) {
        if (precedingSignature != null) {
            updater.update(precedingSignature);
        }

        updater.update(toByteArray(sender()));
        updater.update(bodyDigest);
    }
}
