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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MetadataRewriter1_9To1_8.class, remap = false)
public abstract class MixinMetadataRewriter1_9To1_8 {

    @Inject(method = "handleMetadata", at = @At(value = "FIELD", target = "Lcom/viaversion/viaversion/protocols/protocol1_9to1_8/metadata/MetaIndex;PLAYER_HAND:Lcom/viaversion/viaversion/protocols/protocol1_9to1_8/metadata/MetaIndex;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void preventMetadataForClientPlayer(MetaHandlerEvent event, Metadata metadata, CallbackInfo ci) {
        if (event.user().getEntityTracker(Protocol1_9To1_8.class).clientEntityId() == event.entityId()) {
            ci.cancel();
        }
    }

}
