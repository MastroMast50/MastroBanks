package me.mastromast50.mastroBanks.commandListener;

import me.mastromast50.mastroBanks.MastroBanks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeleteListener implements TabExecutor {

    private final MastroBanks plugin;

    public DeleteListener(MastroBanks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;

        if (player.hasMetadata("delete_pending") && player.getMetadata("delete_pending").get(0).asBoolean()) {

            player.removeMetadata("delete_pending", plugin);
            plugin.deletePlayerData(player);
            Economy economy = plugin.getEconomy();
            if(economy.getBalance(player) == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl tuo saldo era senza uno spiccio!"));
                return true;
            }
            Double balance = economy.getBalance(player);
            economy.withdrawPlayer(player, balance);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c&nIl tuo conto è stato eliminato &ndefinitivamente"));
        }
        else
        {
            player.sendMessage(ChatColor.GREEN + "Non hai richieste di eliminazione del conto!");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
