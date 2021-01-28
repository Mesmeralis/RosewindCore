package me.vividheart.core;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelPlugin;

import java.io.File;

public class Main extends JavaPlugin {
   public Economy econ;
    Chat chat;
    Permission perms;
    SentinelIntegration sentinel;
    @Override
    public void onEnable(){
        getServer().getConsoleSender().sendMessage("vCore v1.0 has been enabled.");
        createConfig();
        createHomes();
        createData();
        setupEconomy();
        setupChat();
        setupPermissions();
        getConfig().options().copyDefaults(true);
        Commands cmd = new Commands(this);
        Events events = new Events(this);
        getCommand("torture").setExecutor(cmd);
        getCommand("fish").setExecutor(cmd);
        getCommand("v").setExecutor(cmd);
        getCommand("glow").setExecutor(cmd);
        getCommand("lock").setExecutor(cmd);
        getCommand("amsg").setExecutor(cmd);
        getCommand("boom").setExecutor(cmd);
        getCommand("tab").setExecutor(cmd);
        getCommand("gm").setExecutor(cmd);
        getCommand("gmc").setExecutor(cmd);
        getCommand("gms").setExecutor(cmd);
        getCommand("help").setExecutor(cmd);
        getCommand("rcore").setExecutor(cmd);
        getCommand("hideall").setExecutor(cmd);
        getCommand("joinsound").setExecutor(cmd);
        getCommand("weather").setExecutor(cmd);
        getCommand("time").setExecutor(cmd);
        getCommand("home").setExecutor(cmd);
        getCommand("sethome").setExecutor(cmd);
        getCommand("homes").setExecutor(cmd);
        getCommand("spawn").setExecutor(cmd);
        getCommand("setspawn").setExecutor(cmd);
        getCommand("speed").setExecutor(cmd);
        getCommand("delhome").setExecutor(cmd);
        getCommand("fly").setExecutor(cmd);
        getCommand("nametag").setExecutor(cmd);
        getCommand("resetpoints").setExecutor(cmd);
        getServer().getPluginManager().registerEvents(events, this);

    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEconomy() {
        return econ;
    }
    public Chat getChat() { return chat; }
    public Permission getPerms() {return perms; }
    @Override
    public void onDisable(){
        getServer().getConsoleSender().sendMessage("vCore v1.0 has been disabled.");
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    private void createHomes() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "homes.yml");
            if (!file.exists()) {
                getLogger().info("homes.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("homes.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void createData(){
        try{
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "data.yml");
            if (!file.exists()) {
                getLogger().info("data.yml not found, creating!");
                file.createNewFile();
                saveDefaultConfig();
            } else {
                getLogger().info("data.yml found, loading!");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
