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

package com.viaversion.viafabricplus.injection.mixin.features.world.always_tick_entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.injection.access.world.always_tick_entities.IEntity;
import java.util.List;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EntityList;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {

    @Shadow
    @Final
    EntityList entityList;

    protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow
    protected abstract void tickPassenger(final Entity entity, final Entity passenger);

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void alwaysTickEntities(Entity entity, CallbackInfo ci) {
        final IEntity mixinEntity = (IEntity) entity;
        if (!mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick() && !entity.isSpectator()) {
            entity.resetPosition();
            this.viaFabricPlus$checkChunk(entity);
            if (mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
                for (Entity entity2 : entity.getPassengerList()) {
                    this.tickPassenger(entity, entity2);
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "tickPassenger", at = @At("HEAD"), cancellable = true)
    private void alwaysTickEntities(Entity entity, Entity passenger, CallbackInfo ci) {
        final IEntity mixinPassenger = (IEntity) passenger;
        if (!mixinPassenger.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
            if (passenger.isRemoved() || passenger.getVehicle() != entity) {
                passenger.stopRiding();
            } else if (passenger instanceof PlayerEntity || this.entityList.has(passenger)) {
                passenger.resetPosition();
                this.viaFabricPlus$checkChunk(passenger);
                if (mixinPassenger.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
                    for (Entity entity2 : passenger.getPassengerList()) {
                        this.tickPassenger(passenger, entity2);
                    }
                }
            }
            ci.cancel();
        }
    }

    @WrapOperation(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPassengerList()Ljava/util/List;"))
    private List<Entity> alwaysTickEntities(Entity instance, Operation<List<Entity>> original) {
        this.viaFabricPlus$checkChunk(instance);
        final IEntity mixinEntity = (IEntity) instance;
        if (mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
            return original.call(instance);
        } else {
            return List.of();
        }
    }

    @Unique
    private void viaFabricPlus$checkChunk(Entity entity) {
        final IEntity mixinEntity = (IEntity) entity;
        final int chunkX = MathHelper.floor(entity.getX() / 16.0D);
        final int chunkZ = MathHelper.floor(entity.getZ() / 16.0D);
        if (!mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick() || entity.getChunkPos().x != chunkX || entity.getChunkPos().z != chunkZ) {
            if (!(this.getChunk(chunkX, chunkZ).isEmpty())) {
                mixinEntity.viaFabricPlus$setInLoadedChunkAndShouldTick(true);
            }
        }
    }

}
