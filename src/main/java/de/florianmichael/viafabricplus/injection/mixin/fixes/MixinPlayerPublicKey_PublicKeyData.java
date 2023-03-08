/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes;

import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(PlayerPublicKey.PublicKeyData.class)
public class MixinPlayerPublicKey_PublicKeyData implements IPublicKeyData {

    @Unique
    private ByteBuffer protocolhack_1_19_0Key;

    @Override
    public ByteBuffer viafabricplus_get1_19_0Key() {
        return protocolhack_1_19_0Key;
    }

    @Override
    public void viafabricplus_set1_19_0Key(ByteBuffer byteBuffer) {
        this.protocolhack_1_19_0Key = byteBuffer;
    }
}
