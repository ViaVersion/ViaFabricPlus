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

package com.viaversion.viafabricplus.injection.mixin.features.networking.packet_handling;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private ClientWorld world;

    @Unique
    private Packet<?> viaFabricPlus$teleportConfirmPacket;

    protected MixinClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Redirect(method = "onVehicleMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;distanceTo(Lnet/minecraft/util/math/Vec3d;)D"))
    private double allowSmallValues(Vec3d instance, Vec3d vec) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return Integer.MAX_VALUE;
        } else {
            return instance.distanceTo(vec);
        }
    }

    @WrapWithCondition(method = "onPlayerPositionLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private boolean changePacketOrder(ClientConnection instance, Packet<?> packet) {
        final boolean cancel = ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_21_2);
        if (cancel) {
            this.viaFabricPlus$teleportConfirmPacket = packet;
        }
        return !cancel;
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    private void changePacketOrder(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (viaFabricPlus$teleportConfirmPacket != null) {
            this.connection.send(viaFabricPlus$teleportConfirmPacket);
            viaFabricPlus$teleportConfirmPacket = null;
        }
    }

    @WrapWithCondition(method = "onEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/TrackedPosition;setPos(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private boolean dontHandleEntityPositionChange(TrackedPosition instance, Vec3d pos) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2);
    }

    @Redirect(method = "onEntityPosition", at = @At(value = "INVOKE", target = "Ljava/util/OptionalInt;isPresent()Z"))
    private boolean dontHandleRemovedVehiclePositionChange(OptionalInt instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_21) && instance.isPresent();
    }

    @Redirect(method = "onSignEditorOpen", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private void openEmptySignEditor(Logger instance, String format, Object arg1, Object arg2, @Local(argsOnly = true) SignEditorOpenS2CPacket packet) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            final BlockPos pos = packet.getPos();

            final SignBlockEntity emptySignBlockEntity = new SignBlockEntity(pos, this.world.getBlockState(pos));
            emptySignBlockEntity.setWorld(this.world);
            this.client.player.openEditSignScreen(emptySignBlockEntity, packet.isFront());
        } else {
            instance.warn(format, arg1, arg2);
        }
    }

    @WrapWithCondition(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;setBaseFrom(Lnet/minecraft/entity/attribute/AttributeContainer;)V"))
    private boolean dontApplyBaseValues(AttributeContainer instance, AttributeContainer other) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21);
    }

    @Redirect(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0))
    private void handleWinGameState0(MinecraftClient instance, Screen screen, @Local int i) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            if (i == 0) {
                this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
//                this.client.setScreen(new DownloadingTerrainScreen(() -> false, DownloadingTerrainScreen.WorldEntryReason.END_PORTAL)); // TODO
            } else if (i == 1) {
                instance.setScreen(screen);
            }
        } else {
            instance.setScreen(screen);
        }
    }

    @WrapWithCondition(method = "onEnterReconfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendAcknowledgment()V"))
    private boolean dontSendChatAck(ClientPlayNetworkHandler instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5);
    }

    @Redirect(method = "onOpenWrittenBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/BookScreen$Contents;create(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/gui/screen/ingame/BookScreen$Contents;"))
    private BookScreen.Contents dontOpenWriteableBookScreen(ItemStack stack) {
        if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5) || stack.isOf(Items.WRITTEN_BOOK)) {
            return BookScreen.Contents.create(stack);
        } else {
            return null;
        }
    }

    @WrapWithCondition(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;startWorldLoading(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/world/LevelLoadingScreen$WorldEntryReason;)V"))
    private boolean checkDimensionChange(ClientPlayNetworkHandler instance, ClientPlayerEntity player, ClientWorld world, LevelLoadingScreen.WorldEntryReason reason, @Local(ordinal = 0) RegistryKey<World> registryKey) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_3) || registryKey != this.client.player.getEntityWorld().getRegistryKey();
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

    @Redirect(method = {"onEntityPositionSync", "onEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isLogicalSideForUpdatingMovement()Z"))
    private boolean allowPlayerToBeMovedByEntityPackets(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_3) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return instance.getControllingPassenger() instanceof PlayerEntity player ? player.isMainPlayer() : !instance.getEntityWorld().isClient();
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

    @SuppressWarnings({"InvalidInjectorMethodSignature"})
    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = AbstractBoatEntity.class))
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

    @Redirect(method = "setPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateTrackedPositionAndAngles(Lnet/minecraft/util/math/Vec3d;FF)V"))
    private static void cancelSmallChanges(Entity instance, Vec3d pos, float yaw, float pitch) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_1) && Math.abs(instance.getX() - pos.x) < 0.03125 && Math.abs(instance.getY() - pos.y) < 0.015625 && Math.abs(instance.getZ() - pos.z) < 0.03125) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && instance.getInterpolator() != null) {
                instance.getInterpolator().setLerpDuration(0);
            }
            instance.updateTrackedPositionAndAngles(instance.getEntityPos(), yaw, pitch);
        } else {
            instance.updateTrackedPositionAndAngles(pos, yaw, pitch);
        }
    }
}
