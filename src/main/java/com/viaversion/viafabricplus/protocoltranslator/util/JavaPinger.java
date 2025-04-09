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

package com.viaversion.viafabricplus.protocoltranslator.util;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Very lightweight modern SLP pinger
 */
public final class JavaPinger {

    private final static Gson GSON = new Gson();

    public static Response ping(InetSocketAddress address, int timeout, int protocolVersion) {
        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(timeout);
            socket.connect(null, timeout);
            try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream handshake = new DataOutputStream(b)) {
                handshake.writeByte(0);
                writeVarInt(handshake, protocolVersion);
                writeVarInt(handshake, address.getHostString().length());
                handshake.writeBytes(address.getHostString());
                handshake.writeShort(address.getPort());
                writeVarInt(handshake, 1);
                writeVarInt(dataOutputStream, b.size());
                dataOutputStream.write(b.toByteArray());
                dataOutputStream.writeByte(1);
                dataOutputStream.writeByte(0);

                int size = readVarInt(dataInputStream);
                int id = readVarInt(dataInputStream);
                int length = readVarInt(dataInputStream);

                if (size <= 0 || id != 0 || length <= 0) {
                    throw new IllegalStateException("No data received from server");
                }
                byte[] in = new byte[length];
                dataInputStream.readFully(in);
                return GSON.fromJson(new String(in, StandardCharsets.UTF_8), Response.class);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not ping " + address + " with protocol " + protocolVersion, e);
        }
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0, j = 0;
        byte k;
        do {
            k = in.readByte();
            i |= (k & 0x7F) << (j++ * 7);
            if (j > 5) {
                throw new IOException("VarInt too big");
            }
        } while ((k & 0x80) != 0);
        return i;
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    public record Response(Version version) {}

    public record Version(String name, int protocol) {}

    public JavaPinger() {}
}
