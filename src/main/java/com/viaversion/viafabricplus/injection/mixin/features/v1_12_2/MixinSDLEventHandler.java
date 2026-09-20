/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.SDLEventHandler;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.injection.access.v1_12_2.ISDLEventHandler;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SDLEventHandler.class)
public abstract class MixinSDLEventHandler implements ISDLEventHandler {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private static long getWindowHandle(final SDL_Event event) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    private final Queue<Runnable> viaFabricPlus$pendingScreenEvents = new ConcurrentLinkedQueue<>();

    @Redirect(method = {"handleTextInputEvent", "handleMouseButtonEvent", "handleMouseWheelEvent"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;execute(Ljava/lang/Runnable;)V"))
    private void storeEvent(Minecraft instance, Runnable runnable) {
        if (this.viaFabricPlus$shouldStoreEvent()) {
            this.viaFabricPlus$pendingScreenEvents.offer(runnable);
        } else {
            instance.execute(runnable);
        }
    }

    @Redirect(method = "handleKeyEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;execute(Ljava/lang/Runnable;)V"))
    private void storeKeyEvent(Minecraft instance, Runnable runnable, @Local(argsOnly = true) SDL_Event event, @Local int action, @Local KeyEvent key) {
        if (this.viaFabricPlus$shouldStoreEvent()) {
            final long handle = getWindowHandle(event);
            this.viaFabricPlus$pendingScreenEvents.offer(() -> this.minecraft.keyboardHandler.keyPress(handle, action, key));
        } else {
            instance.execute(runnable);
        }
    }

    @Unique
    private boolean viaFabricPlus$shouldStoreEvent() {
        return this.minecraft.getConnection() != null && this.minecraft.gui.screen() != null && ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2);
    }

    @Override
    public Queue<Runnable> viaFabricPlus$getPendingScreenEvents() {
        return this.viaFabricPlus$pendingScreenEvents;
    }

}
