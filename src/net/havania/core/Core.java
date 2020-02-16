package net.havania.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.havania.core.commands.HubCommand;
import net.havania.core.commands.SetSpawnCommand;
import net.havania.core.commands.SpawnCommand;
import net.havania.core.listener.PlayerJoin;
import net.havania.core.listener.PlayerQuit;
import net.havania.core.utils.Database;
import net.havania.core.utils.PlayerData;
import net.havania.core.utils.Server;
import net.havania.core.utils.Server.ServerType;

public class Core extends JavaPlugin {
	
	static Core core;
	
	public Database database;
	public HashMap<Player, PlayerData> playersData;
	
	public String databaseHost;
	public String databaseName;
	public String databaseUser;
	public String databasePass;
	
	public Location spawn;
	
	public Integer currentServerId = 0;
	public boolean updateServerList = false;
	public HashMap<Integer, Server> servers;
	
	public boolean restarting;
	
	@Override
	public void onEnable() 
	{
		core = this;
		loadConfiguration();
		registerVariables();
		registerListeners();
		registerCommands();
		reloadPlayer();
		
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");	
		
		startServerUpdateTask();
	}
	
	@Override
	public void onDisable() 
	{
		if(!restarting)
		{
			servers.get(currentServerId).suspend();
		}
	}
	
	private void registerVariables()
	{
		database = new Database("jdbc:mysql://", databaseHost, databaseName, databaseUser, databasePass);
		//database = new Database("jdbc:mysql://", "adm.minecraft-mania.fr", "a822014070213254652734534", "822014070734534", "admin");
		database.connection();
		playersData = new HashMap<>();
		servers = new HashMap<>();
		restarting = false;
		
		servers.put(currentServerId, new Server()); //ADD CURRENT SERVER
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				database.connection();
			}
		}, 0L, 20 * 60 * 120L);
	}
	
	private void registerListeners()
	{
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
	}
	
	private void registerCommands()
	{
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("lobby").setExecutor(new HubCommand());
		getCommand("hub").setExecutor(new HubCommand());
	}
	
	private void loadServersList()
	{
		if(!database.isConnected())
		{
			Bukkit.getConsoleSender().sendMessage("§cERROR: HavaniaCore can't run without be connected to database !");
			Bukkit.shutdown();
		}
		
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("SELECT * FROM servers");
			ResultSet result = preparedStatement.executeQuery();
			
			while(result.next())
			{
				Integer id = result.getInt(0);
				Server.ServerType type = Server.ServerType.valueOf(result.getString(1));
				Server.ServerStatus status = Server.ServerStatus.valueOf(result.getString(2));
				String name = result.getString(3);
				String bungeeName = result.getString(4);
				Integer maxPlayer = result.getInt(5);
				Integer currentPlayer = result.getInt(6);
				
				if(servers.containsKey(id))
				{
					servers.get(id).setStatus(status);
					servers.get(id).setCurrentPlayer(currentPlayer);
				}
				else
				{
					servers.put(id, new Server(id, type, status, name, bungeeName, maxPlayer, currentPlayer));
				}
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void startServerUpdateTask()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(updateServerList)
				{
					loadServersList();
				}
				servers.get(currentServerId).update();
			}
		}.runTaskTimer(this, 0L, 100L);
	}
	
	private void loadConfiguration()
	{
		saveDefaultConfig();
		
		databaseHost = this.getConfig().getString("database.host");
		databaseName = this.getConfig().getString("database.database");
		databaseUser = this.getConfig().getString("database.username");
		databasePass = this.getConfig().getString("database.password");
		
		updateServerList = this.getConfig().getBoolean("update-server-list");
		currentServerId = this.getConfig().getInt("server.id");
		
		if(this.getConfig().contains("spawn"))
        {
        	this.spawn = new Location(Bukkit.getWorld(this.getConfig().getString("spawn.World")), this.getConfig().getDouble("spawn.X"), this.getConfig().getDouble("spawn.Y"), this.getConfig().getDouble("spawn.Z"), (float)this.getConfig().getDouble("spawn.Yaw"), (float)this.getConfig().getDouble("spawn.Pitch"));
        }
        else
        {
        	Location default_location = Bukkit.getWorlds().get(0).getSpawnLocation();
        	this.getConfig().set("spawn.World", "world");
        	this.getConfig().set("spawn.X", default_location.getX());
        	this.getConfig().set("spawn.Y", default_location.getY());
        	this.getConfig().set("spawn.Z", default_location.getZ());
        	this.getConfig().set("spawn.Yaw", default_location.getY());
        	this.getConfig().set("spawn.Pitch", default_location.getPitch());
        }
        
        this.saveConfig();
        this.reloadConfig();
	}
	
	@SuppressWarnings("deprecation")
	public void reloadPlayer()
	{
		for(Player pls : Bukkit.getOnlinePlayers())
		{
			core.loadPlayerData(pls);
			pls.teleport(core.getSpawn());
		}
	}
	
	public static Core getCore() { return core; }
	public Database getDataBase() { return database; }
	public HashMap<Integer, Server> getServers() { return servers; }
	
	public ArrayList<Server> getServersByName(String name)
	{
		ArrayList<Server> list = new ArrayList<>();
		for(Entry<Integer, Server> v : this.servers.entrySet())
		{
			Server s = v.getValue();
			if(s.getName().equalsIgnoreCase(name))
			{
				list.add(s);
			}
		}
		
		return list;
	}
	
	public ArrayList<Server> getServersByType(ServerType type)
	{
		ArrayList<Server> list = new ArrayList<>();
		for(Entry<Integer, Server> v : this.servers.entrySet())
		{
			Server s = v.getValue();
			if(s.getType().equals(type))
			{
				list.add(s);
			}
		}
		
		return list;
	}

	public void loadPlayerData(Player p) 
	{
		playersData.put(p, new PlayerData(p));
	}

	public void unloadPlayerData(Player p) 
	{
		playersData.get(p).saveData();
		playersData.remove(p);
	}
	
	public PlayerData getPlayerData(Player p)
	{
		return playersData.get(p);
	}
	
	public Location getSpawn()
	{
		return this.spawn;
	}

}
