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
package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.injection.access.IPerformanceLog;
import net.minecraft.util.profiler.PerformanceLog;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PerformanceLog.class)
public abstract class MixinPerformanceLog implements IPerformanceLog {

    @Unique
    private VersionEnum viaFabricPlus$forcedVersion;

    @Override
    public VersionEnum viaFabricPlus$getForcedVersion() {
        return this.viaFabricPlus$forcedVersion;
    }

    @Override
    public void viaFabricPlus$setForcedVersion(VersionEnum version) {
        this.viaFabricPlus$forcedVersion = version;
    }

}
