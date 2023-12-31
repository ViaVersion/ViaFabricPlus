/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.UntypedPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.util.function.BooleanSupplier;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = ClientCommonNetworkHandler.class, priority = 1 /* Has to be applied before Fabric's Networking API, so it doesn't cancel our custom-payload packets */)
public abstract class MixinClientCommonNetworkHandler {

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    protected abstract void send(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, Duration expiry);

    @Shadow
    public abstract void sendPacket(Packet<?> packet);

    @Shadow
    @Final
    protected ClientConnection connection;

    @Redirect(method = "onKeepAlive", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientCommonNetworkHandler;send(Lnet/minecraft/network/packet/Packet;Ljava/util/function/BooleanSupplier;Ljava/time/Duration;)V"))
    private void forceSendKeepAlive(ClientCommonNetworkHandler instance, Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, Duration expiry) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            sendPacket(packet);
            return;
        }

        send(packet, sendCondition, expiry);
    }

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(CommonPingS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5)) {
            final InventoryAcknowledgements acks = ((IClientConnection) this.connection).viaFabricPlus$getUserConnection().get(InventoryAcknowledgements.class);
            if (acks.removeId(packet.getParameter())) {
                final short inventoryId = (short) ((packet.getParameter() >> 16) & 0xFF);

                ScreenHandler handler = null;
                if (inventoryId == 0) handler = client.player.playerScreenHandler;
                else if (inventoryId == client.player.currentScreenHandler.syncId) handler = client.player.currentScreenHandler;

                if (handler != null) {
                    acks.addId(packet.getParameter());
                } else {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "onCustomPayload(Lnet/minecraft/network/packet/s2c/common/CustomPayloadS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void handleSyncTask(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet.payload().id().toString().equals(ClientsideFixes.PACKET_SYNC_IDENTIFIER) && packet.payload() instanceof ResolvablePayload payload) {
            ClientsideFixes.handleSyncTask(((UntypedPayload) payload.resolve(null)).buffer());
            ci.cancel(); // Cancel the packet, so it doesn't get processed by the client
        }
    }

}
