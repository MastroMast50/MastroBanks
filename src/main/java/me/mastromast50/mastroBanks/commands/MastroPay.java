package me.mastromast50.mastroBanks.commands;

import me.mastromast50.mastroBanks.MastroBanks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MastroPay implements TabExecutor {
    private final MastroBanks plugin;
    private final Economy economy;

    public MastroPay(MastroBanks plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("mastrobanks.admin")) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.RED + "Non hai i permessi per eseguire il comando!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Utilizzo: /mastropay <player> <denaro>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Giocatore non trovato o offline!");
            return true;
        }

        try {
            double amount = Double.parseDouble(args[1]);

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "L'importo deve essere maggiore di 0!");
                return true;
            }

            String accountType = plugin.getDatabaseConfig().getString("uuid." + target.getUniqueId() + ".dat.conto", "DEFAULT");
            double limit = plugin.getConfig().getDouble("limit-count." + accountType, 1000.0);
            double newBalance = economy.getBalance(target) + amount;

            if (newBalance > limit) {
                sender.sendMessage(ChatColor.RED + "Errore: Il conto di " + target.getName() + " raggiungerebbe il limite massimo (" + limit + ")");
                target.sendMessage(ChatColor.RED+"Attenzione! Qualcuno sta cercando di inviarti "+amount+"€ ma il tuo conto ha raggiunto il limite massimo (" + limit + ")");
                return true;
            }

            economy.depositPlayer(target, amount);
            updateBalance(target);

            String formattedAmount = economy.format(amount);
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.GREEN + "[Console] Hai inviato " + formattedAmount + " a " + target.getName());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Hai inviato " + formattedAmount + " a " + target.getName());
            }

            target.sendMessage(ChatColor.GREEN + "Hai ricevuto " + formattedAmount + " sul tuo conto!");

        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Importo non valido! Inserisci un numero.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Errore durante il trasferimento: " + e.getMessage());
            plugin.getLogger().severe("Errore in MastroPay: " + e.getMessage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
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