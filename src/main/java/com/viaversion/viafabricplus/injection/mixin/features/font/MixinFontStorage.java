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

package com.viaversion.viafabricplus.injection.mixin.features.font;

import com.viaversion.viafabricplus.features.font.BuiltinEmptyGlyph1_12_2;
import com.viaversion.viafabricplus.features.font.RenderableGlyphDiff;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphBaker;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow
    @Final
    GlyphBaker glyphBaker;

    @Shadow
    @Final
    private FontStorage.GlyphPair blankBakedGlyphPair;

    @Unique
    private BakedGlyph viaFabricPlus$blankBakedGlyph1_12_2;

    @Unique
    private FontStorage.GlyphPair viaFabricPlus$blankBakedGlyphPair1_12_2;

    @Unique
    private boolean viaFabricPlus$obfuscatedLookup;

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Lnet/minecraft/client/font/GlyphBaker;)Lnet/minecraft/client/font/BakedGlyphImpl;", ordinal = 0))
    private void bakeBlankGlyph1_12_2(CallbackInfo ci) {
        this.viaFabricPlus$blankBakedGlyph1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this.glyphBaker);
        this.viaFabricPlus$blankBakedGlyphPair1_12_2 = new FontStorage.GlyphPair(() -> this.viaFabricPlus$blankBakedGlyph1_12_2, () -> this.viaFabricPlus$blankBakedGlyph1_12_2);
    }

    @Inject(method = "getBaked", at = @At("RETURN"), cancellable = true)
    private void filterBakedGlyph(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
                cir.setReturnValue(viaFabricPlus$blankBakedGlyphPair1_12_2);
            } else {
                cir.setReturnValue(blankBakedGlyphPair);
            }
        }
    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void fixBlankGlyph1_12_2(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final FontStorage.GlyphPair glyphPair = cir.getReturnValue();
            cir.setReturnValue(glyphPair == this.blankBakedGlyphPair ? this.viaFabricPlus$blankBakedGlyphPair1_12_2 : glyphPair);
        }
    }

    // Ignore obfuscated texts in the character filter as obfuscated text uses *all* characters, even those not existing in the current target version.
    @Inject(method = "getObfuscatedBakedGlyph", at = @At("HEAD"))
    private void pauseCharacterFiltering(Random random, int width, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = true;
    }

    @Inject(method = "getBaked", at = @At("RETURN"))
    private void resumeCharacterFiltering(int codePoint, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = false;
    }

    @Unique
    private boolean viaFabricPlus$shouldBeInvisible(final int codePoint) {
        if (!viaFabricPlus$obfuscatedLookup && DebugSettings.INSTANCE.filterNonExistingGlyphs.getValue()) {
            return (glyphBaker.fontId.equals(MinecraftClient.DEFAULT_FONT_ID) || glyphBaker.fontId.equals(MinecraftClient.UNICODE_FONT_ID)) && !RenderableGlyphDiff.isGlyphRenderable(codePoint);
        } else {
            return false;
        }
    }

}
