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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.AbstractList;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {

    @Redirect(method = "<init>", slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=1")), at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;ofSize(ILjava/lang/Object;)Lnet/minecraft/util/collection/DefaultedList;", ordinal = 0))
    private <T> DefaultedList<T> redirectOffhandInventory(int size, T def) {
        if (ProtocolHack.getTargetVersion().olderThanOrEquals(ProtocolVersion.v1_8)) {
            //noinspection MixinInnerClass
            return new DefaultedList<>(new AbstractList<T>() {
                @Override
                public T get(int index) {
                    return def;
                }

                @Override
                public T set(int index, T element) {
                    return def;
                }

                @Override
                public int size() {
                    return 0;
                }
            }, def) {
            };
        } else {
            return DefaultedList.ofSize(size, def);
        }
    }

}
