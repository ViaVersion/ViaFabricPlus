package de.florianmichael.viafabricplus.definition.c0_30.classicube.data;

import com.google.gson.annotations.SerializedName;

public class ClassiCubeServerInfo {
    public final String hash;
    public final int maxplayers;
    public final String name;
    public final int players;
    public final String software;
    public final long uptime;
    @SerializedName("country_abbr")
    public final String countryCode;
    public final boolean web;
    public final boolean featured;
    public final String ip;
    public final int port;
    public final String mppass;

    public ClassiCubeServerInfo(String hash, int maxplayers, String name, int players, String software, long uptime, String countryCode, boolean web, boolean featured, String ip, int port, String mppass) {
        this.hash = hash;
        this.maxplayers = maxplayers;
        this.name = name;
        this.players = players;
        this.software = software;
        this.uptime = uptime;
        this.countryCode = countryCode;
        this.web = web;
        this.featured = featured;
        this.ip = ip;
        this.port = port;
        this.mppass = mppass;
    }
}
