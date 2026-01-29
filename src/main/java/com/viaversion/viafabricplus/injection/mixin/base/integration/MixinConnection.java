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

package com.viaversion.viafabricplus.injection.mixin.base.integration;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import java.net.ConnectException;
import java.net.SocketException;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MixinConnection extends SimpleChannelInboundHandler<Packet<?>> {

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void printNetworkingErrors(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (DebugSettings.INSTANCE.printNetworkingErrorsToLogs.getValue()) {
            if (ex instanceof SocketException || ex instanceof ConnectException) {
                // Thrown when server is not reachable
                return;
            }
            ViaFabricPlusImpl.INSTANCE.getLogger().error("An exception occurred while handling a packet", ex);
        }
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        // Krypton mixins are badly implemented and override parts of the network pipeline. They rejected
        // to make the mixins mod compatible and instead added this bad API which mods now have to use for no reason.
        if (evt.getClass().getName().equals("me.steinborn.krypton.mod.shared.misc.KryptonPipelineEvent")) {
            if (evt.toString().equals("COMPRESSION_ENABLED")) {
                ViaChannelInitializer.reorderPipeline(ctx.pipeline(), HandlerNames.ENCODER, HandlerNames.DECODER);
                ViaFabricPlusImpl.INSTANCE.getLogger().warn("ViaFabricPlus has detected that the Krypton mod is installed. Please note that Krypton is mostly snake oil on the client side, and it is not recommended to use it.");
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}
