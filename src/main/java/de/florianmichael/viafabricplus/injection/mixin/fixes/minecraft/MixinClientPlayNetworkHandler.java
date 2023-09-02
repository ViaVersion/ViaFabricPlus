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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.base.settings.groups.VisualSettings;
import de.florianmichael.viafabricplus.definition.ClientsideFixes;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.ScreenHandler;
import net.raphimc.vialoader.util.VersionEnum;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Mutable
    @Shadow
    @Final
    private Set<PlayerListEntry> listedPlayerListEntries;

    @Shadow
    public abstract void onSimulationDistance(SimulationDistanceS2CPacket packet);

    @Shadow
    protected abstract void sendPacket(Packet<ServerPlayPacketListener> packet, BooleanSupplier sendCondition, Duration expirationTime);

    @Shadow
    public abstract void sendPacket(Packet<?> packet);

    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    private ClientWorld world;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixPlayerListOrdering(MinecraftClient client, Screen screen, ClientConnection connection, ServerInfo serverInfo, GameProfile profile, WorldSession worldSession, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            this.listedPlayerListEntries = new LinkedHashSet<>();
        }
    }

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_17)) {
            return;
        }

        final int inventoryId = (packet.getParameter() >> 16) & 0xFF; // Fix Via Bug from 1.16.5 (Window Confirmation -> PlayPing) Usage for MiningFast Detection
        ScreenHandler handler = null;

        if (client.player == null) return;

        if (inventoryId == 0) handler = client.player.playerScreenHandler;
        if (inventoryId == client.player.currentScreenHandler.syncId) handler = client.player.currentScreenHandler;

        if (handler == null) ci.cancel();
    }

    @Inject(method = "onChunkLoadDistance", at = @At("RETURN"))
    public void emulateSimulationDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_17_1)) {
            this.onSimulationDistance(new SimulationDistanceS2CPacket(packet.getDistance()));
        }
    }

    @Inject(method = {"onGameJoin", "onPlayerRespawn"}, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }

    @WrapWithCondition(method = "onPlayerSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;setReady()V"))
    public boolean moveDownloadingTerrainClosing(DownloadingTerrainScreen instance) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19);
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    public void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2) && MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = BoatEntity.class))
    public Class<?> dontChangePlayerYaw(Object entity, Class<?> constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2)) {
            return Integer.class;
        }
        return constant;
    }

    @WrapWithCondition(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public boolean removeWarning(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_3);
    }

    @Redirect(method = "onKeepAlive", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;Ljava/util/function/BooleanSupplier;Ljava/time/Duration;)V"))
    public void forceSendKeepAlive(ClientPlayNetworkHandler instance, Packet<ServerPlayPacketListener> packet, BooleanSupplier sendCondition, Duration expirationTime) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            sendPacket(packet);
            return;
        }
        sendPacket(packet, sendCondition, expirationTime);
    }

    @Redirect(method = "onServerMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;isSecureChatEnforced()Z"))
    public boolean removeSecureChatWarning(ServerMetadataS2CPacket instance) {
        return instance.isSecureChatEnforced() || VisualSettings.INSTANCE.disableSecureChatWarning.isEnabled();
    }

    @Inject(method = "onSetTradeOffers", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    public void checkLoginPacket(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion(connection.channel).isOlderThanOrEqualTo(VersionEnum.r1_13_2) && this.client.player == null) {
            ViaFabricPlus.LOGGER.error("Server tried to send Play packet in Login process, dropping \"SetTradeOffers\"");
            ci.cancel();
        }
    }

    @Inject(method = "onCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    public void handlePseudoPackets(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        final var channel = packet.getChannel().toString();
        final var data = packet.getData();

        if (channel.equals(ClientsideFixes.PACKET_SYNC_IDENTIFIER)) {
            final var uuid = data.readString();

            if (ClientsideFixes.hasSyncTask(uuid)) {
                ClientsideFixes.getSyncTask(uuid).accept(data);
                ci.cancel();
            }
        }
    }
}
