package org.zapto.muziedev.McLogin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.zapto.muziedev.util.ConfReader;
import org.zapto.muziedev.util.MySqlConnection;
import org.zapto.muziedev.util.StrList;
import org.zapto.muziedev.util.Vector3f;
/**
 *
 * @author Anton
 */
public class McLogin extends JavaPlugin implements Listener {
    private boolean canCleanRun =false;
    private List<String> playersToKeepSpawning;
    private String getConfigFile(){
        String returnee = "";
        File x = new File(this.getDataFolder(), "mclogin.conf");
        if(x.exists()){
            returnee = x.getAbsolutePath();
        }
        return returnee;
                    
    }
    public void onEnable(){
         canCleanRun = true;
            getLogger().info("Starting McLogin!");
            File f = new File(this.getDataFolder() + "/");
            if(!f.exists()){
                f.mkdir();
                File x = new File(this.getDataFolder(), "mclogin.conf");
                if(!x.exists()){
                    
                    try {
                        x.createNewFile();
                        
                    } catch (IOException ex) {
                        Logger.getLogger(McLogin.class.getName()).log(Level.SEVERE,"EXC",ex);
                    }
            }
            }
            ConfReader cr = new ConfReader(getConfigFile());
            if(!cr.evalConf()){
                canCleanRun =  false;
                getLogger().severe("Cannot read Config! Stopping initing!");
                
            }
            if(canCleanRun){
                playersToKeepSpawning =  new ArrayList();
                this.getServer().getPluginManager().registerEvents(this, this);
            }
            //getLogger().info(getConfigFile());
            
    }

    public void onDisable(){
            getLogger().info("Stopping McLogin!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer() != null){
            String sql = "UPDATE tblplayers SET PlayerLoggedIn=1 WHERE PlayerMCName='" + event.getPlayer().getName() + "'";
            MySqlConnection con = getCon();
            con.execNonQuery(sql);
            getLogger().info(event.getPlayer().getName().toString() + " has logged in!");
            sql = "SELECT PlayerLoginAttempt,PLayerLocked FROM tblplayers WHERE PlayerMCName = '" + event.getPlayer().getName() + "'";
            getLogger().info(sql);
            teleportPlayerToSpawn(event.getPlayer());
            event.setJoinMessage("Please login using the /login command followed by your pin!");
            this.playersToKeepSpawning.add(event.getPlayer().getName());
            if(con.getQueryExists(sql)){
                String Att = con.getFirst2Result(sql);
                String lock = Att.substring(Att.indexOf('|')+1);
                Att = Att.substring(0,Att.indexOf('|'));
                if(Att != null){
                    if(Integer.parseInt(Att) == 0 || lock.equals("1")){
                        event.getPlayer().kickPlayer("You cannot login!\nPlease get a new Key at http://muziedev.zapto.org:8080/mcmuzie!");
                    }
                }
            } else{
                event.getPlayer().kickPlayer("You cannot login!\nPlease register at http://muziedev.zapto.org:8080/mcmuzie!");
            }
        }
        else{
            getLogger().severe("Cannot Get Players name out of NULL!");
        }
    }
    private void teleportPlayerToSpawn(Player p){
         p.setGameMode(GameMode.SURVIVAL);
         ConfReader cr = new ConfReader(getConfigFile());
         Vector3f pos =  cr.getPos();
         System.out.println("Teleporting " + p.getName());
         int area = 5;//Integer.parseInt(cr.getValue(ConfReader.LOGINAREA)[0]);
         int x = (int)((Math.random() * ((double)area - (-(double)area))) + -(double)area);
         int y = (int)((Math.random() * ((double)area - (-(double)area))) + -(double)area);
         Vector3f adder = new Vector3f(x,0,y);
         pos =  pos.add(adder);
         Location location = new Location(Bukkit.getServer().getWorld(cr.getValue(ConfReader.WORLDNAME)[0]), pos.getX(), pos.getY(), pos.getZ());
         p.teleport(location);
         p.setWalkSpeed(0);
         p.setHealth(20.0);
    }
    
