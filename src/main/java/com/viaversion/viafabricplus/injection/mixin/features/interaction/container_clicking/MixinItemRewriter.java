/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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
 
 package com.viaversion.viafabricplus.injection.mixin.features.interaction.container_clicking;
 
 import com.viaversion.viaversion.api.data.entity.EntityTracker;
 import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.rewriter.BlockItemPacketRewriter1_21_5;
 import com.viaversion.viaversion.rewriter.ItemRewriter;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Redirect;
 
 @Mixin(value = { BlockItemPacketRewriter1_21_5.class, ItemRewriter.class }, remap = false)
 public abstract class MixinItemRewriter {
 
     @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/data/entity/EntityTracker;canInstaBuild()Z"))
     private boolean dontCancelPackets(EntityTracker entityTracker) {
         return true;
     }
 
 }
