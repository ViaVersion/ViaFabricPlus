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
package de.florianmichael.viafabricplus.injection.mixin.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.information.AbstractInformationGroup;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.base.settings.groups.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    public void addViaFabricPlusInformation(CallbackInfoReturnable<List<String>> cir) {
        if (MinecraftClient.getInstance().isInSingleplayer() || !GeneralSettings.INSTANCE.showExtraInformationInDebugHud.getValue()) return;

        final List<String> information = new ArrayList<>();
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            final UserConnection userConnection = ProtocolHack.getMainUserConnection();

            information.add("");
            information.add(ChatUtil.PREFIX);

            for (AbstractInformationGroup group : ViaFabricPlus.INSTANCE.getInformationSystem().getGroups()) {
                if (group.getVersionRange() != null && !group.getVersionRange().contains(ProtocolHack.getTargetVersion())) continue;

                final List<String> groupInformation = new ArrayList<>();
                try {
                    group.applyInformation(userConnection, groupInformation);
                } catch (Exception ignored) {}
                if (groupInformation.isEmpty()) continue;

                information.add(group.getVersionRange() == null ? "General" : group.getVersionRange().toString());
                information.addAll(groupInformation);
                information.add("");
            }
        }
        cir.getReturnValue().addAll(information);
    }
}
