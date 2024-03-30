package fr.lampalon.lifeluck.data.config;

import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
    private static FileConfiguration config = MessagesConfig.get();

    public String noperm = config.getString("noperm");
    public String onlyplayer = config.getString("onlyplayer");
}
