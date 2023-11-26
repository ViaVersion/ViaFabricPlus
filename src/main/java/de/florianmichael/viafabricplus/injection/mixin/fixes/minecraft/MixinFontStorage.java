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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import de.florianmichael.viafabricplus.injection.reference.BuiltinEmptyGlyph1_12_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.font.*;
import net.minecraft.util.Identifier;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow
    protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Shadow
    private GlyphRenderer blankGlyphRenderer;

    @Shadow
    @Final
    private Identifier id;

    @Unique
    private Map<String, List<Integer>> viaFabricPlus$forbiddenCharacters;

    @Unique
    private boolean viaFabricPlus$obfuscation;

    @Inject(method = "setFonts", at = @At("HEAD"))
    private void trackForbiddenCharacters(List<Font> fonts, CallbackInfo ci) {
//        viaFabricPlus$forbiddenCharacters = CharacterMappings.getForbiddenCharactersForID(this.id);
        viaFabricPlus$forbiddenCharacters = new HashMap<>(); // TODO | Fix
    }

    @Unique
    private boolean viaFabricPlus$isForbiddenCharacter(final Font font, final int codePoint) {
        String fontName = null;
        if (font instanceof BitmapFont) {
            fontName = "BitmapFont";
        } else if (font instanceof BlankFont) {
            fontName = "BlankFont";
        } else if (font instanceof SpaceFont) {
            fontName = "SpaceFont";
        } else if (font instanceof UnihexFont) {
            fontName = "UnihexFont";
        }
        if (fontName == null) return false;
        final var forbiddenCodepoints = viaFabricPlus$forbiddenCharacters.get(fontName);
        if (forbiddenCodepoints == null) return false;
        return forbiddenCodepoints.contains(codePoint);
    }

    @Inject(method = "findGlyph", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/Font;getGlyph(I)Lnet/minecraft/client/font/Glyph;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injectFindGlyph(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir, Glyph glyph, Iterator var3, Font font) {
        if (!this.id.getNamespace().equals("minecraft")) return;

        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            if (viaFabricPlus$isForbiddenCharacter(font, codePoint)) cir.setReturnValue(FontStorage.GlyphPair.MISSING);

            if (VisualSettings.global().changeFontRendererBehaviour.isEnabled() && cir.getReturnValue() == FontStorage.GlyphPair.MISSING) {
                cir.setReturnValue(new FontStorage.GlyphPair(BuiltinEmptyGlyph1_12_2.VERY_MISSING, BuiltinEmptyGlyph1_12_2.VERY_MISSING));
            }
        }
    }

    @Inject(method = "findGlyphRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/Font;getGlyph(I)Lnet/minecraft/client/font/Glyph;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void injectFindGlyphRenderer(int codePoint, CallbackInfoReturnable<GlyphRenderer> cir, Iterator var2, Font font) {
        if (!this.id.getNamespace().equals("minecraft")) return;

        if (!viaFabricPlus$obfuscation && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            if (viaFabricPlus$isForbiddenCharacter(font, codePoint)) cir.setReturnValue(this.blankGlyphRenderer);

            if (VisualSettings.global().changeFontRendererBehaviour.isEnabled() && cir.getReturnValue() == this.blankGlyphRenderer) {
                cir.setReturnValue(BuiltinEmptyGlyph1_12_2.VERY_MISSING.bake(this::getGlyphRenderer));
            }
        }
    }

    /*
    Minecraft uses all characters that exist for obfuscation mode, even those that no longer exist in the selected target version,
    so we must not make the fix in case it is executed from an obfuscated text, because otherwise the obfuscation would have missing characters
     */

    @Inject(method = "getObfuscatedGlyphRenderer", at = @At("HEAD"))
    private void trackObfuscationState(Glyph glyph, CallbackInfoReturnable<GlyphRenderer> cir) {
        viaFabricPlus$obfuscation = true;
    }

    @Inject(method = "getGlyphRenderer(I)Lnet/minecraft/client/font/GlyphRenderer;", at = @At("RETURN"))
    private void revertObfuscationState(int codePoint, CallbackInfoReturnable<GlyphRenderer> cir) {
        viaFabricPlus$obfuscation = false;
    }

}
