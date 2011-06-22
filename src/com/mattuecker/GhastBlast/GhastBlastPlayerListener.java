/**
 * File: GhastBlastPlayerListener.java
 * Author: Matthew Uecker
 * Description:
 * 
 * This file describes GhastBlast's player listener class as well as
 * what to do when the events happen and what to call in GhastBlast.java
 * if certain requirements are met.
 * 
 * GhastBlast v0.1.0
 */

package com.mattuecker.GhastBlast;

//org.bukkit
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.Material;

public class GhastBlastPlayerListener extends PlayerListener
{	
	public static GhastBlast plugin;
	
	public GhastBlastPlayerListener(GhastBlast gBlast)
	{
		plugin = gBlast;
	}
	
	/**
	 * onPlayerInteract
	 * @param event
	 * 
	 * This method catches the event that player "uses" glowstone dust and
	 * calls the launchFireball method.
	 */
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Material material = event.getMaterial();
		if(material == Material.GLOWSTONE_DUST)
		{
			plugin.launchFireball(player);
		}
	}
}
