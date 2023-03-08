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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_16_2to1_16_1;

import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets.WorldPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Copyright RaphiMC/RK_01 - LICENSE file
@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets_2 {

    @ModifyConstant(method = "lambda$register$1", constant = @Constant(intValue = 16))
    private static int modifySectionCountToSupportClassicWorldHeight(int constant) {
        return 64;
    }
}
