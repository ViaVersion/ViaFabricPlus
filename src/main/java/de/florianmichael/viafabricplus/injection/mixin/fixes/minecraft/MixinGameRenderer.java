/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    MinecraftClient client;

    @ModifyExpressionValue(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult bedrockReachAroundRaycast(HitResult hitResult) {
        if (ProtocolHack.getTargetVersion().equals(VersionEnum.bedrockLatest)) {
            final Entity entity = this.client.getCameraEntity();
            if (hitResult.getType() != HitResult.Type.MISS) return hitResult;
            if (!this.viaFabricPlus$canReachAround(entity)) return hitResult;

            final int x = MathHelper.floor(entity.getX());
            final int y = MathHelper.floor(entity.getY() - 0.2F);
            final int z = MathHelper.floor(entity.getZ());
            final BlockPos floorPos = new BlockPos(x, y, z);

            return new BlockHitResult(floorPos.toCenterPos(), entity.getHorizontalFacing(), floorPos, false);
        }

        return hitResult;
    }

    @Unique
    private boolean viaFabricPlus$canReachAround(final Entity entity) {
        return entity.isOnGround() && entity.getVehicle() == null && entity.getPitch() >= 45;
    }

}
