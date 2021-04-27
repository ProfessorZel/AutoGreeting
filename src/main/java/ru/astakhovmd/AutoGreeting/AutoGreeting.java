package ru.astakhovmd.AutoGreeting;

import de.myzelyam.supervanish.SuperVanishPlugin;
import de.myzelyam.supervanish.hooks.PlaceholderAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AutoGreeting extends JavaPlugin implements Listener {
    Plugin PAPI;
    HashSet<String> wb_say = new HashSet<>();
    ArrayList<String> options = new ArrayList<>();
    SuperVanishPlugin SV;
    Random rand = new Random();
    static String root_dir = "plugins" + File.separator + "AutoGreeting";
    static String config_file = root_dir + File.separator + "config.yml";
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        SV = (SuperVanishPlugin) Bukkit.getPluginManager().getPlugin("SuperVanish");
        PAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        PluginCommand c = Bukkit.getPluginCommand("autowb");
        if (c!=null){
            c.setTabCompleter(new TabUsage());
        }
        loadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player plr = null;
        if (sender instanceof  Player){
            plr = (Player) sender;
        }

        if (command.getName().equalsIgnoreCase("autowb")){
            if (args.length == 0){
                sender.sendMessage("Usage: /autowb on|off");
                return true;
            }
                if (args[0].equalsIgnoreCase("on") && plr!=null){
                    if (!plr.hasPermission("AutoGreeting.use")) {
                        sender.sendMessage("Insufficient permissions!");
                        return true;
                    }
                        wb_say.add(plr.getUniqueId().toString());
                        sender.sendMessage("Auto WB Enabled!");


                }else if (args[0].equalsIgnoreCase("off") && plr!=null){
                    if (!plr.hasPermission("AutoGreeting.use")) {
                        sender.sendMessage("Insufficient permissions!");
                        return true;
                    }
                        wb_say.remove(plr.getUniqueId().toString());
                        sender.sendMessage("Auto WB Disabled!");

                }else if(args[0].equalsIgnoreCase("set")){
                    if (plr!=null)
                        if (!plr.hasPermission("AutoGreeting.admin") ){
                            sender.sendMessage("Insufficient permissions!");
                            return true;
                        }
                    if (args.length>2){
                        Player s = Bukkit.getPlayer(args[1]);
                        if (s!=null){
                            switch (args[2].toLowerCase()) {
                                case "on":
                                    wb_say.add(s.getUniqueId().toString());
                                    break;
                                case "off":
                                    wb_say.remove(s.getUniqueId().toString());
                                    break;
                                default:
                                    sender.sendMessage("Unknown setting use on|off");
                            }
                        }else{
                            sender.sendMessage("Player not found.");
                        }
                    }else{
                        sender.sendMessage("Usage: /autowb set <player> on|off");
                    }
                }else{
                    sender.sendMessage("Usage: /autowb on|off");
                }

        }
        return true;
    }

    void loadConfig(){
        YamlConfiguration conf =  YamlConfiguration.loadConfiguration(new File(config_file));
        wb_say = (HashSet<String>) conf.getStringList("speakers");
        options = (ArrayList<String>) conf.getStringList("options");
        if (wb_say ==null){
            wb_say = new HashSet<>();
        }
        if (options ==null){
            options = new ArrayList<>();
        }
        if (options.size()==0){
            options.add("Hello %player_name%!");
        }
    }

    String getGreeting(Player joiner){
        String msg = options.get(rand.nextInt(options.size()-1));
        if (PAPI!=null) {
            PlaceholderAPI.setPlaceholders(joiner, msg);
        }
        return msg;
    }

    @EventHandler
    boolean onJoin(PlayerJoinEvent e){
        Player n = e.getPlayer();
        if (!n.hasPermission("AutoGreeting.ignore") && !SV.getVanishStateMgr().isVanished(n.getUniqueId())){
            for (Player p: Bukkit.getOnlinePlayers()) {
                if (wb_say.contains(p.getUniqueId())){
                    if (p.hasPermission("AutoGreeting.use")){
                        if (!SV.getVanishStateMgr().isVanished(p.getUniqueId())){
                            //TODO: async and in time span
                            Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                                @Override
                                public void run() {
                                    p.chat(getGreeting(n));
                                }
                            }, rand.nextInt(1500));

                        }
                    }else {
                        wb_say.remove(p.getUniqueId());
                    }

                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        YamlConfiguration conf =  new YamlConfiguration();
        conf.set("speakers", wb_say);
        conf.set("options",options);
        try {
            conf.save(new File(config_file));
        } catch (IOException ignored) {

        }
    }
}
