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

import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.ClientboundPackets1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.meta.MetaFilter;
import de.florianmichael.viafabricplus.fixes.tracker.WolfHealthTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MetadataRewriter1_15To1_14_4.class, remap = false)
public abstract class MixinMetadataRewriter1_15To1_14_4 extends EntityRewriter<ClientboundPackets1_14_4, Protocol1_15To1_14_4> {

    public MixinMetadataRewriter1_15To1_14_4(Protocol1_15To1_14_4 protocol) {
        super(protocol);
    }

    @Redirect(method = "registerRewrites", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/rewriter/meta/MetaFilter$Builder;removeIndex(I)V"))
    private void trackHealth(MetaFilter.Builder instance, int index) {
        instance.handler((event, meta) -> { // Basically removeIndex, but we need to track the actual health value
            final int metaIndex = event.index();
            if (metaIndex == index) {
                WolfHealthTracker.get(event.user()).setWolfHealth(event.entityId(), meta.value());
                event.cancel();
            } else if (metaIndex > index) {
                event.setIndex(metaIndex - 1);
            }
        });
    }

}
