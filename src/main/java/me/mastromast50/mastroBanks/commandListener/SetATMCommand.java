package me.mastromast50.mastroBanks.commandListener;

import me.mastromast50.mastroBanks.utils.ATMManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetATMCommand implements CommandExecutor {
    private final ATMManager atmManager;

    public SetATMCommand(ATMManager atmManager) {
        this.atmManager = atmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i giocatori possono usare questo comando!");
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("mastrobanks.admin")) {
            player.sendMessage(ChatColor.RED + "Non hai i permessi per eseguire questa azione");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "Devi guardare un blocco!");
            return true;
        }

        if (atmManager.isATMBlock(targetBlock)) {
            atmManager.removeATMBlock(targetBlock);
            player.sendMessage(ChatColor.GREEN + "Blocco ATM rimosso!");
        } else {
            atmManager.addATMBlock(targetBlock);
            player.sendMessage(ChatColor.GREEN + "Blocco ATM impostato!");
        }

        return true;
    }
}