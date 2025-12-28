/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.networking.packet_handling;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.jetbrains.annotations.Nullable;
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

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener extends ClientCommonPacketListenerImpl {

    @Mutable
    @Shadow
    @Final
    private Set<PlayerInfo> listedPlayers;

    @Shadow
    public abstract void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket packet);

    @Shadow
    public abstract Connection getConnection();

    @Shadow
    private ClientLevel level;

    @Shadow
    @Nullable
    private LevelLoadTracker levelLoadTracker;

    @Unique
    private Packet<?> viaFabricPlus$teleportConfirmPacket;

    protected MixinClientPacketListener(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }

    @Redirect(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private double allowSmallValues(Vec3 instance, Vec3 vec) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return Integer.MAX_VALUE;
        } else {
            return instance.distanceTo(vec);
        }
    }

    @WrapWithCondition(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private boolean changePacketOrder(Connection instance, Packet<?> packet) {
        final boolean cancel = ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_21_2);
        if (cancel) {
            this.viaFabricPlus$teleportConfirmPacket = packet;
        }
        return !cancel;
    }

    @Inject(method = "handleMovePlayer", at = @At("RETURN"))
    private void changePacketOrder(ClientboundPlayerPositionPacket packet, CallbackInfo ci) {
        if (viaFabricPlus$teleportConfirmPacket != null) {
            this.connection.send(viaFabricPlus$teleportConfirmPacket);
            viaFabricPlus$teleportConfirmPacket = null;
        }
    }

    @WrapWithCondition(method = "handleMoveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/VecDeltaCodec;setBase(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private boolean dontHandleEntityPositionChange(VecDeltaCodec instance, Vec3 pos) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2);
    }

    @Redirect(method = "handleTeleportEntity", at = @At(value = "INVOKE", target = "Ljava/util/OptionalInt;isPresent()Z"))
    private boolean dontHandleRemovedVehiclePositionChange(OptionalInt instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_21) && instance.isPresent();
    }

    @Redirect(method = "handleOpenSignEditor", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private void openEmptySignEditor(Logger instance, String format, Object arg1, Object arg2, @Local(argsOnly = true) ClientboundOpenSignEditorPacket packet) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            final BlockPos pos = packet.getPos();

            final SignBlockEntity emptySignBlockEntity = new SignBlockEntity(pos, this.level.getBlockState(pos));
            emptySignBlockEntity.setLevel(this.level);
            this.minecraft.player.openTextEdit(emptySignBlockEntity, packet.isFrontText());
        } else {
            instance.warn(format, arg1, arg2);
        }
    }

    @WrapWithCondition(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;assignBaseValues(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"))
    private boolean dontApplyBaseValues(AttributeMap instance, AttributeMap other) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21);
    }

    @Redirect(method = "handleGameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 0))
    private void handleWinGameState0(Minecraft instance, Screen screen, @Local int i) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            if (i == 0) {
                this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(new LevelLoadingScreen(this.levelLoadTracker, LevelLoadingScreen.Reason.END_PORTAL));
            } else if (i == 1) {
                instance.setScreen(screen);
            }
        } else {
            instance.setScreen(screen);
        }
    }

    @WrapWithCondition(method = "handleConfigurationStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChatAcknowledgement()V"))
    private boolean dontSendChatAck(ClientPacketListener instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5);
    }

    @Redirect(method = "handleOpenBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/BookViewScreen$BookAccess;fromItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/gui/screens/inventory/BookViewScreen$BookAccess;"))
    private BookViewScreen.BookAccess dontOpenWriteableBookScreen(ItemStack stack) {
        if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_5) || stack.is(Items.WRITTEN_BOOK)) {
            return BookViewScreen.BookAccess.fromItem(stack);
        } else {
            return null;
        }
    }

    @WrapWithCondition(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;startWaitingForNewLevel(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/gui/screens/LevelLoadingScreen$Reason;)V"))
    private boolean checkDimensionChange(ClientPacketListener instance, LocalPlayer player, ClientLevel world, LevelLoadingScreen.Reason worldEntryReason, @Local(ordinal = 0) ResourceKey<Level> registryKey) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_3) || registryKey != this.minecraft.player.level().dimension();
    }

    @WrapWithCondition(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeChatPacketError(Logger instance, String s, Object o) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20_2);
    }

    @Redirect(method = "applyPlayerInfoUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;onGameModeChanged(Lnet/minecraft/world/level/GameType;)V"))
    private void dontResetVelocity(LocalPlayer instance, GameType gameMode) {
        if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_20)) {
            instance.onGameModeChanged(gameMode);
        }
    }

    @WrapWithCondition(method = "initializeChatSession", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean removeInvalidSignatureWarning(Logger instance, String s, Object o) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_4);
    }

    @WrapWithCondition(method = "handlePlayerInfoUpdate", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private boolean removeUnknownPlayerListEntryWarning(Logger instance, String s, Object object1, Object object2) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_3);
    }

    @Redirect(method = {"handleEntityPositionSync", "handleMoveEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isLocalInstanceAuthoritative()Z"))
    private boolean allowPlayerToBeMovedByEntityPackets(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_3) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return instance.getControllingPassenger() instanceof Player player ? player.isLocalPlayer() : !instance.level().isClientSide();
        } else {
            return instance.isLocalInstanceAuthoritative();
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fixPlayerListOrdering(Minecraft client, Connection clientConnection, CommonListenerCookie clientConnectionState, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            this.listedPlayers = new LinkedHashSet<>();
        }
    }

    @ModifyConstant(method = "handleSetEntityPassengersPacket", constant = @Constant(classValue = AbstractBoat.class))
    private boolean dontChangeYawWhenMountingBoats(Object entity, Class<?> boatClass) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18)) {
            return false;
        } else {
            return boatClass.isInstance(entity);
        }
    }

    @Inject(method = "handleSetChunkCacheRadius", at = @At("RETURN"))
    private void emulateSimulationDistance(ClientboundSetChunkCacheRadiusPacket packet, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_17_1)) {
            this.handleSetSimulationDistance(new ClientboundSetSimulationDistancePacket(packet.getRadius()));
        }
    }

    @Redirect(method = "setValuesFromPositionPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;FF)V"))
    private static void cancelSmallChanges(Entity instance, Vec3 pos, float yaw, float pitch) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_1) && Math.abs(instance.getX() - pos.x) < 0.03125 && Math.abs(instance.getY() - pos.y) < 0.015625 && Math.abs(instance.getZ() - pos.z) < 0.03125) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && instance.getInterpolation() != null) {
                instance.getInterpolation().setInterpolationLength(0);
            }
            instance.moveOrInterpolateTo(instance.position(), yaw, pitch);
        } else {
            instance.moveOrInterpolateTo(pos, yaw, pitch);
        }
    }

}
