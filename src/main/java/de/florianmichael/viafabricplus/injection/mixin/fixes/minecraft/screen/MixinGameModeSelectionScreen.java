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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import net.raphimc.vialoader.util.VersionEnum;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GameModeSelectionScreen.class)
public class MixinGameModeSelectionScreen {

    @Mutable
    @Shadow @Final private static int UI_WIDTH;

    @Unique
    private GameModeSelectionScreen.GameModeSelection[] viafabricplus_unwrappedGameModes;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixUIWidth(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_8)) {
            final List<GameModeSelectionScreen.GameModeSelection> gameModeSelections = new ArrayList<>(Arrays.stream(GameModeSelectionScreen.GameModeSelection.values()).toList());

            if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_3_1tor1_3_2)) gameModeSelections.remove(GameModeSelectionScreen.GameModeSelection.ADVENTURE);
            if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_8)) gameModeSelections.remove(GameModeSelectionScreen.GameModeSelection.SPECTATOR);

            viafabricplus_unwrappedGameModes = gameModeSelections.toArray(GameModeSelectionScreen.GameModeSelection[]::new);
            UI_WIDTH = viafabricplus_unwrappedGameModes.length * 31 - 5;
        }
    }

    @Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    public GameModeSelectionScreen.GameModeSelection[] removeNewerGameModes() {
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_8)) {
            return viafabricplus_unwrappedGameModes;
        }
        return GameModeSelectionScreen.GameModeSelection.values();
    }
}
