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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.item.ArmorMaterials;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorMaterials.class)
public abstract class MixinArmorMaterials {

    @Shadow
    @Final
    private int durabilityMultiplier;

    @Redirect(method = "getDurability", at = @At(value = "FIELD", target = "Lnet/minecraft/item/ArmorMaterials;durabilityMultiplier:I"))
    private int changeDurabilityMultiplier(ArmorMaterials instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            if (instance == ArmorMaterials.LEATHER) {
                return 3;
            } else if (instance == ArmorMaterials.CHAIN || instance == ArmorMaterials.GOLD) {
                return 6;
            } else if (instance == ArmorMaterials.IRON) {
                return 12;
            } else if (instance == ArmorMaterials.DIAMOND) {
                return 24;
            }
        }

        return this.durabilityMultiplier;
    }

}
