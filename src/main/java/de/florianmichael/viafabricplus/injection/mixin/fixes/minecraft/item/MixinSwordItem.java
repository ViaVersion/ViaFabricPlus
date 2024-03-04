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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class MixinSwordItem extends ToolItem {

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

    public MixinSwordItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init1_8Fields(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, CallbackInfo ci) {
        this.viaFabricPlus$attackDamage_r1_8 = 4 + toolMaterial.getAttackDamage();
        final ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.viaFabricPlus$attackDamage_r1_8, EntityAttributeModifier.Operation.ADDITION));
        this.viaFabricPlus$AttributeModifiers_r1_8 = builder.build();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8)) {
            ItemStack itemStack = user.getStackInHand(hand);
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        } else {
            return super.use(world, user, hand);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8)) {
            return UseAction.BLOCK;
        } else {
            return super.getUseAction(stack);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8)) {
            return 72000;
        } else {
            return super.getMaxUseTime(stack);
        }
    }

    @Redirect(method = "getAttackDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/item/SwordItem;attackDamage:F"))
    private float changeAttackDamage(SwordItem instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$attackDamage_r1_8;
        } else {
            return this.attackDamage;
        }
    }

    @Redirect(method = "getAttributeModifiers", at = @At(value = "FIELD", target = "Lnet/minecraft/item/SwordItem;attributeModifiers:Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> changeAttributeModifiers(SwordItem instance) {
        if (DebugSettings.global().replaceAttributeModifiers.isEnabled()) {
            return this.viaFabricPlus$AttributeModifiers_r1_8;
        } else {
            return this.attributeModifiers;
        }
    }

    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void changeMiningSpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            cir.setReturnValue(state.isOf(Blocks.COBWEB) ? 15.0F : 1.5F);
        }
    }

}
