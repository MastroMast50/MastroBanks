package me.mastromast50.mastroBanks.utils;

import me.mastromast50.mastroBanks.MastroBanks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericUtils {
    public final MastroBanks plugin;

    public GenericUtils(MastroBanks plugin) {
        this.plugin = plugin;
    }

    public void addBanknotes(double value, Player player)
    {

        String materialname = plugin.getConfig().getString("banknotes.item");
        ItemStack item = new ItemStack(Material.getMaterial(materialname));
        int customModelData;
        switch((int) value)
        {
            case 10:
                customModelData=plugin.getConfig().getInt("banknotes.customModelData10");
                break;
                case 20:
                    customModelData=plugin.getConfig().getInt("banknotes.customModelData20");
                    break;
                    case 50:
                        customModelData=plugin.getConfig().getInt("banknotes.customModelData50");
                        break;
                        case 100:
                            customModelData=plugin.getConfig().getInt("banknotes.customModelData100");
                            break;
                            case 200:
                                customModelData=plugin.getConfig().getInt("banknotes.customModelData200");
                                break;
                                case 500:
                                    customModelData=plugin.getConfig().getInt("banknotes.customModelData500");
                                    break;
                                    default:
                                        return;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBanconota da &f" + value +"&e€"));
        lore.add(ChatColor.translateAlternateColorCodes('&',"&8Erogata da &lBANCA CENTRALE"));
        meta.setLore(lore);
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }
    public void addCredentials(Player player, String tipologia, String pin)
    {
        Material material = Material.valueOf(plugin.getConfig().getString("credit.item"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&aCarta di credito di &f"+player.getName()));
        lore.add(ChatColor.translateAlternateColorCodes('&',"&8Emanata dalla &lBANCA CENTRALE\n"));
        lore.add(ChatColor.translateAlternateColorCodes('&',"&8Tipologia conto: &e"+tipologia));
        meta.setCustomModelData(plugin.getConfig().getInt("credit.customModelData-creditCard"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        ItemStack libro = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta metaLibro = (BookMeta) libro.getItemMeta();
        metaLibro.setAuthor("MastroBanks");
        metaLibro.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&9Credenziali bancarie di "+ player.getName()));
        metaLibro.addPage(ChatColor.translateAlternateColorCodes('&',"&aIl &l&nPIN &a del tuo conto è:&8&l" + pin));
        libro.setItemMeta(metaLibro);
        player.getInventory().addItem(libro);
    }

    public String generatePin(int length) {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();

        for (int i = 0; i < length; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    public ItemStack itemBanknotes(double value) {
        String materialname = plugin.getConfig().getString("banknotes.item");
        ItemStack item = new ItemStack(Material.getMaterial(materialname));
        int customModelData = 0;
        switch ((int) value) {
            case 10:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData10");
                break;
            case 20:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData20");
                break;
            case 50:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData50");
                break;
            case 100:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData100");
                break;
            case 200:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData200");
                break;
            case 500:
                customModelData = plugin.getConfig().getInt("banknotes.customModelData500");
                break;
            default:
                break;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBanconota da &f" + value + "&e€"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8Erogata da &lBANCA CENTRALE"));
        meta.setLore(lore);
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }
}
