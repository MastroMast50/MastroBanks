package me.mastromast50.mastroBanks.commands;

import me.mastromast50.mastroBanks.MastroBanks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BonificoCommand implements TabExecutor {
    private final MastroBanks plugin;
    private Economy economy;

    public BonificoCommand(MastroBanks plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i giocatori possono usare questo comando!");
            return true;
        }

        Player player = (Player) sender;

        if (economy == null) {
            player.sendMessage(ChatColor.RED + "Errore nel sistema economico. Riprova più tardi.");
            plugin.getLogger().severe("Vault economy non inizializzata!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Utilizzo: /bonifico <giocatore> <importo>");
            return true;
        }

        if (!plugin.getDatabaseConfig().contains("uuid." + player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Non hai un conto bancario!");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            player.sendMessage(ChatColor.RED + "Giocatore non trovato!");
            return true;
        }

        if (!plugin.getDatabaseConfig().contains("uuid." + target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + target.getName() + " non ha un conto bancario!");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "L'importo deve essere positivo!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Importo non valido!");
            return true;
        }

        if (economy.getBalance(player) < amount) {
            player.sendMessage(ChatColor.RED + "Fondi insufficienti!");
            return true;
        }

        String targetAccountType = plugin.getDatabaseConfig().getString("uuid." + target.getUniqueId() + ".dat.conto", "DEFAULT");
        double targetLimit = plugin.getConfig().getDouble("limit-count." + targetAccountType, 1000.0);
        double targetBalance = economy.getBalance(target);

        if (targetBalance + amount > targetLimit) {
            double maxAllowed = targetLimit - targetBalance;
            player.sendMessage(ChatColor.RED + "Il trasferimento supererebbe il limite del conto destinatario!");
            player.sendMessage(ChatColor.RED + "Massimo che puoi inviare: " + maxAllowed + "€");
            return true;
        }

        economy.withdrawPlayer(player, amount);
        economy.depositPlayer(target, amount);

        updateBalance(player);
        updateBalance(target);

        player.sendMessage(ChatColor.GREEN + "Hai inviato " + amount + "€ a " + target.getName());
        if (target.isOnline()) {
            target.getPlayer().sendMessage(ChatColor.GREEN + "Hai ricevuto " + amount + "€ da " + player.getName());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList()));
        }

        return completions;
    }

    private void updateBalance(OfflinePlayer player) {
        String path = "uuid." + player.getUniqueId() + ".dat.saldo";
        plugin.getDatabaseConfig().set(path, economy.getBalance(player));
        plugin.saveDatabaseConfig();
    }
}