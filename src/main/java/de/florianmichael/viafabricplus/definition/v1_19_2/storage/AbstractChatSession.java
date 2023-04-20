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
package de.florianmichael.viafabricplus.definition.v1_19_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.viafabricplus.definition.v1_19_2.MessageSigner;

import java.security.PrivateKey;

public abstract class AbstractChatSession extends StoredObject {
    private final ProfileKey profileKey;

    private final MessageSigner signer;

    public AbstractChatSession(UserConnection user, final ProfileKey profileKey, final PrivateKey privateKey) {
        super(user);
        this.profileKey = profileKey;

        this.signer = MessageSigner.create(privateKey, "SHA256withRSA");
    }

    public ProfileKey getProfileKey() {
        return profileKey;
    }

    public MessageSigner getSigner() {
        return signer;
    }
}
