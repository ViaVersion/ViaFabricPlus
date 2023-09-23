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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.passive.AllayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayEntity.class)
public abstract class MixinAllayEntity extends PathAwareEntity {

    protected MixinAllayEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getUnscaledRidingOffset", at = @At("HEAD"), cancellable = true)
    public void changeHeightOffset(CallbackInfoReturnable<Float> cir) {
        final var ver = ProtocolHack.getTargetVersion();

        if (ver.isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            cir.setReturnValue(ver.isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2) ? 0.0F : 0.4F);
        }
    }
}
