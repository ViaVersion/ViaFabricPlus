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

package com.viaversion.viafabricplus.injection.mixin.base.integration;

import com.viaversion.viafabricplus.base.VFPDebugHudEntry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHudEntries.class)
public abstract class MixinDebugHudEntries {

    @Mutable
    @Shadow
    @Final
    public static Map<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> PROFILES;

    @Shadow
    private static Identifier register(final Identifier id, final DebugHudEntry entry) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addViaFabricPlusEntry(CallbackInfo ci) {
        final Identifier entryId = register(VFPDebugHudEntry.ID, new VFPDebugHudEntry());
        final Map<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> profiles = new HashMap<>();
        for (Map.Entry<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> entry : PROFILES.entrySet()) {
            final Map<Identifier, DebugHudEntryVisibility> entries = new HashMap<>(entry.getValue());
            if (entry.getKey() == DebugProfileType.DEFAULT) {
                entries.put(entryId, DebugHudEntryVisibility.IN_F3);
            }
            profiles.put(entry.getKey(), entries);
        }
        PROFILES = profiles;
    }

}
