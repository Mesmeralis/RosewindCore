package me.vividheart.core;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_16_R2.EnumInteractionResult;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import net.minecraft.server.v1_16_R2.WorldServer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Events implements Listener{

    private Main main;
    public Chat chat;
    public Permission perms;
    public PointsSentinelIntegration sent;
    File dataFile = new File("plugins/RosewindCore", "data.yml");
    FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    private MinecraftServer CraftServer;
    //  public HashMap<Player, Integer> points = new HashMap<>();


    public Events(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.isOp()){
            for(Player ps: Bukkit.getOnlinePlayers()) {
                ps.playSound(ps.getLocation(), Sound.valueOf(main.getConfig().getString("adminsound").replace("[", "").replace("]", "").toUpperCase()), 3, 1);
            }
        }
        else {
            for(Player ps: Bukkit.getOnlinePlayers()) {
                ps.playSound(ps.getLocation(),  Sound.valueOf(main.getConfig().getString("playersound").replace("[", "").replace("]", "").toUpperCase()), 3, 1);
            }
        }
        for(Player ps: Bukkit.getOnlinePlayers()){
            ps.setPlayerListFooter(main.getConfig().getString("footer"));
            ps.setPlayerListHeader(main.getConfig().getString("header"));
        }
        String joinmessage = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("joinmessage"));
        String newjoinmsg = joinmessage.replace("[", "").replace("]", "");
        e.setJoinMessage(newjoinmsg.replace("%p%", p.getName()));
        if(!p.hasPlayedBefore()){
            main.getEconomy().createPlayerAccount(p);
        }
        if(!data.contains(p.getUniqueId() + ".points")){
            data.set(p.getUniqueId() + ".points", 0);
            try {
                data.save(dataFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    } // onJoin

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        String leavemessage = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("leavemessage"));
        String newleavemsg = leavemessage.replace("[", "").replace("]", "");
        e.setQuitMessage(newleavemsg.replace("%p%", p.getName()));
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e){
        Entity entity = e.getEntity();
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
                @Override
                public void run() {
                    if(!arrow.isDead() && !arrow.isOnGround()){
                        arrow.getWorld().spawnParticle(Particle.TOTEM, arrow.getLocation(), 1);
                    }
                }
            }, 0L, 2L);
            if(e.getEntity().getShooter() instanceof Skeleton){
                Skeleton skele = (Skeleton) ((Arrow) entity).getShooter();
                skele.setArrowsInBody(skele.getArrowsInBody()-1);
                if(skele.getArrowsInBody() == 0){
                 skele.setHealth(0);
                 skele.getWorld().dropItemNaturally(skele.getLocation(), new ItemStack(Material.BONE, 3));
                 skele.getWorld().createExplosion(skele.getLocation(), 3, false, false);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Zombie){
            Zombie z = (Zombie) e.getEntity();
            float radius = 1.5f;
            float angle = 0f;
            Location location = z.getLocation();
            double x = (radius * Math.sin(angle));
            double zl = (radius * Math.cos(angle));
            angle += 0.1;
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
        }
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e){
        if(e.getEntity() instanceof Skeleton){
            Skeleton skele = (Skeleton) e.getEntity();
            skele.setArrowsInBody(31);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        e.setFormat(ChatColor.RESET + String.valueOf(data.getInt(e.getPlayer().getUniqueId() + ".points")) + ChatColor.DARK_GRAY + " | " + ChatColor.RESET + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + " : " + ChatColor.RESET + e.getMessage());
        if(e.getMessage().contains(".com") || e.getMessage().contains(".net") || e.getMessage().contains(".gg")){
            Player p = e.getPlayer();
            p.sendMessage(ChatColor.GOLD + "Please do not advertise.");
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3, 1);
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void manipulate (PlayerArmorStandManipulateEvent e)
    {
        if (!e.getRightClicked().isVisible()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity().getPlayer();
        Entity killer = p.getKiller();
        String mprefix = ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.BOLD + "!" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.ITALIC;

        if(!p.hasMetadata("NPC")){
            p.getWorld().strikeLightningEffect(p.getLocation());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Entity ent = e.getEntity();
        Entity killer = e.getEntity().getKiller();
        if(ent.hasMetadata("NPC")){
                if (killer instanceof Player) {
                    Player p = (Player) killer;
                    if(ent.getCustomName().equalsIgnoreCase("Land Roamer")){
                        if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                            if(data.contains(p.getUniqueId() + ".points")){
                                data.set(p.getUniqueId() + ".points", data.getInt(p.getUniqueId() + ".points")+ 20);
                                try {
                                    data.save(dataFile);
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                data.set(p.getUniqueId() + ".points", 10);
                                try {
                                    data.save(dataFile);
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                 }
                }
        }
    }



} // end
