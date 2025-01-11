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

package com.viaversion.viafabricplus.injection.mixin.compat.classic4j;

import com.viaversion.viafabricplus.injection.access.base.ITextFieldWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * ClassicCube allows to use forbidden characters which the game doesn't allow in passwords, so we have to bypass this check
 * in order to allow the user to enter their password.
 */
@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget implements ITextFieldWidget {

    @Unique
    private boolean viaFabricPlus$forbiddenCharactersUnlocked = false;

    @Redirect(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringHelper;isValidChar(C)Z"))
    private boolean allowForbiddenCharacters(char c) {
        return this.viaFabricPlus$forbiddenCharactersUnlocked || StringHelper.isValidChar(c);
    }

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringHelper;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;"))
    private String allowForbiddenCharacters(String string) {
        if (this.viaFabricPlus$forbiddenCharactersUnlocked) {
            return string;
        } else {
            return StringHelper.stripInvalidChars(string);
        }
    }

    @Override
    public void viaFabricPlus$unlockForbiddenCharacters() {
        this.viaFabricPlus$forbiddenCharactersUnlocked = true;
    }

}
