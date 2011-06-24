/**
 * File: GhastBlast.java
 * Author: Matthew Uecker
 * Description:
 * 
 * This file contains the main class for the plugin GhastBlast. The idea is,
 * determine if the PLAYER is holding GLOWSTONE_DUST and "uses" it. Then fire a
 * ghast bomb in the direction of the crosshairs.
 * 
 * GhastBlast v0.2.0
 */

package com.mattuecker.GhastBlast;

//World Server
import java.util.logging.Logger;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;

//org.bukkit
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.command.Command;

//Permissions - [Nijikokun, The Yeti]
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;


//Main class.
public class GhastBlast extends JavaPlugin
{

	//Debugging variables.
	public static int debug = 0;
	public static int deny_flag = 0;
	Logger log = Logger.getLogger("Minecraft");
	private GhastBlastPlayerListener playerListener = new GhastBlastPlayerListener(this);;
	public static PermissionHandler permHandler;
	public static PluginManager pm;
	
	
	//Constructor
	public GhastBlast()
	{
		super();
	}
	
	
	//Setup for hooking into permissions - [Nijikokun, The Yeti]
	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	    if (permHandler == null) {
	    	if (permissionsPlugin != null) {
	            permHandler = ((Permissions) permissionsPlugin).getHandler();
	        } else {
	            log.info("Permission system not detected, defaulting to OP");
	        }
	    }
	}
	

	
	/**
	 * onLoad
	 * 
	 * Called when craftbukkit loads or reloads any plugins.
	 */
	public void onLoad()
	{
		log.info("[GhastBlast v0.2.0] Loading...");
	}

	/**
	 * onDisable
	 * 
	 * Called when the user types the command "/ghastblast deny".
	 */
	@Override
	public void onDisable()
	{
		log.info("[GhastBlast v0.2.0] Disabled.");
		this.getServer().reload();
	}
	
	/**
	 * onCommand
	 * 
	 * Captures server commands (also player typed) and determines how to
	 * process the arguments.
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = null;
		String commandString = command.getName().toLowerCase();
			
		//Check if it was a player who typed the command.
		if(sender instanceof Player){
			player = (Player) sender;
		}
				
		//Check if it was the right command.
		if(commandString.equalsIgnoreCase("ghastblast"))
		{
			if(args.length == 1)
			{
				//Process sub-arguments based on permission.
				if(player != null)
				{
					if(args[0].equalsIgnoreCase("help"))
					{
						player.sendMessage("Use glowstone dust to shoot fireballs!");
					}
					
					if(permHandler.has(player, "ghastblast.admin"))
					{
						if(args[0].equalsIgnoreCase("allow"))
						{
							deny_flag = 0;
							player.getServer().broadcastMessage("GhastBlast enabled.");
						}
						else if(args[0].equalsIgnoreCase("deny"))
						{
							deny_flag = 1;
							player.getServer().broadcastMessage("GhastBlast disabled.");
						}
						else if(args[0].equalsIgnoreCase("disable"))
						{
							player.getServer().broadcastMessage("Disabling entire GhastBLast plugin.");
							pm.disablePlugin(this);
						}
					}
				}
				else
				{
					if(args[0].equalsIgnoreCase("help"))
					{
						sender.sendMessage("Use glowstone dust to shoot fireballs!");
					}
					else if(args[0].equalsIgnoreCase("allow"))
					{
						deny_flag = 0;
						sender.sendMessage("GhastBlast enabled.");
					}
					else if(args[0].equalsIgnoreCase("deny"))
					{
						deny_flag = 1;
						sender.sendMessage("GhastBlast disabled.");
					}
					else if(args[0].equalsIgnoreCase("disable"))
					{
						sender.sendMessage("Disabling entire GhastBLast plugin.");
						pm.disablePlugin(this);
					}
				}
			}
			else
			{
				sender.sendMessage("usage: /ghastblast <help|allow|deny|disable>");
			}
		}
		return true;
	}

	/**
	 * onEnable
	 * 
	 * Called when craftbukkit loads the plugin, displays log info in server
	 * command console if successful.
	 */
	@Override
	public void onEnable()
	{		
		//Get PluginManager.
		pm = this.getServer().getPluginManager();
		setupPermissions();

		//Register events.
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		
		//Log enabled status.
		log.info("[GhastBlast v0.2.0] enabled.");
	}	
	
	/**
	 * launchFireball
	 * @param player
	 * 
	 * Creates all required objects for definition and manifestation of an EntityFireball.
	 */
	public void launchFireball(Player player)
	{
		//Define variables and entities.
		CraftPlayer craftPlayer = (CraftPlayer) player;
		EntityLiving playerEntity = craftPlayer.getHandle();
		
		//Define direction of fireball. Multiplying by 10 gives better accuracy. 
		Vector lookat = player.getLocation().getDirection().multiply(10);
		
		//Define location of the player.
		Location loc = player.getLocation();

		//I code in C an awful lot.
		if(debug == 1)
		{
			System.out.println("Location: " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
			System.out.println("Direction: " + lookat.getX() + " " + lookat.getY() + " " + lookat.getZ());
		}
		
		EntityFireball fball = new EntityFireball(((CraftWorld) player.getWorld()).getHandle(), playerEntity, lookat.getX(), lookat.getY(), lookat.getZ());
		
		//Make the fireball spawn slightly out and away from the player.
		fball.locX = loc.getX() + (lookat.getX()/5.0) + 0.25;
		fball.locY = loc.getY() + (player.getEyeHeight()/2.0) + 0.5;
		fball.locZ = loc.getZ() + (lookat.getZ()/5.0);
		
		if(deny_flag == 0 && !permHandler.has(player, "ghastblast.admin"))
		{
			((CraftWorld) player.getWorld()).getHandle().addEntity(fball);
		}
		else if(permHandler.has(player, "ghastblast.admin"))
		{
			((CraftWorld) player.getWorld()).getHandle().addEntity(fball);
		}
	}
}
