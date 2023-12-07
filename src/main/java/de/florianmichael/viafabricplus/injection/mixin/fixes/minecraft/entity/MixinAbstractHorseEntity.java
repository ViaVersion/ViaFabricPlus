/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractHorseEntity.class)
public abstract class MixinAbstractHorseEntity {

    @Shadow protected abstract boolean receiveFood(PlayerEntity player, ItemStack item);

    @Redirect(method = "interactHorse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;receiveFood(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Z"))
    public boolean dontRemoveItemOnClientside(AbstractHorseEntity instance, PlayerEntity player, ItemStack item) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20_2)) {
            return false;
        }
        return receiveFood(player, item);
    }

}
