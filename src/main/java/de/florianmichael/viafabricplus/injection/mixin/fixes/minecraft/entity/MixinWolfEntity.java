/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.v1_14_4.Meta18Storage;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DataFlowIssue")
@Mixin(WolfEntity.class)
public class MixinWolfEntity {

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;getHealth()F"))
    public float rewriteHealth(WolfEntity instance) {
        float health = instance.getHealth();
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            return MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get().get(Meta18Storage.class).getHealthDataMap().getOrDefault(instance.getId(), health);
        }
        return health;
    }
}
