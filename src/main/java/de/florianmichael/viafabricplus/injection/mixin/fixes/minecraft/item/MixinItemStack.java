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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.injection.access.IItemStack;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalDouble;

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
        if (ProtocolHack.getTargetVersion().olderThanOrEquals(ProtocolVersion.v1_8)) {
            return 0;
        } else {
            return player.getAttributeBaseValue(attribute);
        }
    }

    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @ModifyVariable(method = "getAttributeModifiers", ordinal = 0, at = @At(value = "STORE", ordinal = 1))
    private Multimap<EntityAttribute, EntityAttributeModifier> modifyVariableGetAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
        if (!DebugSettings.global().replaceAttributeModifiers.isEnabled() || modifiers.isEmpty()) {
            return modifiers;
        }

        modifiers = HashMultimap.create(modifiers);
        modifiers.removeAll(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        OptionalDouble defaultAttackDamage = viaFabricPlus$getDefaultAttackDamage(getItem());
        if (defaultAttackDamage.isPresent()) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(Item.ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", defaultAttackDamage.getAsDouble(), EntityAttributeModifier.Operation.ADDITION));
        }
        modifiers.removeAll(EntityAttributes.GENERIC_ATTACK_SPEED);
        modifiers.removeAll(EntityAttributes.GENERIC_ARMOR);
        modifiers.removeAll(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        return modifiers;
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void copyViaFabricPlusData(CallbackInfoReturnable<ItemStack> cir) {
        final IItemStack mixinItemStack = (IItemStack) (Object) cir.getReturnValue();
        if (this.viaFabricPlus$has1_10Tag) {
            mixinItemStack.viaFabricPlus$set1_10Count(this.viaFabricPlus$1_10Count);
        }
    }

    @Unique
    private OptionalDouble viaFabricPlus$getDefaultAttackDamage(Item item) {
        if (item instanceof ToolItem toolItem) {
            final float materialBonus = toolItem.getMaterial().getAttackDamage();
            if (item instanceof SwordItem) {
                return OptionalDouble.of(4 + materialBonus);
            } else if (item instanceof PickaxeItem) {
                return OptionalDouble.of(2 + materialBonus);
            } else if (item instanceof ShovelItem) {
                return OptionalDouble.of(1 + materialBonus);
            } else if (item instanceof AxeItem) {
                return OptionalDouble.of(3 + materialBonus);
            }
        }
        return OptionalDouble.empty();
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
