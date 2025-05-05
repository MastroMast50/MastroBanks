package me.mastromast50.mastroBanks.utils;

import me.mastromast50.mastroBanks.MastroBanks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ATMUtils {
    private Inventory inventory;
    private MastroBanks plugin;
    private Player player;

    public ATMUtils(MastroBanks plugin, Player player, Inventory inventory) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = inventory;
    }

    public void SetupInventory() {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bConto di &f" + player.getName()));
        ArrayList<String> lore = new ArrayList<>();

        String conto = plugin.getDatabaseConfig().getString("uuid." + player.getUniqueId().toString() + ".dat.conto");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eTipologia: &f" + conto + "\n"));

        switch(conto) {
            case "DEFAULT":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DEFAULT")));
                break;
            case "IRON":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.IRON")));
                break;
            case "GOLD":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.GOLD")));
                break;
            case "DIAMOND":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DIAMOND")));
                break;
            default:
                player.sendMessage(ChatColor.RED + "Errore nel file di configurazione");
                return;
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(0, item);

        ItemStack depositItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta depositMeta = depositItem.getItemMeta();
        depositMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eDeposita Soldi"));
        ArrayList<String> depositLore = new ArrayList<>();
        depositLore.add(ChatColor.translateAlternateColorCodes('&', "&7&nClicca per depositare"));
        depositMeta.setLore(depositLore);
        depositItem.setItemMeta(depositMeta);
        inventory.setItem(3, depositItem); 

        ItemStack preleva = new ItemStack(Material.RED_CONCRETE);
        ItemMeta prelevaMeta = preleva.getItemMeta();
        prelevaMeta.setDisplayName(ChatColor.RED + "Preleva Soldi");
        ArrayList<String> prelevaLore = new ArrayList<>();
        prelevaLore.add(ChatColor.translateAlternateColorCodes('&', "&7&nClicca per prelevare"));
        prelevaMeta.setLore(prelevaLore);
        preleva.setItemMeta(prelevaMeta);
        inventory.setItem(5, preleva);  

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Chiudi ATM");
        close.setItemMeta(closeMeta);
        inventory.setItem(8, close);  
    }

    public void SetupInventoryPreleva() {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(ChatColor.WHITE + "");
        glass.setItemMeta(glassMeta);

        ItemStack preleva10 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta10 = preleva10.getItemMeta();
        prelevaMeta10.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a10€"));
        ArrayList<String> prelevaLore = new ArrayList<>();
        prelevaLore.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta10.setLore(prelevaLore);
        prelevaMeta10.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData10"));
        preleva10.setItemMeta(prelevaMeta10);

        ItemStack preleva20 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta20 = preleva20.getItemMeta();
        prelevaMeta20.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a20€"));
        ArrayList<String> prelevaLore20 = new ArrayList<>();
        prelevaLore20.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta20.setLore(prelevaLore);
        prelevaMeta20.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData20"));
        preleva20.setItemMeta(prelevaMeta20);

        ItemStack preleva50 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta50 = preleva50.getItemMeta();
        prelevaMeta50.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a50€"));
        ArrayList<String> prelevaLore50 = new ArrayList<>();
        prelevaLore50.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta50.setLore(prelevaLore);
        prelevaMeta50.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData50"));
        preleva50.setItemMeta(prelevaMeta50);

        ItemStack preleva100 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta100 = preleva100.getItemMeta();
        prelevaMeta100.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a100€"));
        ArrayList<String> prelevaLore100 = new ArrayList<>();
        prelevaLore100.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta100.setLore(prelevaLore);
        prelevaMeta100.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData100"));
        preleva100.setItemMeta(prelevaMeta100);

        ItemStack preleva200 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta200 = preleva200.getItemMeta();
        prelevaMeta200.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a200€"));
        ArrayList<String> prelevaLore200 = new ArrayList<>();
        prelevaLore200.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta200.setLore(prelevaLore);
        prelevaMeta200.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData200"));
        preleva200.setItemMeta(prelevaMeta200);

        ItemStack preleva500 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta500 = preleva500.getItemMeta();
        prelevaMeta500.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a500€"));
        ArrayList<String> prelevaLore500 = new ArrayList<>();
        prelevaLore500.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per prelevare"));
        prelevaMeta500.setLore(prelevaLore);
        prelevaMeta500.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData500"));
        preleva500.setItemMeta(prelevaMeta500);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bConto di &f" + player.getName()));
        ArrayList<String> lore = new ArrayList<>();

        String conto = plugin.getDatabaseConfig().getString("uuid." + player.getUniqueId().toString() + ".dat.conto");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eTipologia: &f" + conto + "\n"));

        switch(conto) {
            case "DEFAULT":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DEFAULT")));
                break;
            case "IRON":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.IRON")));
                break;
            case "GOLD":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.GOLD")));
                break;
            case "DIAMOND":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DIAMOND")));
                break;
            default:
                player.sendMessage(ChatColor.RED + "Errore nel file di configurazione");
                return;
        }

        meta.setLore(lore);
        item.setItemMeta(meta);


        inventory.setItem(1, preleva10);
        inventory.setItem(2, preleva20);
        inventory.setItem(3, preleva50);
        inventory.setItem(4, preleva100);
        inventory.setItem(5, preleva200);
        inventory.setItem(6, preleva500);
        inventory.setItem(0, glass);
        inventory.setItem(7, glass);
        inventory.setItem(8, item);
    }

    public void setupInventoryDeposita() {

        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(ChatColor.WHITE + "");
        glass.setItemMeta(glassMeta);

        ItemStack preleva10 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta10 = preleva10.getItemMeta();
        prelevaMeta10.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c10€"));
        ArrayList<String> prelevaLore = new ArrayList<>();
        prelevaLore.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta10.setLore(prelevaLore);
        prelevaMeta10.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData10"));
        preleva10.setItemMeta(prelevaMeta10);

        ItemStack preleva20 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta20 = preleva20.getItemMeta();
        prelevaMeta20.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c20€"));
        ArrayList<String> prelevaLore20 = new ArrayList<>();
        prelevaLore20.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta20.setLore(prelevaLore);
        prelevaMeta20.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData20"));
        preleva20.setItemMeta(prelevaMeta20);

        ItemStack preleva50 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta50 = preleva50.getItemMeta();
        prelevaMeta50.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c50€"));
        ArrayList<String> prelevaLore50 = new ArrayList<>();
        prelevaLore50.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta50.setLore(prelevaLore);
        prelevaMeta50.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData50"));
        preleva50.setItemMeta(prelevaMeta50);

        ItemStack preleva100 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta100 = preleva100.getItemMeta();
        prelevaMeta100.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c100€"));
        ArrayList<String> prelevaLore100 = new ArrayList<>();
        prelevaLore100.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta100.setLore(prelevaLore);
        prelevaMeta100.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData100"));
        preleva100.setItemMeta(prelevaMeta100);

        ItemStack preleva200 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta200 = preleva200.getItemMeta();
        prelevaMeta200.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c200€"));
        ArrayList<String> prelevaLore200 = new ArrayList<>();
        prelevaLore200.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta200.setLore(prelevaLore);
        prelevaMeta200.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData200"));
        preleva200.setItemMeta(prelevaMeta200);

        ItemStack preleva500 = new ItemStack(Material.STICK);
        ItemMeta prelevaMeta500 = preleva500.getItemMeta();
        prelevaMeta500.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c500€"));
        ArrayList<String> prelevaLore500 = new ArrayList<>();
        prelevaLore500.add(ChatColor.translateAlternateColorCodes('&',"&8Clicca per depositare"));
        prelevaMeta500.setLore(prelevaLore);
        prelevaMeta500.setCustomModelData(plugin.getConfig().getInt("banknotes.customModelData500"));
        preleva500.setItemMeta(prelevaMeta500);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bConto di &f" + player.getName()));
        ArrayList<String> lore = new ArrayList<>();

        String conto = plugin.getDatabaseConfig().getString("uuid." + player.getUniqueId().toString() + ".dat.conto");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eTipologia: &f" + conto + "\n"));

        switch(conto) {
            case "DEFAULT":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DEFAULT")));
                break;
            case "IRON":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.IRON")));
                break;
            case "GOLD":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.GOLD")));
                break;
            case "DIAMOND":
                lore.add(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DIAMOND")));
                break;
            default:
                player.sendMessage(ChatColor.RED + "Errore nel file di configurazione");
                return;
        }

        meta.setLore(lore);
        item.setItemMeta(meta);


        inventory.setItem(1, preleva10);
        inventory.setItem(2, preleva20);
        inventory.setItem(3, preleva50);
        inventory.setItem(4, preleva100);
        inventory.setItem(5, preleva200);
        inventory.setItem(6, preleva500);
        inventory.setItem(0, glass);
        inventory.setItem(7, glass);
        inventory.setItem(8, item);

    }
}
