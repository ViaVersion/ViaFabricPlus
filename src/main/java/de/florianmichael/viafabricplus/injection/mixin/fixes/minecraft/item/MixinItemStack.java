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
import de.florianmichael.viafabricplus.injection.access.IItemStack;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 1)
public abstract class MixinItemStack implements IItemStack {

    @Shadow
    public abstract Item getItem();

    @Unique
    private boolean viaFabricPlus$has1_10Tag;

    @Unique
    private int viaFabricPlus$1_10Count;

    @Redirect(method = "getTooltip",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributes;GENERIC_ATTACK_DAMAGE:Lnet/minecraft/entity/attribute/EntityAttribute;", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", ordinal = 0))
    private double fixDamageCalculation(PlayerEntity player, EntityAttribute attribute) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0;
        } else {
            return player.getAttributeBaseValue(attribute);
        }
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void copyViaFabricPlusData(CallbackInfoReturnable<ItemStack> cir) {
        final IItemStack mixinItemStack = (IItemStack) (Object) cir.getReturnValue();
        if (this.viaFabricPlus$has1_10Tag) {
            mixinItemStack.viaFabricPlus$set1_10Count(this.viaFabricPlus$1_10Count);
        }
    }

    @Override
    public boolean viaFabricPlus$has1_10Tag() {
        return this.viaFabricPlus$has1_10Tag;
    }

    @Override
    public int viaFabricPlus$get1_10Count() {
        return this.viaFabricPlus$1_10Count;
    }

    @Override
    public void viaFabricPlus$set1_10Count(final int count) {
        this.viaFabricPlus$has1_10Tag = true;
        this.viaFabricPlus$1_10Count = count;
    }

}
