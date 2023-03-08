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
package de.florianmichael.viafabricplus.definition.v1_19_0;

import de.florianmichael.viafabricplus.definition.v1_19_0.model.SignatureUpdatableModel;

import java.security.*;

public interface MessageSigner {

    byte[] sign(final SignatureUpdatableModel signer);

    static MessageSigner create(final PrivateKey privateKey, final String algorithm) {
        return signer -> {
            try {
                final Signature signature = Signature.getInstance(algorithm);
                signature.initSign(privateKey);

                signer.update(data -> {
                    try {
                        signature.update(data);
                    } catch (SignatureException e) {
                        throw new RuntimeException(e);
                    }
                });
                return signature.sign();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                throw new IllegalStateException("Failed to sign message", e);
            }
        };
    }
}
