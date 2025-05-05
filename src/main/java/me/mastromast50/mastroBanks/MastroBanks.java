package me.mastromast50.mastroBanks;

import me.mastromast50.mastroBanks.commandListener.ATMListener;
import me.mastromast50.mastroBanks.commandListener.DeleteListener;
import me.mastromast50.mastroBanks.commandListener.SetATMCommand;
import me.mastromast50.mastroBanks.commands.ContoGestione;
import me.mastromast50.mastroBanks.commands.WithdrawCommand;
import me.mastromast50.mastroBanks.playerListener.PlayerListener;
import me.mastromast50.mastroBanks.utils.ATMManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MastroBanks extends JavaPlugin {
    private Economy econ= null;
    private ATMManager atmManager;

    private static MastroBanks plugin;
    private File database=null;
    private FileConfiguration config;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void createDatabase()
    {
        database = new File(getDataFolder(), "database.yml");
        if(!database.exists())
        {
            saveResource("database.yml", false);
        }
    }

    private void loadDatabase()
    {
        config = YamlConfiguration.loadConfiguration(database);
    }

    public void saveDatabaseConfig() {
        try {
            if (config != null) {
                config.save(database);
            }
        } catch (Exception e) {
            getLogger().warning("Impossibile salvare il file database.yml");
            e.printStackTrace();
        }
    }

    public void addPlayerData(Player player, double money, String conto, String pin) {
        FileConfiguration db = getDatabaseConfig();
        String path = "uuid." + player.getUniqueId().toString() + ".dat";
        if (!db.contains(path)) {
            db.set(path + ".nome", player.getName());
            db.set(path + ".conto", conto);
            db.set(path + ".saldo", money);
            db.set(path + ".pin", pin);
            saveDatabaseConfig();
        }
    }
    public void deletePlayerData(Player player) {
        FileConfiguration db = getDatabaseConfig();
        String path = "uuid." + player.getUniqueId().toString();
        if (db.contains(path)) {
            db.set(path, null);
            saveDatabaseConfig();
            getLogger().info("Dati per il giocatore " + player.getName() + " rimossi dal database.");
        } else {
            getLogger().info("Nessun dato trovato per il giocatore " + player.getName() + ".");
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false); 
        }

        reloadConfig();

        createDatabase();
        loadDatabase();
        this.atmManager = new ATMManager(this);
        registerCommands("withdraw", new WithdrawCommand(this));
        registerCommands("conto", new ContoGestione(this));
        registerCommands("confirmdelete", new DeleteListener(this));
        this.getCommand("setatm").setExecutor(new SetATMCommand(atmManager));
        getServer().getPluginManager().registerEvents(new ATMListener(this, atmManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disattivato Vault o Essentials non trovati!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }


    public Economy getEconomy() {
        return econ;
    }


    @Override
    public void onDisable() {

    }

    public void registerCommands(String name, TabExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            return;
        }
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    public FileConfiguration getDatabaseConfig() {
        return config;
    }

}
