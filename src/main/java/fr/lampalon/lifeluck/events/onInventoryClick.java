package fr.lampalon.lifeluck.events;

import fr.lampalon.lifeluck.LifeLuck;
import fr.lampalon.lifeluck.data.config.MainConfig;
import fr.lampalon.lifeluck.gui.MainMenu;
import fr.lampalon.lifeluck.utils.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

public class onInventoryClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title == null || !title.equals(ChatColor.GRAY + "Main Menu")) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
            if (skullMeta == null || skullMeta.getOwningPlayer() == null) return;

            Player targetPlayer = Bukkit.getPlayer(Objects.requireNonNull(skullMeta.getOwningPlayer().getName()));
            if (targetPlayer == null || !targetPlayer.isOnline()) return;

            openSecondMenu(player, targetPlayer);
            event.setCancelled(true);
        }
    }

    Material material;
    private void openSecondMenu(Player player, Player targetPlayer) {
        FileConfiguration config = LifeLuck.get().getConfig();
        Inventory secondMenu = Bukkit.createInventory(player, config.getInt("submenu.slots"), Objects.requireNonNull(config.getString("submenu.title")));

        ItemStack discreetWarningButton = new ItemStack(Material.valueOf(config.getString("submenu.warn.material")));
        ItemMeta discreetWarningMeta = discreetWarningButton.getItemMeta();
        discreetWarningMeta.setDisplayName(config.getString("submenu.warn.title"));
        discreetWarningButton.setItemMeta(discreetWarningMeta);

        ItemStack banButton = new ItemStack(Material.valueOf(config.getString("submenu.ban.material")));
        ItemMeta banMeta = banButton.getItemMeta();
        banMeta.setDisplayName(config.getString("submenu.ban.title"));
        banButton.setItemMeta(banMeta);

        secondMenu.setItem(0, discreetWarningButton);
        secondMenu.setItem(1, banButton);

        player.openInventory(secondMenu);
    }

    @EventHandler
    public void onSecondMenuClick(InventoryClickEvent event) {
        FileConfiguration config = LifeLuck.get().getConfig();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        String title = event.getView().getTitle();
        if (topInventory == null || !title.equals(config.getString("submenu.title"))) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta != null && itemMeta.hasDisplayName()) {
            String displayName = ChatColor.stripColor(itemMeta.getDisplayName());
            if (displayName.equals(config.getString("submenu.warn.title"))) {
                handleWarnItem(player);
                event.setCancelled(true);
            }
        }
    }

    private void handleWarnItem(Player player) {
        FileConfiguration config = LifeLuck.get().getConfig();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(config.getString("submenu.warn.action.message")));
    }
}