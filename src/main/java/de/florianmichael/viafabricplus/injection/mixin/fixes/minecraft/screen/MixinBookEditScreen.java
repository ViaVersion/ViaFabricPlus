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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen {

    @ModifyConstant(method = "method_27596", constant = @Constant(intValue = 1024))
    private int modifyPageLength(int constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return 256;
        } else {
            return constant;
        }
    }

    @ModifyConstant(method = "appendNewPage", constant = @Constant(intValue = 100))
    private int modifyPageCount(int constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return 50;
        } else {
            return constant;
        }
    }

}
