/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.v26_1.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camel.class)
public abstract class MixinCamel extends AbstractHorse {

    public MixinCamel(EntityType<? extends AbstractHorse> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "getBodyAnchorAnimationYOffset", constant = @Constant(doubleValue = 0.375))
    private double scaleSitOffset(double constant, @Local(ordinal = 1, argsOnly = true) float scale) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1)) {
            return constant * scale;
        } else {
            return constant;
        }
    }

    @Redirect(method = "getBodyAnchorAnimationYOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/camel/Camel;isBaby()Z"))
    private boolean removeBabySitOffset(Camel instance) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v26_1) && instance.isBaby();
    }

    @Redirect(method = "getDefaultDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/camel/Camel;isBaby()Z"))
    private boolean dontChangeScale(Camel instance) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v26_1) && instance.isBaby();
    }
}
