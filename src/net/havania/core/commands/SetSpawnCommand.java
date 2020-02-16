package net.havania.core.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.havania.core.Core;

public class SetSpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			
			if(p.hasPermission("havania.spawn"))
			{
				Location location = p.getLocation();
				
				Core.getCore().spawn = location;
				
	        	Core.getCore().getConfig().set("spawn.World", location.getWorld().getName());
	        	Core.getCore().getConfig().set("spawn.X", location.getX());
	        	Core.getCore().getConfig().set("spawn.Y", location.getY());
	        	Core.getCore().getConfig().set("spawn.Z", location.getZ());
	        	Core.getCore().getConfig().set("spawn.Yaw", location.getY());
	        	Core.getCore().getConfig().set("spawn.Pitch", location.getPitch());
	        	
	        	p.sendMessage("§7The spawn has been succefully changed !");
	        	
	        	Core.getCore().saveConfig();
	        	Core.getCore().reloadConfig();
			}
		}
		
		return false;
	}
}
