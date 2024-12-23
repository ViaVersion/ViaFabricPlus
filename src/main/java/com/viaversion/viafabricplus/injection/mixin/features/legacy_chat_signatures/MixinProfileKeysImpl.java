/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.legacy_chat_signatures;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.viaversion.viafabricplus.injection.access.legacy_chat_signatures.ILegacyKeySignatureStorage;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeysImpl.class)
public abstract class MixinProfileKeysImpl {

    @Inject(method = "decodeKeyPairResponse", at = @At("RETURN"))
    private static void trackLegacyKey(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
        ((ILegacyKeySignatureStorage) (Object) cir.getReturnValue()).viafabricplus$setLegacyPublicKeySignature(((ILegacyKeySignatureStorage) (Object) keyPairResponse).viafabricplus$getLegacyPublicKeySignature());
    }

}
