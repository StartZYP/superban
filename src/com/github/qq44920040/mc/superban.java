package com.github.qq44920040.mc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class superban extends JavaPlugin implements Listener {
    private List<String> ipconfig = new ArrayList<>();
    @EventHandler
    public void PlayerJoinGame(PlayerJoinEvent event){
        final String hostString = event.getPlayer().getAddress().getHostString();
        final String name = event.getPlayer().getName();
        //System.out.println(hostString+"加入了");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogUtils.info("[SuperBan]" + sdf.format(d) + ":", "IP地址:"+hostString);
        new BukkitRunnable(){
            @Override
            public void run() {
                for (String tempip:ipconfig){
                    if (tempip.contains("*")){
                        //System.out.println("有的");
                        String[] split = hostString.split("\\.");
                        String[] split1 = tempip.split("\\.");
                        //System.out.println(split.length+"数组长度");
                        boolean IsKick = true;
                        for (int a=0;a<=3;a++){
                            if (!split1[a].equalsIgnoreCase("*")){
                                if (!split[a].equalsIgnoreCase(split1[a])){
                                    IsKick = false;
                                    break;
                                }
                            }
                        }
                        if (IsKick){
                            Bukkit.getServer().getPlayer(name).kickPlayer(getConfig().getString("BanMsg"));
                        }
                    }else {
                        //System.out.println("进入");
                        if (tempip.equalsIgnoreCase(hostString)){
                            Bukkit.getServer().getPlayer(name).kickPlayer(getConfig().getString("BanMsg"));
                        }
                    }
                }
            }
        }.runTask(this);
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!file.exists()){
            saveDefaultConfig();
        }
        ipconfig = getConfig().getStringList("BanIPArea");
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        System.out.println(ipconfig.size());
        LogUtils.setLogOutLevel(LogUtils.Level.INFO);
        try {
            LogUtils.setLogOutFile(new File("SuperBanLog.log"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.setLogOutTarget(false, true);
        LogUtils.info("[SuperBan]", "Check");
        super.onEnable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       if (sender.isOp()){
           if (label.equalsIgnoreCase("superbanIP")){
               if (args.length==1&&args[0].contains(".")){
                   ipconfig.add(args[0]);
                   getConfig().set("BanIPArea",ipconfig);
                   sender.sendMessage("§e§l添加成功");
                   saveConfig();
               }else {
                   sender.sendMessage("§e§l填写的Ip不正确");
               }
           }else if (label.equalsIgnoreCase("superunbanIP")){
               if (args.length==1&&ipconfig.contains(args[0])){
                   ipconfig.remove(args[0]);
                   getConfig().set("BanIPArea",ipconfig);
                   sender.sendMessage("§e§l删除成功");
                   saveConfig();
               }else {
                    sender.sendMessage("§e§l目前ip填写不在数据表中");
               }
           }
       }
        return super.onCommand(sender, command, label, args);
    }
}
