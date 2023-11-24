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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IKeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeysImpl.class)
public class MixinProfileKeysImpl {

    @Inject(method = "decodeKeyPairResponse", at = @At("RETURN"))
    private static void trackLegacyKey(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
        ((IPublicKeyData) (Object) cir.getReturnValue()).viaFabricPlus$setV1Key(((IKeyPairResponse) (Object) keyPairResponse).viafabricplus$getLegacyPublicKeySignature());
    }
}
