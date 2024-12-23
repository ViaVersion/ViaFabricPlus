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

import com.viaversion.viafabricplus.features.versioned.PendingUpdateManager1_18_2;
import com.viaversion.viafabricplus.injection.access.IEntity;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.EntityList;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientWorld.class, priority = 900)
public abstract class MixinClientWorld extends World {

    @Shadow
    @Final
    EntityList entityList;

    @Mutable
    @Shadow
    @Final
    private PendingUpdateManager pendingUpdateManager;

    protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void removePendingUpdateManager(CallbackInfo ci) {
        if (DebugSettings.global().disableSequencing.isEnabled()) {
            this.pendingUpdateManager = new PendingUpdateManager1_18_2();
        }
    }

    /**
     * @author RK_01
     * @reason Versions <= 1.8.x and >= 1.17 always tick entities, even if they are not in a loaded chunk.
     */
    @Overwrite
    public void tickEntity(Entity entity) {
        entity.resetPosition();
        final IEntity mixinEntity = (IEntity) entity;
        if (mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick() || entity.isSpectator()) {
            entity.age++;
            Profilers.get().push(() -> Registries.ENTITY_TYPE.getId(entity.getType()).toString());
            entity.tick();
            Profilers.get().pop();
        }
        this.viaFabricPlus$checkChunk(entity);

        if (mixinEntity.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
            for (Entity entity2 : entity.getPassengerList()) {
                this.tickPassenger(entity, entity2);
            }
        }
    }

    /**
     * @author RK_01
     * @reason Versions <= 1.8.x and >= 1.17 always tick entities, even if they are not in a loaded chunk.
     */
    @Overwrite
    private void tickPassenger(Entity entity, Entity passenger) {
        if (!passenger.isRemoved() && passenger.getVehicle() == entity) {
            if (passenger instanceof PlayerEntity || this.entityList.has(passenger)) {
                final IEntity mixinPassenger = (IEntity) passenger;
                passenger.resetPosition();
                if (mixinPassenger.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
                    passenger.age++;
                    passenger.tickRiding();
                }
                this.viaFabricPlus$checkChunk(passenger);

                if (mixinPassenger.viaFabricPlus$isInLoadedChunkAndShouldTick()) {
                    for (Entity entity2 : passenger.getPassengerList()) {
                        this.tickPassenger(passenger, entity2);
                    }
                }
            }
        } else {
            passenger.stopRiding();
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
