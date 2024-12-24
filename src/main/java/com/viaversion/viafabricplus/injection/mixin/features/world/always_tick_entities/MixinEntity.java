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

package com.viaversion.viafabricplus.injection.mixin.features.world.always_tick_entities;

import com.viaversion.viafabricplus.base.settings.impl.DebugSettings;
import com.viaversion.viafabricplus.injection.access.world.always_tick_entities.IEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Unique
    private boolean viaFabricPlus$isInLoadedChunkAndShouldTick;

    @Override
    public boolean viaFabricPlus$isInLoadedChunkAndShouldTick() {
        return this.viaFabricPlus$isInLoadedChunkAndShouldTick || DebugSettings.global().alwaysTickClientPlayer.isEnabled();
    }

    @Override
    public void viaFabricPlus$setInLoadedChunkAndShouldTick(final boolean inLoadedChunkAndShouldTick) {
        this.viaFabricPlus$isInLoadedChunkAndShouldTick = inLoadedChunkAndShouldTick;
    }

}
