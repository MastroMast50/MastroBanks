package me.mastromast50.mastroBanks.commands;

import me.mastromast50.mastroBanks.MastroBanks;
import me.mastromast50.mastroBanks.utils.GenericUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ContoGestione implements TabExecutor {

    MastroBanks plugin;

    public ContoGestione(MastroBanks plugin) {
        this.plugin = plugin;
    }

        List<String> tipiValide = Arrays.asList("DEFAULT", "IRON", "GOLD", "DIAMOND");


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(commandSender instanceof Player player) {

            if(strings.length == 0)
            {
                player.sendMessage(ChatColor.RED + "Sintassi non valida! Utilizza: /conto crea|info|elimina <giocatore>" );
                return true;
            }

            switch (strings[0]) {
                case "crea":
                    if(!player.hasPermission("mastrobanks.banchiere"))
                    {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cMi dispiace questo comando è riservato ai banchieri"));
                        return true;
                    }
                    if(strings.length != 3)
                    {
                        player.sendMessage(ChatColor.RED + "Sintassi non valida! usa /conto crea <giocatore> <tipologia>" );
                        return true;
                    }

                    String giocatore = strings[1];
                    if(Bukkit.getPlayer(giocatore) != null) {
                        Player Target = Bukkit.getPlayer(giocatore);
                        if(!tipiValide.contains(strings[2]))
                        {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cI tipi di conto possono essere: &lDEFAULT - IRON - GOLD - DIAMOND"));
                            return true;
                        }
                        if(plugin.getDatabaseConfig().contains("uuid."+Target.getUniqueId().toString())) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl giocatore gia possiede un conto di tipo: &f"+plugin.getDatabaseConfig().getString("uuid." + Target.getUniqueId().toString() + ".dat.conto")));
                            return true;
                        }

                        GenericUtils utils = new GenericUtils(plugin);
                        String PIN = utils.generatePin(4);
                        plugin.addPlayerData(Target,0,strings[2],PIN);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eHai creato con successo un conto &f"+ strings[2]+ " &ea &f&n"+giocatore));
                        Target.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eIl banchiere &f "+player.getName()+" &eti ha creato un conto &f"+ strings[2]));
                        utils.addCredentials(Target,strings[2],PIN);
                    }
                    else

                    {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl giocatore: &f" + giocatore+" &cnon è online o non esiste"));
                        return true;
                    }

                    break;
                    case "info":
                        String giocatoreinfo = strings[1];
                        if(Bukkit.getPlayer(giocatoreinfo) != null) {
                            Player Target = Bukkit.getPlayer(giocatoreinfo);
                            if(!plugin.getDatabaseConfig().contains("uuid."+Target.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl giocatore non ha un conto bancario"));
                                return true;
                            }
                            if(player.hasPermission("mastrobanks.banchiere") || Target.getName().equals(player.getName()))
                            {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c&lINFO CONTO - &f"+giocatoreinfo));
                                String conto= plugin.getDatabaseConfig().getString("uuid." + Target.getUniqueId().toString() + ".dat.conto");
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eTipologia :&f "+conto));
                                switch(conto)
                                {
                                    case "DEFAULT":
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DEFAULT")));
                                        break;

                                        case "IRON":
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.IRON")));
                                            break;

                                    case "GOLD":
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.GOLD")));
                                        break;
                                        case "DIAMOND":
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSaldo:&f " + plugin.getDatabaseConfig().getDouble("uuid." + player.getUniqueId().toString() + ".dat.saldo") +"&e/&f"+ plugin.getConfig().getDouble("limit-count.DIAMOND")));
                                            break;
                                            default:
                                                player.sendMessage(ChatColor.RED + "Errore nel file di configurazione");
                                                return true;
                                }
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&ePIN: &f"+ plugin.getDatabaseConfig().getString("uuid." + Target.getUniqueId().toString() + ".dat.pin")));
                                return true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&CSolo un banchiere o il proprietario del conto puo verificare le coordinate bancarie di :&f "+giocatoreinfo));
                                return true;
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl giocatore: &f" + giocatoreinfo+" &cnon è online o non esiste"));
                            return true;
                        }
                case "elimina":
                    String giocatoreelimina = strings[1];
                    if(!player.hasPermission("mastrobanks.banchiere")) return true;
                    if(Bukkit.getPlayer(giocatoreelimina) != null) {
                        Player Target = Bukkit.getPlayer(giocatoreelimina);
                        if(!plugin.getDatabaseConfig().contains("uuid."+Target.getUniqueId().toString())) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cIl giocatore non ha un conto bancario"));
                            return true;
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eStai per eliminare il conto &f "+plugin.getDatabaseConfig().getString("uuid." + Target.getUniqueId().toString() + ".dat.conto")+ "&e   di &f"+giocatoreelimina));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eLa &lBANCA CENTRALE &eha inviato una richiesta di eliminazione conto a &f"+giocatoreelimina));
                        TextComponent eliminaconto= new TextComponent(ChatColor.translateAlternateColorCodes('&',"&eIl banchiere &f"+player.getName()+" &esta per eliminarti il conto...\n"));
                        TextComponent clicca=new TextComponent(ChatColor.translateAlternateColorCodes('&',"&f&nClicca qua per confermare l'eliminazione"));
                        clicca.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirmdelete"));
                        clicca.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(ChatColor.translateAlternateColorCodes('&',"&bClicca qua per confermare l'eliminazione"))));

                        BaseComponent[] messaggio = new BaseComponent[]{eliminaconto,clicca};
                        Target.spigot().sendMessage(messaggio);

                        Target.setMetadata("delete_pending", new FixedMetadataValue(plugin, true));
                        if (!(Target.hasMetadata("delete_pending") && Target.getMetadata("delete_pending").get(0).asBoolean()))
                        {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eConto &neliminato&e con &lSUCCESSO"));
                            return true;
                        }
                        return true;
                    } else
                    {
                        player.sendMessage(ChatColor.RED + "Il giocatore non è online o non esiste");
                    }
                case "list":

                    if (!player.hasPermission("mastrobanks.banchiere")) {
                        player.sendMessage(ChatColor.RED + "Non hai i permessi per vedere la lista dei conti bancari.");
                        return true;
                    }


                    List<String> conti = new ArrayList<>();
                    for (String uuid : plugin.getDatabaseConfig().getConfigurationSection("uuid").getKeys(false)) {
                        if (plugin.getDatabaseConfig().contains("uuid." + uuid + ".dat.conto")) {
                            String nomeGiocatore = Bukkit.getPlayer(UUID.fromString(uuid)) != null ? Bukkit.getPlayer(UUID.fromString(uuid)).getName() : "Giocatore offline";
                            String tipoConto = plugin.getDatabaseConfig().getString("uuid." + uuid + ".dat.conto");
                            conti.add(nomeGiocatore + " - " + tipoConto);
                        }
                    }

                    if (conti.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "Non ci sono giocatori con conti bancari.");
                        return true;
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLista dei giocatori con conto bancario:"));
                      for (String conto : conti) {
                          player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f" + conto));
                        }
                    return true;
                    default:
                        return true;
            }
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(strings.length == 1)
        {
            return Arrays.asList("crea", "info", "elimina", "list");
        }

        if(strings.length == 2)
        {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(strings[1].toLowerCase()))
                    .toList();
        }
        if (strings.length == 3 && strings[0].equalsIgnoreCase("crea")) {

            return tipiValide.stream()
                    .filter(tipo -> tipo.toLowerCase().startsWith(strings[2].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
