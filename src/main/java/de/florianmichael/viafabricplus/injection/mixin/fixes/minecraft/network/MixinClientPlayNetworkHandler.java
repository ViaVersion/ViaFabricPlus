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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.fixes.data.recipe.RecipeInfo;
import de.florianmichael.viafabricplus.fixes.data.recipe.Recipes1_11_2;
import de.florianmichael.viafabricplus.injection.access.IDownloadingTerrainScreen;
import de.florianmichael.viafabricplus.injection.access.IPlayerListHud;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonNetworkHandler {

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

    @Shadow
    protected abstract boolean isSecureChatEnforced();

    @Shadow
    public abstract void sendChatCommand(String command);

    protected MixinClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Redirect(method = "sendChatCommand", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean alwaysSignCommands(List<?> instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            return false;
        } else {
            return instance.isEmpty();
        }
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void alwaysSendSignedCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            sendChatCommand(command);
            cir.setReturnValue(true);
        }
    }

    @WrapWithCondition(method = "onEnterReconfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendAcknowledgment()V"))
    private boolean dontSendChatAck(ClientPlayNetworkHandler instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5);
    }

    @WrapWithCondition(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;startWorldLoading(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;)V"))
    private boolean checkDimensionChange(ClientPlayNetworkHandler instance, ClientPlayerEntity player, ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, @Local(ordinal = 0) RegistryKey<World> registryKey) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_3) || registryKey != this.client.player.getWorld().getRegistryKey();
    }

    @WrapWithCondition(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeChatPacketError(Logger instance, String s, Object o) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_2);
    }

    @Redirect(method = "handlePlayerListAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;onGameModeChanged(Lnet/minecraft/world/GameMode;)V"))
    private void dontResetVelocity(ClientPlayerEntity instance, GameMode gameMode) {
        if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20)) {
            instance.onGameModeChanged(gameMode);
        }
    }

    @WrapWithCondition(method = "setPublicSession", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeInvalidSignatureWarning(Logger instance, String s, Object o) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_4);
    }

    @WrapWithCondition(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private boolean removeUnknownPlayerListEntryWarning(Logger instance, String s, Object object1, Object object2) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_3);
    }

    @Redirect(method = {"onEntityPosition", "onEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isLogicalSideForUpdatingMovement()Z"))
    private boolean allowPlayerToBeMovedByEntityPackets(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_3) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return instance.getControllingPassenger() instanceof PlayerEntity player ? player.isMainPlayer() : !instance.getWorld().isClient;
        } else {
            return instance.isLogicalSideForUpdatingMovement();
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fixPlayerListOrdering(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            this.listedPlayerListEntries = new LinkedHashSet<>();
        }
    }

    @Redirect(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;isSecureChatEnforced()Z"))
    private boolean removeSecureChatWarning(ClientPlayNetworkHandler instance) {
        return isSecureChatEnforced() || VisualSettings.global().disableSecureChatWarning.isEnabled();
    }

    @Inject(method = "onPlayerSpawnPosition", at = @At("RETURN"))
    private void moveDownloadingTerrainClosing(PlayerSpawnPositionS2CPacket packet, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_18_2, ProtocolVersion.v1_20_2) && this.client.currentScreen instanceof IDownloadingTerrainScreen mixinDownloadingTerrainScreen) {
            mixinDownloadingTerrainScreen.viaFabricPlus$setReady();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    private void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18) && this.client.currentScreen instanceof IDownloadingTerrainScreen mixinDownloadingTerrainScreen) {
            mixinDownloadingTerrainScreen.viaFabricPlus$setReady();
        }
    }

    @SuppressWarnings({"InvalidInjectorMethodSignature"})
    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = BoatEntity.class))
    private Class<?> dontChangeYawWhenMountingBoats(Object entity, Class<?> boatClass) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18)) {
            return Integer.class; // Dummy class file to false the instanceof check
        } else {
            return boatClass;
        }
    }

    @Inject(method = "onChunkLoadDistance", at = @At("RETURN"))
    private void emulateSimulationDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_17_1)) {
            this.onSimulationDistance(new SimulationDistanceS2CPacket(packet.getDistance()));
        }
    }

    @Redirect(method = "onEntityPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateTrackedPositionAndAngles(DDDFFI)V"))
    private void cancelSmallChanges(Entity instance, double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_1) && Math.abs(instance.getX() - x) < 0.03125 && Math.abs(instance.getY() - y) < 0.015625 && Math.abs(instance.getZ() - z) < 0.03125) {
            instance.updateTrackedPositionAndAngles(instance.getX(), instance.getY(), instance.getZ(), yaw, pitch, ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) ? 0 : interpolationSteps);
        } else {
            instance.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps);
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void sendRecipes(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
            final List<RecipeEntry<?>> recipes = new ArrayList<>();
            final List<RecipeInfo> recipeInfos = Recipes1_11_2.getRecipes(ProtocolTranslator.getTargetVersion());
            for (int i = 0; i < recipeInfos.size(); i++) {
                recipes.add(recipeInfos.get(i).create(new Identifier("viafabricplus", "recipe/" + i)));
            }
            this.onSynchronizeRecipes(new SynchronizeRecipesS2CPacket(recipes));
        }
        ClientsideFixes.GLOBAL_TABLIST_INDEX = 0;
        ((IPlayerListHud) MinecraftClient.getInstance().inGameHud.getPlayerListHud()).viaFabricPlus$setMaxPlayers(packet.maxPlayers());
    }

}
