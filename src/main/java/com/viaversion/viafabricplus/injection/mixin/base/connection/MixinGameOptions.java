/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.base.connection;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions {

    @Shadow
    public boolean useNativeTransport;

    /**
     * @author RK_01
     * @reason Needed as an indicator if the client wants to ping a server or connect to a server
     */
    @Overwrite
    public boolean shouldUseNativeTransport() {
        if (!this.useNativeTransport) {
            ViaFabricPlusImpl.INSTANCE.getLogger().error("Native transport is disabled, but enabling it anyway since we use it as an indicator if the client wants to ping a server or connect to a server.");
        }
        return true;
    }

}
