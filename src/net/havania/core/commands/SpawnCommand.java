package net.havania.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.havania.core.Core;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			
			if(p.hasPermission("havania.spawn"))
			{
				p.teleport(Core.getCore().getSpawn());
				p.sendMessage("§7You were been teleported !");
			}
		}
		
		return false;
	}
}
