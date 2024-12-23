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

package com.viaversion.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.viaversion.viaversion.protocols.v1_11_1to1_12.rewriter.EntityPacketRewriter1_12$1", remap = false)
public abstract class MixinEntityPacketRewriter1_12 {

    @Redirect(method = "lambda$register$1", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;newerThanOrEqualTo(Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;)Z"))
    private static boolean dontClearRecipes(ProtocolVersion instance, ProtocolVersion other) {
        return false;
    }

}
