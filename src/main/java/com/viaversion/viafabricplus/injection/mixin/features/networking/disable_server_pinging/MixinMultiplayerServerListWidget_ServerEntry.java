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
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.viaversion.viafabricplus.injection.access.base.IServerData;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class MixinMultiplayerServerListWidget_ServerEntry {

    @Shadow
    @Final
    private ServerData serverData;

    @Mutable
    @Shadow
    @Final
    private FaviconTexture icon;

    @Unique
    private boolean viaFabricPlus$disableServerPinging = false;

    @WrapWithCondition(method = "renderContent", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"))
    private boolean disableServerPinging(ThreadPoolExecutor instance, Runnable runnable) {
        ProtocolVersion version = ((IServerData) serverData).viaFabricPlus$forcedVersion();
        if (version == null) {
            version = ProtocolTranslator.getTargetVersion();
        }

        viaFabricPlus$disableServerPinging = DebugSettings.INSTANCE.disableServerPinging.isEnabled(version);
        if (viaFabricPlus$disableServerPinging) {
            this.serverData.version = Component.nullToEmpty(version.getName()); // Show target version
        }
        return !viaFabricPlus$disableServerPinging;
    }

    @Redirect(method = "renderContent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ServerData$State;INCOMPATIBLE:Lnet/minecraft/client/multiplayer/ServerData$State;"))
    private ServerData.State disableServerPinging() {
        if (viaFabricPlus$disableServerPinging) {
            return this.serverData.state(); // server version will always be shown (as we don't have a player count anyway)
        } else {
            return ServerData.State.INCOMPATIBLE;
        }
    }

    @Redirect(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;"))
    private List<FormattedCharSequence> disableServerPinging(Font instance, FormattedText text, int width) {
        if (viaFabricPlus$disableServerPinging) { // server label will just show the server address
            return instance.split(Component.nullToEmpty(serverData.ip), width);
        } else {
            return instance.split(text, width);
        }
    }

    @ModifyArg(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"), index = 2)
    private int disableServerPinging(int x) {
        if (viaFabricPlus$disableServerPinging) { // Move server label to the right (as we remove the ping bar)
            x += 15 /* ping bar width */ - 3 /* magical offset */;
        }
        return x;
    }

    @WrapWithCondition(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private boolean disableServerPinging(GuiGraphics instance, RenderPipeline pipeline, ResourceLocation sprite, int x, int y, int width, int height) {
        return !viaFabricPlus$disableServerPinging; // Remove ping bar
    }

    @WrapWithCondition(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Ljava/util/List;II)V"))
    private boolean disableServerPinging(GuiGraphics instance, List<FormattedCharSequence> text, int x, int y) {
        return !viaFabricPlus$disableServerPinging; // Remove player list tooltip
    }

    @Redirect(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/FaviconTexture;textureLocation()Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation disableServerPinging(FaviconTexture instance) {
        if (viaFabricPlus$disableServerPinging) { // Remove server icon
            return FaviconTexture.MISSING_LOCATION;
        } else {
            return this.icon.textureLocation();
        }
    }

    @WrapWithCondition(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V"))
    private boolean disableServerPinging(GuiGraphics instance, Component text, int x, int y) {
        return !viaFabricPlus$disableServerPinging; // Remove ping bar tooltip
    }

}
