/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_15to1_14_4;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.ClientboundPackets1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import de.florianmichael.viafabricplus.definition.Meta18Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(MetadataRewriter1_15To1_14_4.class)
public abstract class MixinMetadataRewriter1_15To1_14_4 extends EntityRewriter<ClientboundPackets1_14_4, Protocol1_15To1_14_4> {

    public MixinMetadataRewriter1_15To1_14_4(Protocol1_15To1_14_4 protocol) {
        super(protocol);
    }

    @Inject(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), remap = false)
    public void trackHealth(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection, CallbackInfo ci) {
        if (protocol.get(Meta18Storage.class) == null) {
            protocol.put(new Meta18Storage(connection));
            Via.getPlatform().getLogger().severe("Metadata 18 storage is missing!");
        }
        protocol.get(Meta18Storage.class).getHealthDataMap().put(entityId, (Float) metadata.getValue());
    }
}
