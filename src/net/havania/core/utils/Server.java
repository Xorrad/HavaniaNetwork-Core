package net.havania.core.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.havania.core.Core;
import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;

public class Server {
	
	public Integer id;
	public ServerType type;
	public ServerStatus status;
	public String name;
	public String bungeeName;
	public Integer maxPlayer;
	public Integer currentPlayer;
	
	public Server(Integer id, ServerType type, ServerStatus status, String name, String bungeeName, Integer maxPlayer) 
	{
		this.id = id;
		this.type = type;
		this.status = status;
		this.name = name;
		this.bungeeName = bungeeName;
		this.maxPlayer = maxPlayer;
	}
	
	public Server(Integer id, ServerType type, ServerStatus status, String name, String bungeeName, Integer maxPlayer, Integer currentPlayer) 
	{
		this.id = id;
		this.type = type;
		this.status = status;
		this.name = name;
		this.bungeeName = bungeeName;
		this.maxPlayer = maxPlayer;
		this.currentPlayer = currentPlayer;
	}
	
	public Server() //Current Server
	{
		loadData();
		this.status = ServerStatus.READY;
	}

	public Integer getId()
	{
		return this.id;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public ServerType getType() {
		return type;
	}

	public void setType(ServerType type) {
		this.type = type;
	}

	public ServerStatus getStatus() {
		return status;
	}

	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBungeeName() {
		return bungeeName;
	}

	public void setBungeeName(String bungeeName) {
		this.bungeeName = bungeeName;
	}

	public Integer getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(Integer maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public Integer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Integer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public void connectPlayer(Player p)
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(this.bungeeName);
		p.sendPluginMessage(Bukkit.getPluginManager().getPlugin("HavaniaCore"), "BungeeCord", out.toByteArray());
	}
	
	public void update()
	{
		loadData();
		updateToDatabase();
	}
	
	@SuppressWarnings("deprecation")
	public void loadData()
	{
		this.id = Core.getCore().getConfig().getInt("server.id");
		this.type = Server.ServerType.valueOf(Core.getCore().getConfig().getString("server.type"));
		this.name = Core.getCore().getConfig().getString("server.name");
		this.bungeeName = Core.getCore().getConfig().getString("server.bungee-name");
		this.maxPlayer = Core.getCore().getConfig().getInt("server.size");
		this.currentPlayer = Bukkit.getOnlinePlayers().length;
	}
	
	public void updateToDatabase()
	{
		if(isInDatabase())
		{
			PreparedStatement preparedStatement;
			try {
				preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("UPDATE servers SET status = ?, current_player = ? WHERE id = ?");
				preparedStatement.setString(1, this.status.name());
				preparedStatement.setInt(2, this.currentPlayer);
				preparedStatement.setInt(3, this.id);
				
				preparedStatement.executeUpdate();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			PreparedStatement preparedStatement;
			try {
				preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("INSERT INTO servers (id, type, status, name, bungee_name, max_player, current_player) VALUES (?, ?, ?, ?, ?, ?, ?)");
				preparedStatement.setInt(1, this.id);
				preparedStatement.setString(2, this.type.name());
				preparedStatement.setString(3, this.status.name());
				preparedStatement.setString(4, this.name);
				preparedStatement.setString(5, this.bungeeName);
				preparedStatement.setInt(6, this.maxPlayer);
				preparedStatement.setInt(7, this.currentPlayer);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void suspend()
	{
		this.status = ServerStatus.CLOSE;
		updateToDatabase();
	}
	
	public void close()
	{
		if(isInDatabase())
		{
			PreparedStatement preparedStatement;
			try {
				preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("DELETE FROM servers WHERE id = ?");
				preparedStatement.setInt(1, this.id);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isInDatabase()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("SELECT id FROM servers WHERE id = ?");
			preparedStatement.setInt(1, this.getId());
			ResultSet result = preparedStatement.executeQuery();
			
			if(result.next()) {
				preparedStatement.close();
				return true;
			}
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public enum ServerType 
	{
		RUSH,
		PRACTICE,
		LOBBY
	}

	public enum ServerStatus 
	{
		READY,
		PLAYING,
		RESTARTING,
		CLOSE,
	}
}
