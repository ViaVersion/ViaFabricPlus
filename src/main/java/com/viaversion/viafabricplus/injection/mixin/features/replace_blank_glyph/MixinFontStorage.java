/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.replace_blank_glyph;

import com.viaversion.viafabricplus.features.replace_blank_glyph.BuiltinEmptyGlyph1_12_2;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow private BakedGlyph blankBakedGlyph;

    @Shadow protected abstract BakedGlyph bake(RenderableGlyph c);

    @Unique
    private BakedGlyph viaFabricPlus$blankBakedGlyph1_12_2;

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/BakedGlyph;", ordinal = 0))
    private void bakeBlankGlyph1_12_2(CallbackInfo ci) {
        this.viaFabricPlus$blankBakedGlyph1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this::bake);
    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void fixBlankGlyph1_12_2(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final FontStorage.GlyphPair glyphPair = cir.getReturnValue();
            final Glyph glyph1 = glyphPair.glyph();
            final Glyph glyph2 = glyphPair.advanceValidatedGlyph();
            cir.setReturnValue(new FontStorage.GlyphPair(glyph1 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph1, glyph2 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph2));
        }
    }

    @Redirect(method = "bake(I)Lnet/minecraft/client/font/BakedGlyph;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/font/FontStorage;blankBakedGlyph:Lnet/minecraft/client/font/BakedGlyph;"))
    private BakedGlyph fixBlankBakedGlyph1_12_2(FontStorage instance) {
        return this.viaFabricPlus$getBlankBakedGlyph();
    }

    @Unique
    private BakedGlyph viaFabricPlus$getBlankBakedGlyph() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viaFabricPlus$blankBakedGlyph1_12_2;
        } else {
            return this.blankBakedGlyph;
        }
    }

}
