package me.mastromast50.mastroBanks.commands;

import me.mastromast50.mastroBanks.MastroBanks;
import me.mastromast50.mastroBanks.utils.GenericUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class WithdrawCommand implements TabExecutor {

    private final MastroBanks plugin ;

    public WithdrawCommand(MastroBanks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player)
        {
            if(strings.length != 1) return true;
            if(!player.hasPermission("mastroBanks.withdraw")) return true;
            Economy economy = plugin.getEconomy();
            if(economy.getBalance(player) < 0)
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl tuo conto è a 0"));
                return true;
            }
            double value = 0;
            try
            {
                value = Double.parseDouble(strings[0]);
            }catch (NumberFormatException e)
            {
                player.sendMessage(ChatColor.RED + "Devi inserire un numero valido");
                return true;
            }
            if(economy.getBalance(player) >= value)
            {
                economy.withdrawPlayer(player,value);
                GenericUtils utils = new GenericUtils(plugin);
                utils.addBanknotes(value,player);
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cNon hai &nabbastanza&c soldi"));
                return true;
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}
