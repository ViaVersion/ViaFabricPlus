/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.features.block.bedrock.dynamic.model;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.Quadrant;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import org.cube.converter.model.element.Cube;
import org.cube.converter.model.element.Parent;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.cube.converter.util.element.Position3V;
import org.cube.converter.util.math.MathUtil;
import org.cube.converter.util.math.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.resources.model.cuboid.FaceBakery;

/**
 * Mostly methods copied from net.minecraft.client.resources.model.cuboid.FaceBakery but improved for ours use case.
 */
public class BlockModelBaker {
    public static QuadCollection bake(final ModelBaker modelBaker, final BedrockGeometryModel model, final TextureSlots textures, int lightEmission, DynamicBlockCache.Transformation transformation) {
        QuadCollection.Builder builder = new QuadCollection.Builder();

        for (final Parent parent : model.getParents()) {
            final List<Pair<Position3V, Position3V>> rotations = new ArrayList<>();
            Parent next = parent;
            while (next != null) {
                rotations.add(new Pair<>(next.getRotation(), next.getPivot()));

                final String name = next.getParent();
                next = null;
                if (name != null && !name.isEmpty()) {
                    for (final Parent other : model.getParents()) {
                        if (other.getName().equals(name)) {
                            next = other;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }

            rotations.add(new Pair<>(transformation.rotation().multiply(-1, -1, 1), transformation.pivot()));

            Collections.reverse(rotations);

            for (Cube cube : parent.getCubes().values()) {
                cube = cube.clone();

                cube.inflate();
                cube.getPosition().set(cube.getPosition().asJavaPosition(cube.getSize()));
                cube.getPosition().set(cube.getPosition().add(transformation.translation().multiply(16, 16, 16)));

                final Vector3fc from = toVector3fc(cube.getPosition()), to = toVector3fc(cube.getPosition().add(cube.getSize()));

                boolean drawX = true, drawY = true, drawZ = true;

                if (from.x() == to.x()) {
                    drawY = false;
                    drawZ = false;
                }
                if (from.y() == to.y()) {
                    drawX = false;
                    drawZ = false;
                }
                if (from.z() == to.z()) {
                    drawX = false;
                    drawY = false;
                }

                if (!drawX && !drawY && !drawZ) {
                    continue;
                }

                for (Map.Entry<org.cube.converter.util.element.Direction, Float[]> entry : cube.getUvMap().getUvMap().entrySet()) {
                    Direction facing = Direction.values()[entry.getKey().ordinal()];
                    Float[] uv = entry.getValue();
                    boolean var10000;
                    switch (facing.getAxis()) {
                        case X -> var10000 = drawX;
                        case Y -> var10000 = drawY;
                        case Z -> var10000 = drawZ;
                        default -> throw new MatchException(null, null);
                    }

                    if (uv != null) {
                        for (int i = 0; i < uv.length; i++) {
                            uv[i] = MathUtil.clamp(uv[i] * 16 / (i % 2 == 0 ? model.getTextureSize().getX() : model.getTextureSize().getY()), 0, 16);
                        }
                    }

                    boolean shouldDrawFace = var10000;
                    if (shouldDrawFace) {
                        final List<Pair<Position3V, Position3V>> allRotations = new ArrayList<>(rotations);
                        allRotations.add(new Pair<>(cube.getRotation(), cube.getPivot()));

                        Material.Baked material = modelBaker.materials().resolveSlot(textures, facing.name(), () -> "");
                        BakedQuad quad = bakeQuad(modelBaker, from, to, material, facing, lightEmission, uv, allRotations);
                        builder.addUnculledFace(quad);
                    }
                }
            }
        }

        return builder.build();
    }

    public static BakedQuad bakeQuad(final ModelBaker modelBaker,
                                     final Vector3fc from, final Vector3fc to,
                                     final Material.Baked material,
                                     final Direction facing,
                                     final int lightEmission, Float[] uv, List<Pair<Position3V, Position3V>> rotations) {
        CuboidFace.UVs uvs;
        if (uv == null) {
            uvs = FaceBakery.defaultFaceUV(from, to, facing);
        } else {
            uvs = new CuboidFace.UVs(uv[0], uv[1], uv[2], uv[3]);
        }

        Transparency transparency = FaceBakery.computeMaterialTransparency(material, uvs);
        ModelBaker.Interner interner = modelBaker.interner();
        BakedQuad.MaterialInfo materialInfo = interner.materialInfo(BakedQuad.MaterialInfo.of(material, transparency, -1, true, lightEmission));
        return bakeQuad(interner, from, to, uvs, materialInfo, facing, rotations);
    }

    private static BakedQuad bakeQuad(final ModelBaker.Interner interner, final Vector3fc from,
                                      final Vector3fc to, final CuboidFace.UVs uvs,
                                      final BakedQuad.MaterialInfo materialInfo, final Direction facing,
                                      List<Pair<Position3V, Position3V>> rotations) {
        Vector3fc[] vertexPositions = new Vector3fc[4];
        long[] vertexPackedUvs = new long[4];
        FaceInfo faceInfo = FaceInfo.fromFacing(facing);

        for(int i = 0; i < 4; ++i) {
            bakeVertex(i, faceInfo, uvs, from, to, materialInfo, vertexPositions, vertexPackedUvs, interner, rotations);
        }

        Direction finalDirection = FaceBakery.calculateFacing(vertexPositions);
        if (rotations.isEmpty() && finalDirection != null) {
            FaceBakery.recalculateWinding(vertexPositions, vertexPackedUvs, finalDirection);
        }

        return new BakedQuad(vertexPositions[0], vertexPositions[1], vertexPositions[2], vertexPositions[3], vertexPackedUvs[0], vertexPackedUvs[1], vertexPackedUvs[2], vertexPackedUvs[3], (Direction)Objects.requireNonNullElse(finalDirection, Direction.UP), materialInfo);
    }

    private static void bakeVertex(final int index, final FaceInfo faceInfo,
                                   final CuboidFace.UVs uvs, final Vector3fc from, final Vector3fc to,
                                   final BakedQuad.MaterialInfo materialInfo,
                                   final Vector3fc[] positionOutput, final long[] uvOutput, final ModelBaker.Interner interner,
                                   List<Pair<Position3V, Position3V>> rotations) {
        FaceInfo.VertexInfo vertexInfo = faceInfo.getVertexInfo(index);
        Vector3f vertex = vertexInfo.select(from, to).div(16.0F);
        for (Pair<Position3V, Position3V> rotation : rotations) {
            FaceBakery.rotateVertexBy(vertex,
                toVector3fc(rotation.right().multiply(-1, 1, 1).withJavaOffset().multiply(0.0625f, 0.0625f, 0.0625f)),
                new Matrix4f().rotationXYZ((float) Math.toRadians(-rotation.left().getX()),
                    (float) Math.toRadians(-rotation.left().getY()), (float) Math.toRadians(rotation.left().getZ())));
        }

        float rawU = CuboidFace.getU(uvs, Quadrant.R0, index);
        float rawV = CuboidFace.getV(uvs, Quadrant.R0, index);

        positionOutput[index] = interner.vector(vertex);
        uvOutput[index] = UVPair.pack(materialInfo.sprite().getU(rawU), materialInfo.sprite().getV(rawV));
    }

    private static Vector3fc toVector3fc(Position3V position3V) {
        return new Vector3f(position3V.getX(), position3V.getY(), position3V.getZ());
    }
}
