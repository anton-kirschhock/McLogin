package org.zapto.muziedev.McLogin;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.zapto.muziedev.util.ConfReader;
import org.zapto.muziedev.util.Vector3f;

/**
 *
 * @author Anton
 */
public class PlayerDiedTask extends BukkitRunnable  {
    private final JavaPlugin plugin;
    private Player p;
    public PlayerDiedTask(JavaPlugin plugin,Player p) throws Exception {
        this.plugin = plugin;
        if(p == null){
            throw new Exception("Player is NULL");
        } else{
            this.p =  p;
        }
                
    }
 
        private String getConfigFile(){
        String returnee = "";
        File x = new File(plugin.getDataFolder(), "mclogin.conf");
        if(x.exists()){
            returnee = x.getAbsolutePath();
        }
        return returnee;
                    
    }
    
    @Override
    public void run() {
         //System.out.println("Running Task for player " + p.getName());
         p.setGameMode(GameMode.SURVIVAL);
         ConfReader cr = new ConfReader(getConfigFile());
         Vector3f pos =  cr.getPos();
         int area = 5;//Integer.parseInt(cr.getValue(ConfReader.LOGINAREA)[0]);
         int x = (int)((Math.random() * ((double)area - (-(double)area))) + -(double)area);
         int y = (int)((Math.random() * ((double)area - (-(double)area))) + -(double)area);
         Vector3f adder = new Vector3f(x,0,y);
         pos =  pos.add(adder);
         Location location = new Location(Bukkit.getServer().getWorld(cr.getValue(ConfReader.WORLDNAME)[0]), pos.getX(), pos.getY(), pos.getZ());
         p.teleport(location);
         p.setWalkSpeed(0);
         p.setHealth(20.0);
         this.cancel();        
    }
}
