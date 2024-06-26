package fr.lampalon.lifeluck.commands;

import fr.lampalon.lifeluck.LifeLuck;
import fr.lampalon.lifeluck.gui.MainMenu;
import fr.lampalon.lifeluck.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TopLuckCmd implements CommandExecutor {
    private final LifeLuck plugin;
    private final MainMenu menuManager;
    private FileConfiguration config = LifeLuck.get().getConfig();
    public TopLuckCmd(LifeLuck plugin, MainMenu menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("topluck")){

            if (!(sender instanceof Player)){
                sender.sendMessage(Objects.requireNonNull(MessageUtil.parseColors(config.getString("prefix") + config.getString("lang.onlyplayer"))));
            }

            Player player = (Player) sender;

            if (!player.hasPermission("lifeluck.topluck")){
                player.sendMessage(MessageUtil.parseColors(config.getString("prefix") + config.getString("lang.noperm")));
                return false;
            }

            menuManager.openMenu(player, 1);
        }
        return false;
    }
}
