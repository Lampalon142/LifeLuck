package fr.lampalon.lifeluck.events;

import fr.lampalon.lifeluck.LifeLuck;
import fr.lampalon.lifeluck.utils.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Date;
import java.util.Objects;

public class onInventoryClick implements Listener {
    private Messages messages;
    private Player targetPlayer;
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        FileConfiguration config = LifeLuck.get().getConfig();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title == null || !title.equals(ChatColor.translateAlternateColorCodes('&', config.getString("menu.title")))) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
            if (skullMeta == null || skullMeta.getOwningPlayer() == null) return;

            targetPlayer = Bukkit.getPlayer(Objects.requireNonNull(skullMeta.getOwningPlayer().getName()));
            if (targetPlayer == null || !targetPlayer.isOnline()) return;

            openSecondMenu(player, targetPlayer);
            event.setCancelled(true);
        }
    }
    private void openSecondMenu(Player player, Player targetPlayer) {
        FileConfiguration config = LifeLuck.get().getConfig();
        Inventory secondMenu = Bukkit.createInventory(player, config.getInt("submenu.slots"), ChatColor.translateAlternateColorCodes('&', config.getString("submenu.title")));

        ItemStack discreetWarningButton = new ItemStack(Material.valueOf(config.getString("submenu.warn.material")));
        ItemMeta discreetWarningMeta = discreetWarningButton.getItemMeta();
        discreetWarningMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.warn.title")));
        discreetWarningButton.setItemMeta(discreetWarningMeta);

        ItemStack banButton = new ItemStack(Material.valueOf(config.getString("submenu.ban.material")));
        ItemMeta banMeta = banButton.getItemMeta();
        banMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.ban.title")));
        banButton.setItemMeta(banMeta);

        secondMenu.setItem(0, discreetWarningButton);
        secondMenu.setItem(1, banButton);

        player.openInventory(secondMenu);
    }

    @EventHandler
    public void onSecondMenuClick(InventoryClickEvent event) {
        FileConfiguration config = LifeLuck.get().getConfig();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        String title = event.getView().getTitle();

        if (topInventory == null || !title.equals(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.title")))) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName()) {
            return;
        }

        String displayName = MessageUtil.parseColors(itemMeta.getDisplayName());

        String warnTitle = ChatColor.translateAlternateColorCodes('&', config.getString("submenu.warn.title"));
        String banTitle = ChatColor.translateAlternateColorCodes('&', config.getString("submenu.ban.title"));

        if (displayName.equalsIgnoreCase(warnTitle)) {
            handleWarnItem(player);
            event.setCancelled(true);
        } else if (displayName.equalsIgnoreCase(banTitle)) {
            Player targetPlayer = this.targetPlayer;
            handleBanItem(player, targetPlayer);
            event.setCancelled(true);
        } else {
            System.out.println("Clicked item does not match any expected options.");
        }
    }


    private void handleWarnItem(Player player) {
        FileConfiguration config = LifeLuck.get().getConfig();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.warn.action.message"))));
    }
    private void handleBanItem(Player player, Player targetPlayer) {
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.offline));
            return;
        }

        FileConfiguration config = LifeLuck.get().getConfig();
        String banTime = config.getString("submenu.ban.action.time");

        if (banTime == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.bantimeno));
            return;
        }

        long durationTicks = parseDuration(banTime) * 20;

        if (durationTicks <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "Invalid ban duration."));
            return;
        }

        if (!targetPlayer.getPlayer().isBanned()) {
            Bukkit.getBanList(BanList.Type.NAME).addBan(targetPlayer.getName(), ChatColor.translateAlternateColorCodes('&', config.getString("submenu.ban.action.message")), new Date(System.currentTimeMillis() + durationTicks), null);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.ban.action.chat")));
            targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&', config.getString("submenu.ban.action.message")));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.playerbanned));
        }
    }

    private long parseDuration(String durationString) {
        if (durationString == null) {
            return 0;
        }

        long durationTicks = 0;
        try {
            char timeUnit = durationString.charAt(durationString.length() - 1);
            int durationAmount = Integer.parseInt(durationString.substring(0, durationString.length() - 1));

            switch (timeUnit) {
                case 's':
                    durationTicks = durationAmount * 20;
                    break;
                case 'm':
                    durationTicks = durationAmount * 60 * 20;
                    break;
                case 'h':
                    durationTicks = durationAmount * 60 * 60 * 20;
                    break;
                case 'd':
                    durationTicks = durationAmount * 24 * 60 * 60 * 20;
                    break;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return durationTicks;
    }
}