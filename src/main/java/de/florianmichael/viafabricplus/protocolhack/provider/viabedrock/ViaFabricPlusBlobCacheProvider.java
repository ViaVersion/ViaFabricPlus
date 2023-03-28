/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.protocolhack.provider.viabedrock;

import net.raphimc.viabedrock.protocol.providers.BlobCacheProvider;

import java.util.HashMap;
import java.util.Map;

public class ViaFabricPlusBlobCacheProvider extends BlobCacheProvider {

    private final Map<Long, byte[]> blobs = new HashMap<>();
    private long size;

    public ViaFabricPlusBlobCacheProvider() {
        this.blobs.put(0L, new byte[0]);
    }

    @Override
    public byte[] addBlob(final long hash, final byte[] compressedBlob) {
        synchronized (this.blobs) {
            if (this.blobs.containsKey(hash)) { // In case the server overwrites a blob
                size -= this.blobs.get(hash).length;
                this.blobs.remove(hash);
            }
            size += compressedBlob.length;
            return this.blobs.put(hash, compressedBlob);
        }
    }

    @Override
    public boolean hasBlob(final long hash) {
        synchronized (this.blobs) {
            return this.blobs.containsKey(hash);
        }
    }

    @Override
    public byte[] getBlob(final long hash) {
        synchronized (this.blobs) {
            return this.blobs.get(hash);
        }
    }

    public Map<Long, byte[]> getBlobs() {
        return blobs;
    }

    public long getSize() {
        return size;
    }
}
