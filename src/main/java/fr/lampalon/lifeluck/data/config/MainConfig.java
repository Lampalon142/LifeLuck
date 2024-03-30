package fr.lampalon.lifeluck.data.config;

import fr.lampalon.lifeluck.LifeLuck;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig {
    private final FileConfiguration config = LifeLuck.get().getConfig();
    public String prefix = config.getString("prefix");
}
