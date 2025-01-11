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

package com.viaversion.viafabricplus.injection.mixin.base.access;

import com.viaversion.viafabricplus.injection.access.base.IMultiValueDebugSampleLogImpl;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiValueDebugSampleLogImpl.class)
public abstract class MixinMultiValueDebugSampleLogImpl implements IMultiValueDebugSampleLogImpl {

    @Unique
    private ProtocolVersion viaFabricPlus$forcedVersion;

    @Override
    public ProtocolVersion viaFabricPlus$getForcedVersion() {
        return this.viaFabricPlus$forcedVersion;
    }

    @Override
    public void viaFabricPlus$setForcedVersion(ProtocolVersion version) {
        this.viaFabricPlus$forcedVersion = version;
    }

}
