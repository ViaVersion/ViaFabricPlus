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

package com.viaversion.viafabricplus.injection.mixin.base.ui;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.storage.ClassicProgressStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class MixinLevelLoadingScreen extends Screen {
    public MixinLevelLoadingScreen(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderClassicProgress(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (GeneralSettings.INSTANCE.showClassicLoadingProgressInConnectScreen.getValue()) {
            // Check if ViaVersion is translating
            final UserConnection connection = ProtocolTranslator.getPlayNetworkUserConnection();
            if (connection == null) {
                return;
            }

            // Check if the client is connecting to a classic server
            final ClassicProgressStorage classicProgressStorage = connection.get(ClassicProgressStorage.class);
            if (classicProgressStorage == null) {
                return;
            }

            // Draw the classic loading progress
            context.drawCenteredTextWithShadow(
                client.textRenderer,
                ChatUtil.prefixText(classicProgressStorage.status),
                width / 2,
                height / 2 - 30,
                -1
            );
        }
    }
}