    private void teleportPlayerToVector(Player p,Vector3f pos,String world){
        System.out.println(pos);
        Location location = new Location(Bukkit.getServer().getWorld(world), pos.getX(), pos.getY(), pos.getZ());
        p.teleport(location);        
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e){
        if (this.canCleanRun){
            System.out.println(e.getPlayer().getName() + " Respawned!");
            if(StrList.findElement(playersToKeepSpawning, e.getPlayer().getName())){
                try {
                    PlayerDiedTask pdt = new PlayerDiedTask(this,e.getPlayer());
                    pdt.runTaskLater(this, 20);
                } catch (Exception ex) {
                    System.err.println(ex.toString());
                }
                
            }
        }
    }
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
        if(this.canCleanRun){
            if(!StrList.findElement(playersToKeepSpawning, event.getPlayer().getName())){
                saveData(event.getPlayer());
            }else{
                this.playersToKeepSpawning.remove(event.getPlayer().getName());
            }
        }
        String sql = "UPDATE tblplayers SET PlayerLoggedIn=0 WHERE PlayerMCName='" + event.getPlayer().getName() + "'";
        MySqlConnection con =  getCon();
        con.execNonQuery(sql);
    }    
    private MySqlConnection getCon(){
        ConfReader cr = new ConfReader(getConfigFile());
        String sa = cr.getValue(ConfReader.DATABASEHOST)[0];
        String sp = cr.getValue(ConfReader.DATABASEPORT)[0];
        String sn = cr.getValue(ConfReader.DATABASENAME)[0];
        String su = cr.getValue(ConfReader.DATABASEUSER)[0];
        String spw = cr.getValue(ConfReader.DATABASEPASSWORD)[0];
        return new MySqlConnection(sa,sp,sn,su,spw);
    }
    private void saveData(Player p){
        int x = p.getLocation().getBlockX(),
        y = p.getLocation().getBlockY(),
        z = p.getLocation().getBlockZ();      
        String sql = "UPDATE tblplayers SET PlayerLastLocation='" + x +"|" + y + "|" + z + "',PlayerWorld='" + p.getLocation().getWorld().getName() +
                "' WHERE PlayerMCName='" + p.getName() + "'";
        MySqlConnection con = getCon();
        con.execNonQuery(sql);
        p.sendMessage("Location update complete!");
  }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        boolean returnee = false;
        if(this.canCleanRun){
            if(cmd.getName().equalsIgnoreCase("login") && sender instanceof Player){
                if(args.length < 1){
                    sender.sendMessage("[McLogin] You have to enter your pin to login!");
                    returnee =  true;
                } else{
                    String sql = "SELECT PlayerLastLocation,PlayerWorld FROM tblplayers WHERE PlayerMCName='" + ((Player)sender).getName() + "' AND PlayerLoginKey='" + args[0] + "'";
                    getLogger().info(sql);
                    MySqlConnection con = getCon();
                    if(con.getQueryExists(sql)){
                        sender.sendMessage("[McLogin] Welcome back " + ((Player)sender).getName() + "!");
                        String pos = con.getFirstResult(sql, 1);
                        String world = con.getFirstResult(sql, 2);
                        Vector3f posTo;
                        System.out.println(pos + world);
                        if(pos == null || world ==  null || pos.equals("") || world.equals("")){
                            ConfReader cr = new ConfReader(this.getConfigFile());
                            pos = cr.getValue(ConfReader.DEFAULTSPAWN)[0];
                            world = cr.getValue(ConfReader.DEFAULTWORLD)[0];
                        }
//                        System.out.print("Pos:" + pos.indexOf("|"));
                        String x = pos.substring(0,pos.indexOf("|"));
                        String y = pos.substring(pos.indexOf("|") +1,pos.lastIndexOf("|"));
                        String z = pos.substring(pos.lastIndexOf("|")+1);
                        System.out.println(x + " " + y + " " + z);
                        posTo = new Vector3f((float)Integer.parseInt(x),(float)Integer.parseInt(y),(float)Integer.parseInt(z));
                        this.teleportPlayerToVector((Player)sender, posTo, world);
                        this.playersToKeepSpawning.remove(((Player)sender).getName());
                        ((Player)sender).setWalkSpeed(0.2f);
                    } else{
                        sql = "SELECT PlayerLoginAttempt FROM tblplayers WHERE PlayerMCName='" + ((Player)sender).getName() + "'";
                        getLogger().info(sql);
                        String res = con.getFirstResult(sql,1);
                        if(res != null){
                            int attempt = Integer.parseInt(res);
                            attempt--;
                            if(attempt <= 0){
                                sql = "UPDATE tblplayers SET tblplayers.PlayerLocked=1 WHERE PlayerMCName='" + ((Player)sender).getName() + "'";
                                con.execNonQuery(sql);
                                ((Player)sender).kickPlayer("You have tried to much!\nPlease get a new Key at http://muziedev.zapto.org:8080/mcmuzie!");
                            }else{
                                sender.sendMessage("[McLogin] Wrong Pin! [" + res + " tries left]");
                                String plNm = ((Player)sender).getName();
                                sql = "UPDATE tblplayers SET PlayerLoginAttempt='" + attempt + "' WHERE PlayerMCName='" + plNm + "'";
                                con.execNonQuery(sql);
                            }
                        }
                    }
                    returnee =  true;
                }
            }else  if(cmd.getName().equalsIgnoreCase("saveloc") && sender instanceof Player){
                //Save the current location
                Player p = (Player)sender;
                saveData(p);
                returnee =  true;
            } else if(!(sender instanceof Player)){
                sender.sendMessage("[McLogin]This is a player command!");
            }
        }
        return returnee; 
    }
}
