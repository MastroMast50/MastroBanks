package me.mastromast50.mastroBanks.commandListener;

import me.mastromast50.mastroBanks.MastroBanks;
import me.mastromast50.mastroBanks.utils.ATMManager;
import me.mastromast50.mastroBanks.utils.ATMUtils;
import me.mastromast50.mastroBanks.utils.GenericUtils;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class ATMListener implements Listener {
    private MastroBanks plugin;
    private final ATMManager atmManager;
    public ATMListener(MastroBanks plugin, ATMManager atmManager) {
        this.plugin = plugin;
        this.atmManager = atmManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if(event.getClickedBlock() == null ||
                event.getClickedBlock().getType() == null ||
                (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) ||
                event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!atmManager.isATMBlock(event.getClickedBlock())) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        String materialName = plugin.getConfig().getString("credit.item", "PAPER");
        Material creditCardMaterial = Material.getMaterial(materialName);
        if(creditCardMaterial == null) creditCardMaterial = Material.PAPER;

        if(itemInHand == null || itemInHand.getType() != creditCardMaterial) {
            player.sendMessage(ChatColor.RED + "Devi avere in mano una carta di credito!");
            return;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        String expectedName = ChatColor.translateAlternateColorCodes('&',
                "&aCarta di credito di &f" + player.getName());

        if(meta == null || !meta.hasDisplayName() || !meta.getDisplayName().equals(expectedName)) {
            player.sendMessage(ChatColor.RED + "Carta di credito non valida!");
            return;
        }


        if(!plugin.getDatabaseConfig().contains("uuid." + player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Non hai un conto bancario!");
            return;
        }

        openAnvilGUI(player);
    }

    public void openAnvilGUI(Player player) {
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    //Aggiungere Suono;
                })
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    if(stateSnapshot.getText().equalsIgnoreCase(plugin.getDatabaseConfig().getString("uuid." + player.getUniqueId().toString() + ".dat.pin"))) {
                        stateSnapshot.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&eHai inserito correttamente il tuo &l&nPIN"));
                        openBank(player);
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } else {
                        stateSnapshot.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&c&lERRORE!&c Hai inserito un pin &nerrato"));
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("PIN ERRATO!"),AnvilGUI.ResponseAction.close());
                    }

                })
                .text("Pin..")
                .title("Inserisci il PIN")
                .plugin(plugin)
                .open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null) return;

        if (event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&8&lDISPOSITIVO ATM")) || event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&8&lPRELEVA DENARO")) || event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&8&lDEPOSITA DENARO"))) {

            event.setCancelled(true);
            player.updateInventory();

            ItemStack clickeditem = event.getCurrentItem();
            if (clickeditem == null || clickeditem.getType() == Material.AIR) return;

            String displayname = clickeditem.getItemMeta().getDisplayName();
            if (displayname.contains((ChatColor.RED + "Preleva Soldi"))) {
                player.closeInventory();
                openPreleva(player);
            }

            if (displayname.contains(ChatColor.translateAlternateColorCodes('&', "&eDeposita Soldi"))) {
                player.closeInventory();
                openDeposita(player);
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c10€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(10);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 10);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig(); // IMPORTANTE: salva le modifiche

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 10€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 10€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c20€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(20);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 20);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 20€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 20€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c50€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(50);
                Economy economy = plugin.getEconomy();


                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 50);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 50€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 50€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c50€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(50);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 50);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 50€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 50€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c100€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(100);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 100);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 100€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 100€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c200€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(200);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 200);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 200€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 200€ nel tuo inventario!");
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&c500€")))) {
                Inventory inventory = player.getInventory();
                GenericUtils utils = new GenericUtils(plugin);
                ItemStack item = utils.itemBanknotes(500);
                Economy economy = plugin.getEconomy();

                for(int i = 0; i < inventory.getSize(); i++) {
                    ItemStack currentItem = inventory.getItem(i);
                    if(currentItem != null && currentItem.isSimilar(item)) {
                        String accountType = checkCount(player);
                        double limit = plugin.getConfig().getDouble("limit-count." + accountType);

                        if(economy.getBalance(player) >= limit) {
                            player.sendMessage(ChatColor.RED + "Hai raggiunto il limite massimo per il tuo tipo di conto!");
                            return;
                        }

                        currentItem.setAmount(currentItem.getAmount() - 1);
                        if(currentItem.getAmount() <= 0) {
                            inventory.setItem(i, null);
                        }

                        economy.depositPlayer(player, 500);

                        String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                        plugin.saveDatabaseConfig();

                        player.sendMessage(ChatColor.GREEN + "Hai depositato 500€");
                        return;
                    }
                }
                player.sendMessage(ChatColor.RED + "Non hai banconote da 500€ nel tuo inventario!");
            }



            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a10€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 10) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 10);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(10, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n10€"));
            }


            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a20€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 20) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 20);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(20, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n20€"));
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a50€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 50) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 50);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(50, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n50€"));
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a100€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 100) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 100);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(100, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n100€"));
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a200€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 200) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 200);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(200, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n200€"));
            }

            if (displayname.contains((ChatColor.translateAlternateColorCodes('&', "&a500€")))) {
                Economy economy = plugin.getEconomy();
                if (economy.getBalance(player) < 500) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNon hai abbastanza soldi"));
                    return;
                }

                economy.withdrawPlayer(player, 500);

                String path = "uuid." + player.getUniqueId().toString() + ".dat.saldo";
                plugin.getDatabaseConfig().set(path, economy.getBalance(player));
                plugin.saveDatabaseConfig();

                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(500, player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eHai prelevato &n10€"));
            }

            if (displayname.contains((ChatColor.RED + "Chiudi ATM"))) {
                player.closeInventory();
                return;
            }
        }
    }
    
    public void openBank(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&',"&8&lDISPOSITIVO ATM"));
        ATMUtils atm = new ATMUtils(plugin, player, inventory);
        atm.SetupInventory();
        player.openInventory(inventory);
    }

    public void openPreleva(Player player) {
        Inventory prelevainventory = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&',"&8&lPRELEVA DENARO"));
        ATMUtils atm = new ATMUtils(plugin, player, prelevainventory);
        atm.SetupInventoryPreleva();
        player.openInventory(prelevainventory);
    }

    public void openDeposita(Player player) {
        Inventory depositainventory = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&',"&8&lDEPOSITA DENARO"));
        ATMUtils atm = new ATMUtils(plugin, player, depositainventory);
        atm.setupInventoryDeposita();
        player.openInventory(depositainventory);
    }

    public String checkCount(Player player) {
        String conto = plugin.getDatabaseConfig().getString("uuid." + player.getUniqueId().toString() + ".dat.conto");
        return conto;
    }
}
