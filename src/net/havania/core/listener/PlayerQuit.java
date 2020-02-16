package net.havania.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.havania.core.Core;

public class PlayerQuit implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		Core core = Core.getCore();
		
		core.unloadPlayerData(p);
		
		e.setQuitMessage(null);
	}

}
