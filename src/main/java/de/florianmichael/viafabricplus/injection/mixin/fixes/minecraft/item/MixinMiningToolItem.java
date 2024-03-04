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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MiningToolItem.class)
public abstract class MixinMiningToolItem extends ToolItem {

    @Shadow @Final private float attackDamage;

    public MixinMiningToolItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    /**
     * @author FlorianMichael/EnZaXD
     * @reason Change attack damage calculation
     */
    @Overwrite
    public float getAttackDamage() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            final float materialDamage = getMaterial().getAttackDamage();
            if ((Item) this instanceof PickaxeItem) {
                return 2 + materialDamage;
            } else if ((Item) this instanceof ShovelItem) {
                return 1 + materialDamage;
            } else if ((Item) this instanceof AxeItem) {
                return 3 + materialDamage;
            }
        }
        return this.attackDamage;
    }

}
