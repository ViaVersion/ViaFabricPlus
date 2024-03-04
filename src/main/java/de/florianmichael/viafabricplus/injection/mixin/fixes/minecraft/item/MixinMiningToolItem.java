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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MiningToolItem.class)
public abstract class MixinMiningToolItem extends ToolItem {

    @Shadow
    @Final
    private float attackDamage;

    @Shadow
    @Final
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    @Unique
    private float viaFabricPlus$attackDamage_r1_8;

    @Unique
    private Multimap<EntityAttribute, EntityAttributeModifier> viaFabricPlus$AttributeModifiers_r1_8;

    public MixinMiningToolItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init1_8Fields(float attackDamage, float attackSpeed, ToolMaterial material, TagKey effectiveBlocks, Settings settings, CallbackInfo ci) {
        final float materialAttackDamage = material.getAttackDamage();
        if ((Item) this instanceof PickaxeItem) {
            this.viaFabricPlus$attackDamage_r1_8 = 2 + materialAttackDamage;
        } else if ((Item) this instanceof ShovelItem) {
            this.viaFabricPlus$attackDamage_r1_8 = 1 + materialAttackDamage;
        } else if ((Item) this instanceof AxeItem) {
            this.viaFabricPlus$attackDamage_r1_8 = 3 + materialAttackDamage;
        } else { // HoeItem didn't use MiningToolItem abstraction in 1.8
            this.viaFabricPlus$AttributeModifiers_r1_8 = ImmutableMultimap.of();
            return;
        }

        final ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", this.viaFabricPlus$attackDamage_r1_8, EntityAttributeModifier.Operation.ADDITION));
        this.viaFabricPlus$AttributeModifiers_r1_8 = builder.build();
    }

    @Redirect(method = "getAttackDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/item/MiningToolItem;attackDamage:F"))
    private float changeAttackDamage(MiningToolItem instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$attackDamage_r1_8;
        } else {
            return this.attackDamage;
        }
    }

    @Redirect(method = "getAttributeModifiers", at = @At(value = "FIELD", target = "Lnet/minecraft/item/MiningToolItem;attributeModifiers:Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> changeAttributeModifiers(MiningToolItem instance) {
        if (DebugSettings.global().replaceAttributeModifiers.isEnabled()) {
            return this.viaFabricPlus$AttributeModifiers_r1_8;
        } else {
            return this.attributeModifiers;
        }
    }

}
