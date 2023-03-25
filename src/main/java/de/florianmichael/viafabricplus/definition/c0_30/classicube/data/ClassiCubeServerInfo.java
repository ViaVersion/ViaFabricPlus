package de.florianmichael.viafabricplus.definition.c0_30.classicube.data;

import com.google.gson.annotations.SerializedName;

public record ClassiCubeServerInfo(String hash, int maxplayers, String name, int players, String software, long uptime,
                                   @SerializedName("country_abbr") String countryCode, boolean web, boolean featured,
                                   String ip, int port, String mppass) {
}
