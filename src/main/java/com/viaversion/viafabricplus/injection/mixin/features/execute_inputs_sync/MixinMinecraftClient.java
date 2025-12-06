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

package com.viaversion.viafabricplus.injection.mixin.features.execute_inputs_sync;

import com.viaversion.viafabricplus.injection.access.execute_inputs_sync.IMouseKeyboard;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import java.util.Queue;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    public MouseHandler mouseHandler;

    @Shadow
    @Final
    public KeyboardHandler keyboardHandler;

    @Inject(method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0),
        slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 0),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillCrashDetails(Lnet/minecraft/CrashReport;)V")
        )
    )
    private void processInputQueues(CallbackInfo ci) {
        if (DebugSettings.INSTANCE.executeInputsSynchronously.isEnabled()) {
            Queue<Runnable> inputEvents = ((IMouseKeyboard) this.mouseHandler).viaFabricPlus$getPendingScreenEvents();
            while (!inputEvents.isEmpty()) inputEvents.poll().run();

            inputEvents = ((IMouseKeyboard) this.keyboardHandler).viaFabricPlus$getPendingScreenEvents();
            while (!inputEvents.isEmpty()) inputEvents.poll().run();
        }
    }

}
