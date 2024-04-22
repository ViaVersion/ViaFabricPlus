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
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.UseAction;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public abstract class MixinSwordItem extends ToolItem {

    public MixinSwordItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    /*@Inject(method = "<init>", at = @At("RETURN"))
    private void init1_8Fields(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, CallbackInfo ci) {
        this.viaFabricPlus$attackDamage_r1_8 = 4 + toolMaterial.getAttackDamage();
        final ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.viaFabricPlus$attackDamage_r1_8, EntityAttributeModifier.Operation.ADDITION));
        this.viaFabricPlus$AttributeModifiers_r1_8 = builder.build();
    }*/

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8)) {
            return UseAction.BLOCK;
        } else {
            return super.getUseAction(stack);
        }
    }

    /*@Redirect(method = "getAttributeModifiers", at = @At(value = "FIELD", target = "Lnet/minecraft/item/SwordItem;attributeModifiers:Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> changeAttributeModifiers(SwordItem instance) {
        if (DebugSettings.global().replaceAttributeModifiers.isEnabled()) {
            return this.viaFabricPlus$AttributeModifiers_r1_8;
        } else {
            return this.attributeModifiers;
        }
    }*/

}
