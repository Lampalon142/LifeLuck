package fr.lampalon.lifeluck.data.config;

import fr.lampalon.lifeluck.LifeLuck;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Options {
    private static FileConfiguration config = LifeLuck.get().getConfig();
    private InputStream stream = LifeLuck.get().getResource("config.yml");
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(this.stream, StandardCharsets.UTF_8));
    private int configVersion = this.configuration.getInt("version");

    public Options(){
        if (101 < this.configVersion){
            File dataFolder = LifeLuck.get().getDataFolder();
            File configFile = new File(dataFolder, "config.yml");
            YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(configFile);
            String backup = "backup-#101.yml";
            configFile.renameTo(new File(dataFolder, backup));
            LifeLuck.get().getConfig().options().copyDefaults(true);
            config.set("config-version", Integer.valueOf(101));
            config.options().header(" LifeLuck | Made and Directed by Lampalon_ with love\n Your configuration file has been autmatically updated to lastest version. (101)\n Note : All data has been deleted but you have your config on 'backup-#101.yml' !");
            config.options().copyHeader();
            config.set("config-version", Integer.valueOf(this.configVersion));
            LifeLuck.get().saveConfig();
        }
    }
    public YamlConfiguration getConfiguration(){
        return YamlConfiguration.loadConfiguration(new File(LifeLuck.get().getDataFolder(), "config.yml"));
    }
    public Options reloadConfig(){
        return new Options();
    }
    private void updateConfig(){
        File dataFolder = LifeLuck.get().getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(configFile);
        String backup = "backup-#101.yml";
        String currentKey = "";
        configFile.renameTo(new File(dataFolder, backup));
        LifeLuck.get().saveDefaultConfig();
        config = LifeLuck.get().getConfig();
        for (String key : oldConfig.getConfigurationSection("").getKeys(true)){
            if (key.equalsIgnoreCase("config-version")){
                config.set(key, Integer.valueOf(101));
                continue;
            }
            config.set(key, oldConfig.get(key));
        }
        for (String key : config.getConfigurationSection("").getKeys(true)){
            if (!key.contains("."))
                currentKey = key;
            if (!oldConfig.contains(key))
                config.set(currentKey + key, config.get(key));
            config.options().header(" LifeLuck | Made and Directed by Lampalon_ with love\n Your configuration file has been autmatically updated to lastest version. (101)\n Note : All data has been deleted but you have your config on 'backup-#101.yml' !");
            config.options().copyHeader(true);
            LifeLuck.get().saveConfig();
            Bukkit.getConsoleSender().sendMessage("Your config has been updated to #101!");
        }
    }
}
