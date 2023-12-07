/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.florianmichael.viafabricplus.fixes.data.recipe.RecipeInfo;
import de.florianmichael.viafabricplus.fixes.data.recipe.Recipes1_11_2;
import de.florianmichael.viafabricplus.injection.access.IDownloadingTerrainScreen;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.raphimc.vialoader.util.VersionEnum;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonNetworkHandler {

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Mutable
    @Shadow
    @Final
    private Set<PlayerListEntry> listedPlayerListEntries;

    @Shadow
    public abstract void onSimulationDistance(SimulationDistanceS2CPacket packet);

    @Shadow
    public abstract ClientConnection getConnection();

    @Shadow
    public abstract void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet);

    @Shadow protected abstract boolean isSecureChatEnforced();

    protected MixinClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @WrapWithCondition(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeChatPacketError(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_20_2);
    }

    @Redirect(method = "handlePlayerListAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;onGameModeChanged(Lnet/minecraft/world/GameMode;)V"))
    private void dontResetVelocity(ClientPlayerEntity instance, GameMode gameMode) {
        if (ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            instance.onGameModeChanged(gameMode);
        }
    }

    @WrapWithCondition(method = "setPublicSession", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeInvalidSignatureWarning(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_4);
    }

    @WrapWithCondition(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private boolean removeUnknownPlayerListEntryWarning(Logger instance, String s, Object object1, Object object2) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_3);
    }

    @Redirect(method = {"onEntityPosition", "onEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isLogicalSideForUpdatingMovement()Z"))
    private boolean allowPlayerToBeMovedByEntityPackets(Entity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3) || ProtocolHack.getTargetVersion().equals(VersionEnum.bedrockLatest)) {
            return instance.getControllingPassenger() instanceof PlayerEntity player ? player.isMainPlayer() : !instance.getWorld().isClient;
        }

        return instance.isLogicalSideForUpdatingMovement();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fixPlayerListOrdering(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            this.listedPlayerListEntries = new LinkedHashSet<>();
        }
    }

    @Redirect(method = "onServerMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;isSecureChatEnforced()Z"))
    private boolean removeSecureChatWarning(ClientPlayNetworkHandler instance) {
        return isSecureChatEnforced() || VisualSettings.global().disableSecureChatWarning.isEnabled();
    }

    @Inject(method = "onPlayerSpawnPosition", at = @At("RETURN"))
    public void moveDownloadingTerrainClosing(PlayerSpawnPositionS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isBetweenInclusive(VersionEnum.r1_19, VersionEnum.r1_20_2) && this.client.currentScreen instanceof IDownloadingTerrainScreen mixinDownloadingTerrainScreen) {
            mixinDownloadingTerrainScreen.viaFabricPlus$setReady();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    private void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18tor1_18_1) && this.client.currentScreen instanceof IDownloadingTerrainScreen mixinDownloadingTerrainScreen) {
            mixinDownloadingTerrainScreen.viaFabricPlus$setReady();
        }
    }

    @SuppressWarnings({"InvalidInjectorMethodSignature"})
    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = BoatEntity.class))
    private Class<?> dontChangeYawWhenMountingBoats(Object entity, Class<?> boatClass) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18tor1_18_1)) {
            return Integer.class;
        }
        return boatClass;
    }

    @Inject(method = "onChunkLoadDistance", at = @At("RETURN"))
    private void emulateSimulationDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_17_1)) {
            this.onSimulationDistance(new SimulationDistanceS2CPacket(packet.getDistance()));
        }
    }

    @Redirect(method = "onEntityPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateTrackedPositionAndAngles(DDDFFI)V"))
    private void cancelSmallChanges(Entity instance, double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_1) && Math.abs(instance.getX() - x) < 0.03125 && Math.abs(instance.getY() - y) < 0.015625 && Math.abs(instance.getZ() - z) < 0.03125) {
            instance.updateTrackedPositionAndAngles(instance.getX(), instance.getY(), instance.getZ(), yaw, pitch, ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2) ? 0 : interpolationSteps);
        } else {
            instance.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps);
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void sendAdditionalData(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_11_1to1_11_2)) {
            final List<RecipeEntry<?>> recipes = new ArrayList<>();
            final List<RecipeInfo<?>> recipeInfos = Recipes1_11_2.getRecipes();
            for (int i = 0; i < recipeInfos.size(); i++) {
                recipes.add(recipeInfos.get(i).create(new Identifier("viafabricplus", "recipe/" + i)));
            }
            this.onSynchronizeRecipes(new SynchronizeRecipesS2CPacket(recipes));
        }
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            this.onEntityStatus(new EntityStatusS2CPacket(this.client.player, (byte) 28)); // Op-level 4
        }
    }

}
