/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.injection.mixin.compat.fabricapi;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.fixes.versioned.visual.FootStepParticle1_12_2;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistrySyncManager.class)
public abstract class MixinRegistrySyncManager {

    @Redirect(method = "createAndPopulateRegistryMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;getId(Ljava/lang/Object;)Lnet/minecraft/util/Identifier;"))
    private static @Nullable <T> Identifier skipFootStepParticle(Registry<T> instance, T t) {
        final Identifier id = instance.getId(t);
        if (id == FootStepParticle1_12_2.ID) {
            return null;
        } else {
            return id;
        }
    }

    @Inject(method = "checkRemoteRemap", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V", ordinal = 0), cancellable = true, remap = false)
    private static void ignoreFabricSyncErrors(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfo ci) {
        if (DebugSettings.global().ignoreFabricSyncErrors.getValue()) {
            ViaFabricPlus.global().getLogger().warn("Ignoring missing registries from Fabric API");
            ci.cancel();
        }
    }

}
