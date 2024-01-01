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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import de.florianmichael.viafabricplus.fixes.data.RenderableGlyphDiff;
import de.florianmichael.viafabricplus.fixes.replacement.BuiltinEmptyGlyph1_12_2;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow
    private GlyphRenderer blankGlyphRenderer;

    @Shadow
    protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Shadow
    @Final
    private Identifier id;

    @Unique
    private GlyphRenderer viaFabricPlus$blankGlyphRenderer1_12_2;

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;", ordinal = 0))
    private void bakeBlankGlyph1_12_2(List<Font> fonts, CallbackInfo ci) {
        this.viaFabricPlus$blankGlyphRenderer1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this::getGlyphRenderer);
    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void filterGlyphs1(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir, @Local Font font) {
        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
            cir.setReturnValue(this.viaFabricPlus$getBlankGlyphPair());
        }
    }

    @Inject(method = "findGlyphRenderer", at = @At("RETURN"), cancellable = true)
    private void filterGlyphs2(int codePoint, CallbackInfoReturnable<GlyphRenderer> cir, @Local Font font) {
        if (this.viaFabricPlus$shouldBeInvisible(codePoint)) {
            cir.setReturnValue(this.viaFabricPlus$getBlankGlyphRenderer());
        }
    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void fixBlankGlyph1_12_2(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (VisualSettings.global().changeFontRendererBehaviour.isEnabled()) {
            final FontStorage.GlyphPair glyphPair = cir.getReturnValue();
            final Glyph glyph1 = glyphPair.glyph();
            final Glyph glyph2 = glyphPair.advanceValidatedGlyph();
            cir.setReturnValue(new FontStorage.GlyphPair(glyph1 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph1, glyph2 == BuiltinEmptyGlyph.MISSING ? BuiltinEmptyGlyph1_12_2.INSTANCE : glyph2));
        }
    }

    @Redirect(method = "findGlyphRenderer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/font/FontStorage;blankGlyphRenderer:Lnet/minecraft/client/font/GlyphRenderer;"))
    private GlyphRenderer fixBlankGlyphRenderer1_12_2(FontStorage instance) {
        return this.viaFabricPlus$getBlankGlyphRenderer();
    }

    @Unique
    private boolean viaFabricPlus$shouldBeInvisible(final int codePoint) {
        return (this.id.equals(MinecraftClient.DEFAULT_FONT_ID) || this.id.equals(MinecraftClient.UNICODE_FONT_ID)) && !RenderableGlyphDiff.isGlyphRenderable(codePoint);
    }

    @Unique
    private FontStorage.GlyphPair viaFabricPlus$getBlankGlyphPair() {
        if (VisualSettings.global().changeFontRendererBehaviour.isEnabled()) {
            return new FontStorage.GlyphPair(BuiltinEmptyGlyph1_12_2.INSTANCE, BuiltinEmptyGlyph1_12_2.INSTANCE);
        } else {
            return FontStorage.GlyphPair.MISSING;
        }
    }

    @Unique
    private GlyphRenderer viaFabricPlus$getBlankGlyphRenderer() {
        if (VisualSettings.global().changeFontRendererBehaviour.isEnabled()) {
            return this.viaFabricPlus$blankGlyphRenderer1_12_2;
        } else {
            return this.blankGlyphRenderer;
        }
    }

}
