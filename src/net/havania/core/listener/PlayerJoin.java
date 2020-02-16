package net.havania.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.havania.core.Core;

public class PlayerJoin implements Listener {
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		Core core = Core.getCore();
		
		core.loadPlayerData(p);
		p.teleport(core.getSpawn());
		e.setJoinMessage(null);
	}

}
