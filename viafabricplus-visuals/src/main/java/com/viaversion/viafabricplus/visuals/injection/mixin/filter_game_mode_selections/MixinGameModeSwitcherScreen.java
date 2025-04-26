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

package com.viaversion.viafabricplus.visuals.injection.mixin.filter_game_mode_selections;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GameModeSwitcherScreen.class)
public abstract class MixinGameModeSwitcherScreen extends Screen {

    @Mutable
    @Shadow
    @Final
    private static int UI_WIDTH;

    @Unique
    private GameModeSwitcherScreen.GameModeSelection[] viaFabricPlusVisuals$unwrappedGameModes;

    public MixinGameModeSwitcherScreen(Text title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fixUIWidth(CallbackInfo ci) {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            final List<GameModeSwitcherScreen.GameModeSelection> selections = new ArrayList<>(Arrays.stream(GameModeSwitcherScreen.GameModeSelection.values()).toList());

            selections.remove(GameModeSwitcherScreen.GameModeSelection.SPECTATOR);
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                selections.remove(GameModeSwitcherScreen.GameModeSelection.ADVENTURE);
            }

            viaFabricPlusVisuals$unwrappedGameModes = selections.toArray(GameModeSwitcherScreen.GameModeSelection[]::new);
            UI_WIDTH = viaFabricPlusVisuals$unwrappedGameModes.length * 31 - 5;
        }
    }

    @Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSwitcherScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSwitcherScreen$GameModeSelection;"))
    private GameModeSwitcherScreen.GameModeSelection[] removeNewerGameModes() {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            return viaFabricPlusVisuals$unwrappedGameModes;
        } else {
            return GameModeSwitcherScreen.GameModeSelection.values();
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void disableInClassic(CallbackInfo ci) {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) { // survival mode was added in a1.0.15
            this.close();
        }
    }

}
