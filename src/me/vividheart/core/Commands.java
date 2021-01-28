package me.vividheart.core;

import net.luckperms.api.model.group.Group;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Commands implements CommandExecutor {

    private Main main;
    ArrayList<Player> vanished = new ArrayList<Player>();
    ArrayList<Player> glowing = new ArrayList<>();
   public ArrayList<Player> locked = new ArrayList<>();
   public Commands(Main main){
       this.main = main;
   }
   Events events;
   File homesFile = new File("plugins/RosewindCore", "homes.yml");
   FileConfiguration cfg = YamlConfiguration.loadConfiguration(homesFile);
   File dataFile = new File("plugins/RosewindCore", "data.yml");
   FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
   Permission perms;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("prefix"));
        Player p = (Player) sender;
        if(cmd.getLabel().equalsIgnoreCase("torture")){
            Player t = Bukkit.getServer().getPlayer(args[0]);
            if(p.hasPermission("vcore.torture")){
                p.sendMessage(prefix + ChatColor.GREEN + "You are now torturing " + ChatColor.GOLD + t.getName());
                t.playEffect(EntityEffect.HURT);
                t.setHealth(1);
                t.sendTitle(ChatColor.DARK_RED + "Ouch...", ChatColor.GOLD + "You were punished by " + ChatColor.GREEN + p.getName(), 15, 20, 15);
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("fish")){
            if(p.hasPermission("vcore.fish")){
                p.sendMessage(prefix + ChatColor.GREEN + "You are now able to breathe like a fish for 5 minutes.");
                p.sendMessage(ChatColor.GOLD + "Plugin made by vividheart.");
                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 6000, 1));
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("v")){
            if(p.hasPermission("vcore.vanish")) {
                if(!vanished.contains(p)){
                    for(Player pl: Bukkit.getOnlinePlayers()){
                        pl.hidePlayer(p);
                        if(pl.hasPermission("vcore.vanish")){
                            pl.sendMessage(prefix + ChatColor.GRAY + p.getName() + " vanished.");
                            pl.showPlayer(p);
                        }
                    }
                    vanished.add(p);
                    p.getLocation().getWorld().playEffect(p.getLocation(), Effect.SMOKE, 50);
                    p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 10);
                    p.sendTitle(prefix + ChatColor.GREEN + "Vanished.", ChatColor.GOLD + "", 10, 20, 10);
                } else {
                    for(Player pl: Bukkit.getOnlinePlayers()){
                        pl.showPlayer(p);
                        if(pl.hasPermission("vcore.vanish")){
                            pl.sendMessage(prefix + ChatColor.GRAY + p.getName() + " un-vanished.");
                        }
                    }
                    vanished.remove(p);
                    p.getLocation().getWorld().playEffect(p.getLocation(), Effect.SMOKE, 50);
                    p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 10, 10);
                    p.sendTitle(prefix + ChatColor.RED + "Un-vanished.", ChatColor.GOLD + "", 10, 20, 10);
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("glow")){
            if(p.hasPermission("vcore.glow")) {
                if(!glowing.contains(p)){
                    glowing.add(p);
                    p.setGlowing(true);
                    p.sendMessage(prefix + ChatColor.GREEN + "You are now glowing.");
                    p.sendMessage(ChatColor.GOLD + "Plugin made by vividheart.");
                } else {
                    glowing.remove(p);
                    p.setGlowing(false);
                    p.sendMessage(prefix + ChatColor.RED + "You are no longer glowing.");
                    p.sendMessage(ChatColor.GOLD + "Plugin made by vividheart.");
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("lock")){
            Player t = Bukkit.getServer().getPlayer(args[0]);
            if(p.hasPermission("vcore.lock")){
                if(args.length == 1){
                    if(!locked.contains(t)){
                        p.sendMessage(prefix + ChatColor.RED + "You have locked " + ChatColor.GOLD + t.getName());
                        locked.add(t);
                        t.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255, true, false));
                        t.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 148, true, false));
                        t.sendTitle(ChatColor.DARK_RED + "Oof.", ChatColor.GOLD + "You were locked by " + ChatColor.GREEN + p.getName(), 15, 20, 15);
                    } else {
                        p.sendMessage(prefix + ChatColor.GREEN + "You have unlocked " + ChatColor.GOLD + t.getName());
                        locked.remove(t);
                        t.removePotionEffect(PotionEffectType.SLOW);
                        t.removePotionEffect(PotionEffectType.JUMP);
                        t.sendTitle(ChatColor.GREEN + "Congrats!", ChatColor.GOLD + "You were unlocked by " + ChatColor.GREEN + p.getName(), 15, 20, 15);
                    }
                } else {
                    return false;
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("amsg")){
            Player t = Bukkit.getPlayer(args[0]);
            if(p.hasPermission("vcore.adminmessage")){
                if(args.length < 2){
                    p.sendMessage(prefix + ChatColor.RED + "Invalid usage. /amsg [player] [message]");
                }
                if(!t.isOnline()){
                    p.sendMessage(prefix + ChatColor.RED + "That player is not online.");
                }
                if(args.length >= 2){
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i] + " ");
                    }
                    String msg = sb.toString();
                    t.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ADMIN " + ChatColor.GOLD + "to you: " + ChatColor.GREEN + msg);
                    t.sendMessage(ChatColor.GOLD + "You may not reply to this message. Use chat to respond.");
                    p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ADMIN " + ChatColor.GOLD + "to " + t.getName() + ": " + ChatColor.GREEN + msg);
                    p.sendMessage(ChatColor.GOLD + "Players cannot respond to admin messages, they must use chat to respond.");
                    for(Player pl: Bukkit.getOnlinePlayers()){
                        if(pl.hasPermission("vcore.adminmessage") && !pl.getName().equalsIgnoreCase(p.getName())){
                            pl.sendMessage(ChatColor.GRAY + "(" + p.getName() + ")" + ChatColor.DARK_RED + "" + ChatColor.BOLD + " ADMIN " + ChatColor.GOLD + "to " + t.getName() + ": " + ChatColor.GREEN + msg);
                        }
                    }
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("boom")){
            if(p.hasPermission("vcore.boom")){
                Location loc = p.getLocation();
                p.getWorld().createExplosion(loc, 10);
                p.sendTitle("Boom!", "", 10, 40, 10);
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("tab")) {
            if (p.hasPermission("vcore.tab")) {
                if (args.length == 0) {
                    p.sendMessage(prefix + ChatColor.GRAY + "Invalid arguments.");
                    return false;
                }
                if (args[0].toString().equalsIgnoreCase("header")) {
                    if (args.length < 1) {
                        p.sendMessage(prefix + ChatColor.GRAY + "/tab header [header].");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i] + " ");
                        }
                        String msg = sb.toString();
                        String newmsg = ChatColor.translateAlternateColorCodes('&', msg);
                        for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
                            ps.setPlayerListHeader(newmsg);
                        }
                        main.getConfig().set("header", newmsg);
                        main.saveConfig();
                        p.sendMessage(prefix + ChatColor.GREEN + "Header set to: " + newmsg);
                    }
                }
                if (args[0].toString().equalsIgnoreCase("footer")) {
                    if (args.length < 1) {
                        p.sendMessage(prefix + ChatColor.GRAY + "/tab footer [footer].");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i] + " ");
                        }
                        String msg = sb.toString();
                        String newmsg = ChatColor.translateAlternateColorCodes('&', msg);
                        for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
                            ps.setPlayerListFooter(newmsg);
                        }
                        main.getConfig().set("footer", newmsg);
                        main.saveConfig();
                        p.sendMessage(prefix + ChatColor.GREEN + "Footer set to: " + newmsg);
                    }
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("gm")) {
            if (p.hasPermission("vcore.gamemode")) {
                if (args.length == 0) {
                    p.sendMessage(prefix + ChatColor.GRAY + "Invalid arguments.");
                    return false;
                }
                if (args[0].toString().equalsIgnoreCase("s") || args[0].toString().equalsIgnoreCase("survival") || args[0].equals("0")) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(prefix + ChatColor.GRAY + "Gamemode set to " + ChatColor.GOLD + "survival" + ChatColor.GRAY + ".");
                }
                if (args[0].toString().equalsIgnoreCase("c") || args[0].toString().equalsIgnoreCase("creative") || args[0].equals("1")) {
                    p.setGameMode(GameMode.CREATIVE);
                    p.sendMessage(prefix + ChatColor.GRAY + "Gamemode set to " + ChatColor.GOLD + "creative" + ChatColor.GRAY + ".");
                }
                if (args[0].toString().equalsIgnoreCase("sp") || args[0].toString().equalsIgnoreCase("spectator") || args[0].equals("3")) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(prefix + ChatColor.GRAY + "Gamemode set to " + ChatColor.GOLD + "spectator" + ChatColor.GRAY + ".");
                }
                if (args[0].toString().equalsIgnoreCase("a") || args[0].toString().equalsIgnoreCase("adventure") || args[0].equals("2")) {
                    p.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(prefix + ChatColor.GRAY + "Gamemode set to " + ChatColor.GOLD + "adventure" + ChatColor.GRAY + ".");
                }
            } else {
            p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("gmc")){
            Bukkit.dispatchCommand(p, "gamemode creative");
        } // gmc

        if(cmd.getLabel().equalsIgnoreCase("gms")){
            Bukkit.dispatchCommand(p, "gamemode survival");
        } // gms

        if(cmd.getLabel().equalsIgnoreCase("help")){
            List<String> list = main.getConfig().getStringList("help");
            for (String line : list) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        } // help

        if(cmd.getLabel().equalsIgnoreCase("rcore")){
            if(p.hasPermission("vcore.vcore")){
                if(args.length == 0){
                    p.sendMessage(prefix + ChatColor.GRAY + "Custom coded.");
                } else {
                    main.reloadConfig();
                    p.sendMessage(prefix + ChatColor.GREEN + "Config reloaded successfully.");
                }
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", ChatColor.GOLD + "", 10, 20, 10);
            }
        } // vcore

        if(cmd.getLabel().equalsIgnoreCase("hideall")){
            if(p.hasPermission("vcore.hideall")){
                for(Player ps: Bukkit.getOnlinePlayers()){
                    ps.setDisplayName("null");
                    ps.setCustomName("null");
                    ps.setPlayerListName("null");
                }
                p.sendMessage(prefix + ChatColor.GREEN + "All player names hidden.");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "For safety reasons, all player names have been temporarily hidden." +
                        " Please wait for further information from an administrator.");
            } else {
                p.sendTitle(prefix + ChatColor.DARK_RED + "Permission denied.", "", 10, 20, 10);
            } // no perms
        } // hideall

        if(cmd.getLabel().equalsIgnoreCase("joinsound")){
            if(p.hasPermission("vcore.joinsound")){
                if(args.length == 0){
                    p.sendMessage(ChatColor.RED + "Invalid arguments. /joinsound [admin/player] <sound>");
                }
                if(args[0].equalsIgnoreCase("admin") && args[1].equals(null)){
                    p.sendMessage(ChatColor.GREEN + "Admin sound: " + main.getConfig().getString("adminsound"));
                }
                if(args[0].equalsIgnoreCase("admin") && !args[1].equals(null)){
                    main.getConfig().set("adminsound", "Sound." + args[1]);
                    p.sendMessage(ChatColor.GREEN + "Admin sound set to: " + main.getConfig().getString("adminsound"));
                }
                if(args[0].equalsIgnoreCase("player") && args[1].equals(null)){
                    p.sendMessage(ChatColor.GREEN + "Player sound: " + main.getConfig().getString("playersound"));
                }
                if(args[0].equalsIgnoreCase("player") && !args[1].equals(null)){
                    main.getConfig().set("playersound", "Sound." + args[1]);
                    p.sendMessage(ChatColor.GREEN + "Player sound set to: " + main.getConfig().getString("playersound"));
                }
            } else {
                p.sendMessage(ChatColor.RED + "Denied.");
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("weather")){
            if(p.hasPermission("core.weather")){
                if(args.length == 0){
                    p.sendMessage(prefix + ChatColor.RED + "/weather [mood]");
                } else {
                    if (args.length == 1) {
                        if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("sun")){
                            p.getLocation().getWorld().setStorm(false);
                            p.sendMessage(prefix + ChatColor.GREEN + "Weather cleared.");
                        }
                        if(args[0].equalsIgnoreCase("storm")){
                            p.getLocation().getWorld().setThundering(true);
                            p.getLocation().getWorld().setStorm(true);
                            p.sendMessage(prefix + ChatColor.GREEN + "Summoned a storm.");
                        }
                        if(args[0].equalsIgnoreCase("rain")){
                            p.getLocation().getWorld().setThundering(false);
                            p.getLocation().getWorld().setStorm(true);
                            p.sendMessage(prefix + ChatColor.GREEN + "Summoned a storm.");
                        }
                    }
                }
            } else {
                p.sendMessage(prefix  + ChatColor.RED + "Denied.");
            }
        }
        if(cmd.getLabel().equalsIgnoreCase("time")){
            if(p.hasPermission("core.time")){
                if(args.length == 0){
                    p.sendMessage(prefix + ChatColor.RED + "/time [time]");
                } else {
                    if (args.length == 1) {
                        if(args[0].equalsIgnoreCase("day")){
                            p.getLocation().getWorld().setTime(0);
                            p.sendMessage(prefix + ChatColor.GREEN + "Set to day time.");
                        }
                        if(args[0].equalsIgnoreCase("night")){
                            p.getLocation().getWorld().setTime(18000);
                            p.sendMessage(prefix + ChatColor.GREEN + "Set to night time.");
                        }
                        if(args[0].equalsIgnoreCase("noon")){
                            p.getLocation().getWorld().setTime(6000);
                            p.sendMessage(prefix + ChatColor.GREEN + "Set to noon.");
                        }
                        if(args[0].equalsIgnoreCase("dawn")){
                            p.getLocation().getWorld().setTime(23000);
                            p.sendMessage(prefix + ChatColor.GREEN + "Set to dawn.");
                        }
                        if(args[0].equalsIgnoreCase("dusk")){
                            p.getLocation().getWorld().setTime(12000);
                            p.sendMessage(prefix + ChatColor.GREEN + "Set to dusk.");
                        }
                    } else {
                        p.sendMessage(prefix + ChatColor.RED + "/time [time]");
                    }
                }
            } else {
                p.sendMessage(prefix  + ChatColor.RED + "Denied.");
            }
        }

        int homesnum = 0;
        if (cmd.getLabel().equalsIgnoreCase("sethome")) {
            if (p.hasPermission("core.sethome.default")) {
                if(cfg.contains(("users." + p.getUniqueId() + ".homescount"))) {
                    if (cfg.getInt("users." + p.getUniqueId() + ".homescount") < 2) {
                        if (args.length == 1) {
                            int homescount = cfg.getInt("user." + p.getUniqueId() + ".homescount");
                            homescount++;
                            String homeName = args[0];
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".world",
                                    p.getLocation().getWorld().getName());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".x",
                                    p.getLocation().getX());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".y",
                                    p.getLocation().getY());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".z",
                                    p.getLocation().getZ());
                            try {
                                cfg.save(homesFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage(prefix + ChatColor.GOLD + "Set your home to your location.");
                        } else if (args.length < 1) {
                            p.sendMessage(prefix + ChatColor.RED + "Please specify a name for your home.");
                        }
                    } else {
                        p.sendMessage(prefix + ChatColor.RED + "You may only set two homes.");
                    }
                } else {
                    cfg.set("users." + p.getUniqueId() + ".homescount", homesnum);
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Access denied.");
            }
            if(p.hasPermission("core.sethome.vip")){
                if(cfg.contains("users." + p.getUniqueId() + ".homescount")){
                    if(cfg.getInt("users." + p.getUniqueId() + ".homescount") < 5) {
                        if (args.length == 1) {
                            int homescount = cfg.getInt("user." + p.getUniqueId() + ".homescount");
                            homescount++;
                            cfg.set("users." + p.getUniqueId() + ".homescount", homesnum);
                            String homeName = args[0];
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".world",
                                    p.getLocation().getWorld().getName());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".x",
                                    p.getLocation().getX());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".y",
                                    p.getLocation().getY());
                            cfg.set("users." + p.getUniqueId() + ".homes." + homeName + ".z",
                                    p.getLocation().getZ());
                            try {
                                cfg.save(homesFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage(prefix + ChatColor.GOLD + "Set your home to your location.");
                        } else if (args.length < 1) {
                            p.sendMessage(prefix + ChatColor.RED + "Please specify a name for your home.");
                        }
                    } else {
                        p.sendMessage(prefix + ChatColor.RED + "You may only set five homes.");
                    }
                } else {
                    cfg.set("users." + p.getUniqueId() + ".homescount", homesnum);
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Access denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("home")){
            if(p.hasPermission("core.home")){
                if (args.length == 0) {
                    p.sendMessage(prefix + ChatColor.GRAY + "Please specify the name of the home.");
                }
                String homeName = args[0];

                String sx = cfg.getString("users." + p.getUniqueId() + ".homes." + homeName + ".x");
                World w = Bukkit
                        .getWorld(cfg.getString("users." + p.getUniqueId() + ".homes." + homeName + ".world"));
                float x = Float.valueOf(sx);
                String sy = cfg.getString("users." + p.getUniqueId() + ".homes." + homeName + ".y");
                float y = Float.valueOf(sy);
                String sz = cfg.getString("users." + p.getUniqueId() + ".homes." + homeName + ".z");
                float z = Float.valueOf(sz);
                Location homeLoc = new Location(w, x, y, z);
                if (args.length == 1) {
                    p.teleport(homeLoc);
                    p.sendMessage(prefix + ChatColor.GRAY + "You've been teleported to your home.");
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("homes")){
            ConfigurationSection section = cfg.getConfigurationSection("users." + p.getUniqueId() + ".homes");
            if(p.hasPermission("core.homes")){
                p.sendMessage(prefix + ChatColor.GRAY + "Homes: " + ChatColor.GOLD + section.getKeys(false).stream().collect(Collectors.joining(", ")));
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("delhome")){
            ConfigurationSection section = cfg.getConfigurationSection("users." + p.getUniqueId() + ".homes");
            String homename = args[0];
            if(p.hasPermission("core.delhome")){
                int homescount = cfg.getInt("users." + p.getUniqueId() + ".homescount", homesnum);
                homescount--;
                if(args.length == 0){
                    p.sendMessage(prefix + ChatColor.RED + "/delhome [home]");
                } else if(args.length == 1){
                    section.set(homename, null);
                    p.sendMessage(prefix + ChatColor.GRAY + "Deleted home " + ChatColor.GOLD + homename + ChatColor.GRAY + ".");
                }
                try {
                    cfg.save(homesFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("setspawn")){
            if(p.hasPermission("core.setspawn")){
                main.getConfig().set("Spawn.world",p.getLocation().getWorld().getName());
                main.getConfig().set("Spawn.x",Double.valueOf(p.getLocation().getX()));

                main.getConfig().set("Spawn.y",Double.valueOf(p.getLocation().getY()));

                main.getConfig().set("Spawn.z",Double.valueOf(p.getLocation().getZ()));

                main.getConfig().set("Spawn.yaw",Float.valueOf(p.getLocation().getYaw()));

                main.getConfig().set("Spawn.pitch",Float.valueOf(p.getLocation().getPitch()));
                p.sendMessage(prefix + ChatColor.GREEN + "Spawn set to your location.");
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Permission denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("spawn")){
            if(main.getConfig().getString("Spawn").isEmpty()){
                p.sendMessage(prefix + ChatColor.RED + "Cannot teleport to spawn, spawn is non-existent.");
            }
            World w = Bukkit.getServer().getWorld(main.getConfig().getString("Spawn.world"));
            double x = main.getConfig().getDouble("Spawn.x");
            double y = main.getConfig().getDouble("Spawn.y");
            double z = main.getConfig().getDouble("Spawn.z");
            p.sendTitle(ChatColor.GOLD + "Teleporting...", "", 10, 10, 10);
            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.SMOKE, 50);
            p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 5, 10);
            p.teleport(new Location(w, x, y, z));
        }

        if(cmd.getLabel().equalsIgnoreCase("speed")){
            if(p.hasPermission("core.speed")){
                if(args.length == 0){
                    p.sendMessage(prefix + ChatColor.RED + "/speed [speed]");
                } else {
                    int speed = Integer.valueOf(args[0]);
                    switch(speed){
                        case 1:
                            p.setWalkSpeed((float) 0.1);
                            p.setFlySpeed((float) 0.1);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 2:
                            p.setWalkSpeed((float) 0.2);
                            p.setFlySpeed((float) 0.2);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 3:
                            p.setWalkSpeed((float) 0.3);
                            p.setFlySpeed((float) 0.3);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 4:
                            p.setWalkSpeed((float) 0.4);
                            p.setFlySpeed((float) 0.4);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 5:
                            p.setWalkSpeed((float) 0.5);
                            p.setFlySpeed((float) 0.5);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 6:
                            p.setWalkSpeed((float) 0.6);
                            p.setFlySpeed((float) 0.6);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 7:
                            p.setWalkSpeed((float) 0.7);
                            p.setFlySpeed((float) 0.7);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 8:
                            p.setWalkSpeed((float) 0.8);
                            p.setFlySpeed((float) 0.8);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 9:
                            p.setWalkSpeed((float) 0.9);
                            p.setFlySpeed((float) 0.9);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                        case 10:
                            p.setWalkSpeed(1);
                            p.setFlySpeed(1);
                            p.sendMessage(prefix + ChatColor.GRAY + "Speed set to " + ChatColor.GOLD + speed);
                            break;
                    }
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Permission denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("fly")){
            if(p.hasPermission("core.fly")){
                p.setAllowFlight(true);
                p.getAllowFlight();
                p.setFlying(true);
                p.sendMessage(prefix + ChatColor.GREEN + "Fly mode enabled.");
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Permission denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("nametag")){
            if(p.hasPermission("core.nametag")) {
                if(args.length == 1){
                    if(args[0].contains("&")){
                        String msgwcolor = ChatColor.translateAlternateColorCodes('&', args[0]);
                        p.setCustomName(msgwcolor);
                        p.sendMessage(prefix + ChatColor.GREEN + "New nametag set to " + msgwcolor + ChatColor.GREEN + ".");
                    } else {
                        p.setCustomName(args[0]);
                    }
                } if(args.length == 2){
                    for(Player online : Bukkit.getOnlinePlayers()){
                        if(args[1].contains(online.getName())){
                            p.sendMessage(prefix + ChatColor.RED + "Please specify a nametag for that user.");
                        } else {
                            if(args[0].contains("&")){
                                String msgwcolor = ChatColor.translateAlternateColorCodes('&', args[0]);
                                p.setCustomName(msgwcolor);
                                p.sendMessage(prefix + ChatColor.GREEN + "New nametag set to " + msgwcolor + ChatColor.GREEN + ".");
                            } else {
                                p.setCustomName(args[0]+args[1]);
                            }
                        }
                    }
                } if(args.length > 2){
                    Player target = Bukkit.getPlayer(args[0]);
                    for(Player online : Bukkit.getOnlinePlayers()){
                        if(args[0].contains(online.getName())){
                            if(args[1].contains("&")){
                                String msgwcolor = ChatColor.translateAlternateColorCodes('&', args[1]);
                                target.setCustomName(msgwcolor);
                                target.sendMessage(prefix + ChatColor.GREEN + "New nametag set to " + msgwcolor + ChatColor.GREEN + ".");
                            } else {
                                target.setCustomName(args[1]);
                            }
                        } else {
                            if(args[0].contains("&")){
                                String msgwcolor = ChatColor.translateAlternateColorCodes('&', args[0]);
                                p.setCustomName(msgwcolor);
                                p.sendMessage(prefix + ChatColor.GREEN + "New nametag set to " + msgwcolor + ChatColor.GREEN + ".");
                            } else {
                                p.setCustomName(args[0]+args[1]);
                            }
                        }
                    }
                }
            } else {
                p.sendMessage(prefix + ChatColor.RED + "Permission denied.");
            }
        }

        if(cmd.getLabel().equalsIgnoreCase("resetpoints")){
            if(p.hasPermission("core.resetpoints")){
                if(args.length == 1){
                    Player target = Bukkit.getServer().getPlayer(args[0]);
                    data.set(target.getUniqueId() + ".points", 0);
                    try{
                       data.save(dataFile);
                    } catch (IOException exception){
                        exception.printStackTrace();
                    }
                    p.sendMessage(ChatColor.GREEN + "The points of have been reset for " + target.getName());
                } else {
                    p.sendMessage(ChatColor.RED + "Invalid arguments. /resetpoints [player]");
                }
            }
        }



        return true;
    } // end

} // class end
