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
package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import de.florianmichael.viafabricplus.injection.access.IExtensionProtocolMetadataStorage;
import de.florianmichael.viafabricplus.event.LoadClassicProtocolExtensionCallback;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;

@Mixin(value = ExtensionProtocolMetadataStorage.class, remap = false)
public class MixinExtensionProtocolMetadataStorage implements IExtensionProtocolMetadataStorage {

    @Shadow @Final private EnumMap<ClassicProtocolExtension, Integer> serverExtensions;

    @Inject(method = "addServerExtension", at = @At("RETURN"))
    public void updateChatLengthDefinition(ClassicProtocolExtension extension, int version, CallbackInfo ci) {
        LoadClassicProtocolExtensionCallback.EVENT.invoker().onLoadClassicProtocolExtension(extension);
    }

    @Override
    public EnumMap<ClassicProtocolExtension, Integer> getServerExtensions() {
        return this.serverExtensions;
    }
}
