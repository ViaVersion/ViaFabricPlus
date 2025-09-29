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

package com.viaversion.viafabricplus.injection.mixin.features.networking.remove_signed_commands;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.time.Instant;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonNetworkHandler {
    @Shadow
    private LastSeenMessagesCollector lastSeenMessagesCollector;

    protected MixinClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @WrapWithCondition(method = "runClickEventCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;openConfirmRunCommandScreen(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/gui/screen/Screen;)V"))
    private boolean dontOpenConfirmationScreens(ClientPlayNetworkHandler instance, String command, String message, Screen screenAfterRun) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_21_5);
    }

    @Redirect(method = "sendChatCommand", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean alwaysSignCommands(List<?> instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20_3) && instance.isEmpty();
    }

    @Redirect(method = "runClickEventCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void alwaysSignCommands(ClientPlayNetworkHandler instance, Packet<?> packet, @Local(argsOnly = true) String command) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            this.sendPacket(new ChatCommandSignedC2SPacket(command, Instant.now(), 0L, ArgumentSignatureDataMap.EMPTY, this.lastSeenMessagesCollector.collect().update()));
        } else {
            instance.sendPacket(packet);
        }
    }
}
