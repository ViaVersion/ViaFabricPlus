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

package de.florianmichael.viafabricplus.injection.mixin.base.perserverversion;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.injection.access.IPerformanceLog;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PerformanceLog.class)
public abstract class MixinPerformanceLog implements IPerformanceLog {

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
