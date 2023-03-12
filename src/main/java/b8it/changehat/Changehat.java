package b8it.changehat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class Changehat extends JavaPlugin implements CommandExecutor {

    private Map<String, Hat> hats = new HashMap<>();
    private int defaultHatId;
    private Material defaultItem;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getCommand("changehat").setExecutor(this);
    }

    private void loadConfig() {
        hats.clear();
        defaultHatId = getConfig().getInt("default-hat-id", 0);
        defaultItem = Material.getMaterial(getConfig().getString("default-item"));
        ConfigurationSection hatsSection = getConfig().getConfigurationSection("hats");
        if (hatsSection != null) {
            for (String key : hatsSection.getKeys(false)) {
                ConfigurationSection hatSection = hatsSection.getConfigurationSection(key);
                int id = hatSection.getInt("id");
                String name = hatSection.getString("name");
                ChatColor renameColor = ChatColor.valueOf(hatSection.getString("rename-color", "WHITE"));
                String permission = hatSection.getString("permission", "");
                hats.put(key.toLowerCase(), new Hat(id, name, renameColor, permission));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changehat")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reloadconfig")) {
                if (!sender.isOp()) {
                    sender.sendMessage("You do not have permission to reload the hat config.");
                    return true;
                }
                reloadConfig();
                loadConfig();
                sender.sendMessage("Config reloaded.");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            String defaultItem = getConfig().getString("default-item");
            if (item.getType() != Material.getMaterial(defaultItem) || item.getItemMeta() == null) {
                player.sendMessage(getConfig().getString("error-sentence"));
                return true;
            }
            if (item.getItemMeta().getCustomModelData() != defaultHatId) {
                player.sendMessage(getConfig().getString("error-sentence"));
                return true;
            }
            if (args.length == 0) {
                List<String> hatNames = new ArrayList<>();
                for (Map.Entry<String, Hat> entry : hats.entrySet()) {
                    Hat hat = entry.getValue();
                    if (player.hasPermission(hat.getPermission())) {
                        hatNames.add(entry.getKey());
                    }
                }
                Collections.sort(hatNames);
                sender.sendMessage("Usage: /changehat <hatname>");
                sender.sendMessage("Available hats: " + StringUtils.join(hatNames, ", "));
                return true;
            }
            String hatName = args[0].toLowerCase();
            Hat hat = hats.get(hatName);
            if (hat == null) {
                sender.sendMessage("Invalid hat name.");
                return true;
            }
            String permission = hat.getPermission();
            if (!player.hasPermission(permission)) {
                sender.sendMessage("You do not have permission to use the " + hatName + " hat.");
                return true;
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(hat.getId());
            itemMeta.setDisplayName(hat.getRenameColor() + hat.getName());
            item.setItemMeta(itemMeta);
            sender.sendMessage("Changed hat to " + hatName + ".");
        }
        return true;
    }




    private class Hat {
        private int id;
        private String name;
        private ChatColor renameColor;
        private String permission;

        public Hat(int id, String name, ChatColor renameColor, String permission) {
            this.id = id;
            this.name = name;
            this.renameColor = renameColor;
            this.permission = permission;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ChatColor getRenameColor() {
            return renameColor;
        }

        public String getPermission() {
            return permission;
        }
    }

}
