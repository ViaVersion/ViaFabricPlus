/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.item.tooltip;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.viafabricplus.util.ItemUtil;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.rewriter.ComponentRewriter1_21_5;
import com.viaversion.viaversion.util.TagUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ComponentRewriter1_21_5.class, remap = false)
public abstract class MixinComponentRewriter1_21_5 {

    @WrapOperation(method = "handleShowItem", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/util/TagUtil;removeNamespaced(Lcom/viaversion/nbt/tag/CompoundTag;Ljava/lang/String;)Z"))
    private boolean storeBackupTag(CompoundTag tag, String key, Operation<Boolean> original) {
        if (key.equals("hide_additional_tooltip")) {
            CompoundTag customData = TagUtil.getNamespacedCompoundTag(tag, "custom_data");
            if (customData == null) {
                tag.put("custom_data", customData = new CompoundTag());
            }

            final CompoundTag backupTag = new CompoundTag();
            backupTag.putBoolean("hide_additional_tooltip", true);
            customData.put(ItemUtil.vvNbtName(Protocol1_21_4To1_21_5.class, "backup"), backupTag);
        }

        return original.call(tag, key);
    }

}
