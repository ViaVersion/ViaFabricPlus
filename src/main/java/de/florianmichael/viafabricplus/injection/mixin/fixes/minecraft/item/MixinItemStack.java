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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.raphimc.vialoader.util.VersionEnum;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalDouble;

@Mixin(value = ItemStack.class, priority = 1)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Shadow @Final public static ItemStack EMPTY;

    @Shadow private int count;

    @Shadow @Final @Deprecated private @Nullable Item item;

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    public void dontRecalculateState(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance() != null && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_10)) {
            final ItemStack self = (ItemStack) (Object) this;

            cir.setReturnValue(self == EMPTY || this.item == null || this.item == Items.AIR || count == 0);
        }
    }

    @Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    private void modifyMiningSpeedMultiplier(BlockState state, CallbackInfoReturnable<Float> ci) {
        final Item toolItem = ((ItemStack) (Object) this).getItem();

        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2) && toolItem instanceof HoeItem) {
            ci.setReturnValue(1F);
        }
    }

    @Redirect(method = "getTooltip",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributes;GENERIC_ATTACK_DAMAGE:Lnet/minecraft/entity/attribute/EntityAttribute;", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", ordinal = 0))
    private double redirectGetTooltip(PlayerEntity player, EntityAttribute attribute) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return 0;
        } else {
            return player.getAttributeBaseValue(attribute);
        }
    }

    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @ModifyVariable(method = "getAttributeModifiers", ordinal = 0, at = @At(value = "STORE", ordinal = 1))
    private Multimap<EntityAttribute, EntityAttributeModifier> modifyVariableGetAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> modifiers) {
        if (!DebugSettings.INSTANCE.replaceAttributeModifiers.isEnabled() || modifiers.isEmpty()) return modifiers;

        modifiers = HashMultimap.create(modifiers);
        modifiers.removeAll(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        OptionalDouble defaultAttackDamage = viaFabricPlus$getDefaultAttackDamage(getItem());
        if (defaultAttackDamage.isPresent()) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(Item.ATTACK_DAMAGE_MODIFIER_ID, "Weapon Modifier", defaultAttackDamage.getAsDouble(), EntityAttributeModifier.Operation.ADDITION));
        }
        modifiers.removeAll(EntityAttributes.GENERIC_ATTACK_SPEED);
        modifiers.removeAll(EntityAttributes.GENERIC_ARMOR);
        modifiers.removeAll(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        return modifiers;
    }

    @Unique
    private OptionalDouble viaFabricPlus$getDefaultAttackDamage(Item item) {
        if (item instanceof ToolItem) {
            ToolMaterial material = ((ToolItem) item).getMaterial();
            int materialBonus;
            if (material == ToolMaterials.STONE) {
                materialBonus = 1;
            } else if (material == ToolMaterials.IRON) {
                materialBonus = 2;
            } else if (material == ToolMaterials.DIAMOND) {
                materialBonus = 3;
            } else {
                materialBonus = 0;
            }
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
}
