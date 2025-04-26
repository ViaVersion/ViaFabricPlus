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

package com.viaversion.viafabricplus.injection.mixin.features.networking.disable_server_pinging;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinMultiplayerServerListWidget_ServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Mutable
    @Shadow
    @Final
    private WorldIcon icon;

    @Unique
    private boolean viaFabricPlus$disableServerPinging = false;

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"))
    private boolean disableServerPinging(ThreadPoolExecutor instance, Runnable runnable) {
        ProtocolVersion version = ((IServerInfo) server).viaFabricPlus$forcedVersion();
        if (version == null) {
            version = ProtocolTranslator.getTargetVersion();
        }

        viaFabricPlus$disableServerPinging = DebugSettings.INSTANCE.disableServerPinging.isEnabled(version);
        if (viaFabricPlus$disableServerPinging) {
            this.server.version = Text.of(version.getName()); // Show target version
        }
        return !viaFabricPlus$disableServerPinging;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ServerInfo$Status;INCOMPATIBLE:Lnet/minecraft/client/network/ServerInfo$Status;"))
    private ServerInfo.Status disableServerPinging() {
        if (viaFabricPlus$disableServerPinging) {
            return this.server.getStatus(); // server version will always be shown (as we don't have a player count anyway)
        } else {
            return ServerInfo.Status.INCOMPATIBLE;
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;"))
    private List<OrderedText> disableServerPinging(TextRenderer instance, StringVisitable text, int width) {
        if (viaFabricPlus$disableServerPinging) { // server label will just show the server address
            return instance.wrapLines(Text.of(server.address), width);
        } else {
            return instance.wrapLines(text, width);
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"), index = 2)
    private int disableServerPinging(int x) {
        if (viaFabricPlus$disableServerPinging) { // Move server label to the right (as we remove the ping bar)
            x += 15 /* ping bar width */ - 3 /* magical offset */;
        }
        return x;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
    private boolean disableServerPinging(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        return !viaFabricPlus$disableServerPinging; // Remove ping bar
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Ljava/util/List;)V"))
    private boolean disableServerPinging(MultiplayerScreen instance, List<Text> tooltip) {
        return !viaFabricPlus$disableServerPinging; // Remove player list tooltip
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/WorldIcon;getTextureId()Lnet/minecraft/util/Identifier;"))
    private Identifier disableServerPinging(WorldIcon instance) {
        if (viaFabricPlus$disableServerPinging) { // Remove server icon
            return WorldIcon.UNKNOWN_SERVER_ID;
        } else {
            return this.icon.getTextureId();
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Lnet/minecraft/text/Text;)V"))
    private boolean disableServerPinging(MultiplayerScreen instance, Text text) {
        return !viaFabricPlus$disableServerPinging; // Remove ping bar tooltip
    }

}
