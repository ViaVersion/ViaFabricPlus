/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("DataFlowIssue")
@Mixin(GameModeSelectionScreen.GameModeSelection.class)
public abstract class MixinGameModeSelectionScreen_GameModeSelection {

    @Shadow @Final public static GameModeSelectionScreen.GameModeSelection SURVIVAL;

    @Shadow @Final public static GameModeSelectionScreen.GameModeSelection CREATIVE;

    @Inject(method = "getCommand", at = @At("HEAD"), cancellable = true)
    private void oldCommand(CallbackInfoReturnable<String> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_2_4tor1_2_5)) {
            cir.setReturnValue(
                    "gamemode " + MinecraftClient.getInstance().getSession().getUsername() + ' ' + switch (((Enum<?>)(Object)this).ordinal()) {
                        case 0, 3 -> 1;
                        case 1, 2 -> 0;
                        default -> throw new AssertionError();
                    }
            );
        }
    }

    @Inject(method = "next", at = @At("HEAD"), cancellable = true)
    private void unwrapGameModes(CallbackInfoReturnable<Optional<GameModeSelectionScreen.GameModeSelection>> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_8)) {
            switch ((GameModeSelectionScreen.GameModeSelection)(Object)this) {
                case CREATIVE -> cir.setReturnValue(Optional.of(SURVIVAL));
                case SURVIVAL -> {
                    if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.r1_2_4tor1_2_5)) {
                        cir.setReturnValue(Optional.of(CREATIVE));
                    } else {
                        cir.setReturnValue(Optional.of(GameModeSelectionScreen.GameModeSelection.ADVENTURE));
                    }
                }
                case ADVENTURE -> cir.setReturnValue(Optional.of(CREATIVE));
            }
        }
    }

}
