/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 * <p>
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 * <p>
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Mutable
    @Shadow @Final private Set<PlayerListEntry> listedPlayerListEntries;

    @Shadow public abstract void onSimulationDistance(SimulationDistanceS2CPacket packet);

    @Shadow public abstract void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet);

    @Shadow private ClientWorld world;

    @Shadow @Nullable public abstract PlayerListEntry getPlayerListEntry(UUID uuid);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixPlayerListOrdering(MinecraftClient client, Screen screen, ClientConnection connection, ServerInfo serverInfo, GameProfile profile, WorldSession worldSession, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            this.listedPlayerListEntries = new LinkedHashSet<>();
        }
    }

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
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
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_17_1)) {
            this.onSimulationDistance(new SimulationDistanceS2CPacket(packet.getDistance()));
        }
    }

    @Inject(method = "onEntitySpawn", at = @At("TAIL"))
    public void forceEntityVelocity(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            if (packet.getEntityType() == EntityType.ITEM ||
            packet.getEntityType() == EntityType.ARROW || packet.getEntityType() ==
            EntityType.SPECTRAL_ARROW || packet.getEntityType() == EntityType.TRIDENT) {
                onEntityVelocityUpdate(new EntityVelocityUpdateS2CPacket(
                        packet.getId(), new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ())
                ));
            }
        }
    }

    @Inject(method = { "onGameJoin", "onPlayerRespawn" }, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }

    @Redirect(method = "onPlayerSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;setReady()V"))
    public void moveDownloadingTerrainClosing(DownloadingTerrainScreen instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19)) {
            instance.setReady();
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    public void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18_2) && MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = BoatEntity.class))
    public Class<?> dontChangePlayerYaw(Object entity, Class<?> constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18_2)) {
            return Integer.class;
        }
        return constant;
    }

    @Redirect(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public void removeNewWarning(Logger instance, String s, Object o) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19_3)) {
            instance.warn(s, o);
        }
    }
}
