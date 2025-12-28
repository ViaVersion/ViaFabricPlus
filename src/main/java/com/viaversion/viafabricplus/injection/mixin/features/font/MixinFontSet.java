/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.GlyphStitcher;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontSet.class)
public abstract class MixinFontSet {

    @Shadow
    @Final
    GlyphStitcher stitcher;

    @Shadow
    @Final
    private FontSet.SelectedGlyphs missingSelectedGlyphs;

    @Unique
    private BakedGlyph viaFabricPlus$blankBakedGlyph1_12_2;

    @Unique
    private FontSet.SelectedGlyphs viaFabricPlus$blankBakedGlyphPair1_12_2;

    @Unique
    private boolean viaFabricPlus$obfuscatedLookup;

    @Inject(method = "resetTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/SpecialGlyphs;bake(Lnet/minecraft/client/gui/font/GlyphStitcher;)Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;", ordinal = 0))
    private void bakeBlankGlyph1_12_2(CallbackInfo ci) {
        this.viaFabricPlus$blankBakedGlyph1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this.stitcher);
        this.viaFabricPlus$blankBakedGlyphPair1_12_2 = new FontSet.SelectedGlyphs(() -> this.viaFabricPlus$blankBakedGlyph1_12_2, () -> this.viaFabricPlus$blankBakedGlyph1_12_2);
    }

    @Inject(method = "getGlyph", at = @At("RETURN"), cancellable = true)
    private void filterBakedGlyph(int codePoint, CallbackInfoReturnable<FontSet.SelectedGlyphs> cir) {
        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
                cir.setReturnValue(viaFabricPlus$blankBakedGlyphPair1_12_2);
            } else {
                cir.setReturnValue(missingSelectedGlyphs);
            }
        }
    }

    @Inject(method = "computeGlyphInfo", at = @At("RETURN"), cancellable = true)
    private void fixBlankGlyph1_12_2(int codePoint, CallbackInfoReturnable<FontSet.SelectedGlyphs> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final FontSet.SelectedGlyphs glyphPair = cir.getReturnValue();
            cir.setReturnValue(glyphPair == this.missingSelectedGlyphs ? this.viaFabricPlus$blankBakedGlyphPair1_12_2 : glyphPair);
        }
    }

    // Ignore obfuscated texts in the character filter as obfuscated text uses *all* characters, even those not existing in the current target version.
    @Inject(method = "getRandomGlyph", at = @At("HEAD"))
    private void pauseCharacterFiltering(RandomSource random, int width, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = true;
    }

    @Inject(method = "getGlyph", at = @At("RETURN"))
    private void resumeCharacterFiltering(int codePoint, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = false;
    }

    @Unique
    private boolean viaFabricPlus$shouldBeInvisible(final int codePoint) {
        if (!viaFabricPlus$obfuscatedLookup && DebugSettings.INSTANCE.filterNonExistingGlyphs.getValue()) {
            return (stitcher.texturePrefix.equals(Minecraft.DEFAULT_FONT) || stitcher.texturePrefix.equals(Minecraft.UNIFORM_FONT)) && !RenderableGlyphDiff.isGlyphRenderable(codePoint);
        } else {
            return false;
        }
    }

}
