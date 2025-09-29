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
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow
    private BakedGlyph blankBakedGlyph;

//    @Shadow
//    @Final
//    private Identifier id;

    @Unique
    private BakedGlyph viaFabricPlus$blankBakedGlyph1_12_2;

    @Unique
    private boolean viaFabricPlus$obfuscatedLookup;

    // TODO
//    @Shadow
//    protected abstract BakedGlyph bake(RenderableGlyph c);

//    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/BakedGlyph;", ordinal = 0))
//    private void bakeBlankGlyph1_12_2(CallbackInfo ci) {
//        this.viaFabricPlus$blankBakedGlyph1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this::bake);
//    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void filterGlyphs(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
//        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
//            cir.setReturnValue(this.viaFabricPlus$getBlankGlyphPair());
//        }
    }

//    @Inject(method = "bake(I)Lnet/minecraft/client/font/BakedGlyph;", at = @At("RETURN"), cancellable = true)
//    private void filterBakedGlyph(int codePoint, CallbackInfoReturnable<BakedGlyph> cir) {
//        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
//            cir.setReturnValue(this.viaFabricPlus$getBlankBakedGlyph());
//        }
//    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void fixBlankGlyph1_12_2(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final FontStorage.GlyphPair glyphPair = cir.getReturnValue();
//            final Glyph glyph1 = glyphPair.glyph();
//            final Glyph glyph2 = glyphPair.advanceValidatedGlyph();
//            cir.setReturnValue(new FontStorage.GlyphPair(glyph1 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph1, glyph2 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph2));
        }
    }

//    @Redirect(method = "bake(I)Lnet/minecraft/client/font/BakedGlyph;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/font/FontStorage;blankBakedGlyph:Lnet/minecraft/client/font/BakedGlyph;"))
//    private BakedGlyph fixBlankBakedGlyph1_12_2(FontStorage instance) {
//        return this.viaFabricPlus$getBlankBakedGlyph();
//    }

//    @Unique
//    private boolean viaFabricPlus$shouldBeInvisible(final int codePoint) {
//        if (!viaFabricPlus$obfuscatedLookup && DebugSettings.INSTANCE.filterNonExistingGlyphs.getValue()) {
//            return (this.id.equals(MinecraftClient.DEFAULT_FONT_ID) || this.id.equals(MinecraftClient.UNICODE_FONT_ID)) && !RenderableGlyphDiff.isGlyphRenderable(codePoint);
//        } else {
//            return false;
//        }
//    }

    // Ignore obfuscated texts in the character filter as obfuscated text uses *all* characters, even those not existing in the current target version.
    @Inject(method = "getObfuscatedBakedGlyph", at = @At("HEAD"))
    private void pauseCharacterFiltering(Random random, int width, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = true;
    }

    @Inject(method = "getBaked", at = @At("RETURN"))
    private void resumeCharacterFiltering(int codePoint, CallbackInfoReturnable<BakedGlyph> cir) {
        viaFabricPlus$obfuscatedLookup = false;
    }

//    @Unique
//    private FontStorage.GlyphPair viaFabricPlus$getBlankGlyphPair() {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
//            return new FontStorage.GlyphPair(BuiltinEmptyGlyph1_12_2.INSTANCE, BuiltinEmptyGlyph1_12_2.INSTANCE);
//        } else {
//            return FontStorage.GlyphPair.MISSING;
//        }
//    }

    @Unique
    private BakedGlyph viaFabricPlus$getBlankBakedGlyph() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viaFabricPlus$blankBakedGlyph1_12_2;
        } else {
            return this.blankBakedGlyph;
        }
    }
}
