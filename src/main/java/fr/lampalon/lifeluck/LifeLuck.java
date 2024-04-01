package fr.lampalon.lifeluck;

import fr.lampalon.lifeluck.commands.TopLuckCmd;
import fr.lampalon.lifeluck.data.config.MainConfig;
import fr.lampalon.lifeluck.data.config.Options;
import fr.lampalon.lifeluck.events.BlockBreakListener;
import fr.lampalon.lifeluck.events.onInventoryClick;
import fr.lampalon.lifeluck.utils.OreTracker;
import fr.lampalon.lifeluck.gui.MainMenu;
//import fr.lampalon.lifeluck.utils.Update;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;

public final class LifeLuck extends JavaPlugin {
    private static MainConfig mc;
    private static Options options;
    public static LifeLuck instance;
    private FileConfiguration config;
    private OreTracker oreTracker;
    private MainMenu mainMenu;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        startup();
        utils();
        //Update();
    }

    private void startup(){
        registerCommands();
        registerEvents();
        this.mc = new MainConfig();
        this.options = new Options();
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("[LifeLuck] Good bye thanks to use LifeLuck");
        this.mc = null;
        this.options = null;
    }

    private void registerCommands(){
        FileConfiguration config = getConfig();
        oreTracker = new OreTracker(config);
        mainMenu = new MainMenu(oreTracker);

        getCommand("topluck").setExecutor(new TopLuckCmd(this, mainMenu));
    }
    private void registerEvents(){
        PluginManager pm = Bukkit.getPluginManager();
        this.config = getConfig();

        BlockBreakListener blockBreakListener = new BlockBreakListener(oreTracker);
        onInventoryClick onInventoryClick = new onInventoryClick();

        pm.registerEvents(blockBreakListener, this);
        pm.registerEvents(onInventoryClick, this);
    }

    private void utils(){
        int pluginId = 19817;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SingleLineChart("players", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Bukkit.getOnlinePlayers().size();
            }
        }));
    }
    //private void Update(){
    //new Update(this, 21395).getLatestVersion(version -> {
    //if(this.getDescription().getVersion().equalsIgnoreCase(version)){
    //this.getLogger().info("Plugin use the latest update thanks.");
    //} else {
    //this.getLogger().warning("Plugin required an update ! (https://www.spigotmc.org/resources/1-8-1-20-lifemod-moderation-plugin.112381/)");
    //}
    //});
    //}
    public static LifeLuck get(){
        return instance;
    }
    public OreTracker getOreTracker() {
        return oreTracker;
    }
}
