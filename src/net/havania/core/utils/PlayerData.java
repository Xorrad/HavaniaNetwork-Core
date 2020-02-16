package net.havania.core.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import net.havania.core.Core;
import net.havania.core.utils.scoreboard.CustomScoreboard;

public class PlayerData {
	
	public Player player;
	public Rank rank;
	public Integer rushPoint;
	public Integer rushKills;
	public Integer rushDeaths;
	public Integer rushBeds;
	public Integer rushWins;
	public CustomScoreboard scoreboard;
	
	public PlayerData(Player player) {
		this.player = player;
		this.rank = Rank.PLAYER;
		
		reloadData();
		loadData();
		
		if(!isExist())
		{
			createData();
		}
	}
	
	public void loadData()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("SELECT * FROM players WHERE uuid = ?");
			preparedStatement.setString(1, player.getUniqueId().toString());
			ResultSet result = preparedStatement.executeQuery();
			
			if(!result.next()) {
				return;
			}
			
			//SETUP VARIABLES
			this.rank = Rank.valueOf(result.getString(4));
			this.rushPoint = result.getInt(5);
			this.rushKills = result.getInt(6);
			this.rushDeaths = result.getInt(7);
			this.rushBeds = result.getInt(8);
			this.rushWins = result.getInt(9);
			
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createData()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("INSERT INTO players (uuid, username, rank) VALUES (?, ?, ?)");
			preparedStatement.setString(1, player.getUniqueId().toString());
			preparedStatement.setString(2, player.getName());
			preparedStatement.setString(3, "PLAYER");
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadData()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("UPDATE players SET username = ? WHERE uuid = ?");
			preparedStatement.setString(1, player.getName());
			preparedStatement.setString(2, player.getUniqueId().toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isExist()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("SELECT id FROM players WHERE uuid = ?");
			preparedStatement.setString(1, player.getUniqueId().toString());
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
	
	public void saveData()
	{
		PreparedStatement preparedStatement;
		try {
			preparedStatement = Core.getCore().getDataBase().getConnection().prepareStatement("UPDATE players SET rank = ?, rushPoint = ?, rushKills = ?, rushDeaths = ?, rushBeds = ?, rushWins = ? WHERE uuid = ?");
			preparedStatement.setString(1, this.rank.name());
			preparedStatement.setInt(2, this.rushPoint);
			preparedStatement.setInt(3, this.rushKills);
			preparedStatement.setInt(4, this.rushDeaths);
			preparedStatement.setInt(5, this.rushBeds);
			preparedStatement.setInt(6, this.rushWins);
			preparedStatement.setString(7, player.getUniqueId().toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public void setRank(Rank rank)
	{
		this.rank = rank;
	}
	
	public Rank getRank()
	{
		return this.rank;
	}
	
	public Integer getRushPoint() {
		return rushPoint;
	}

	public void setRushPoint(Integer rushPoint) {
		this.rushPoint = rushPoint;
	}
	
	public void addRushPoint(Integer value)
	{
		this.rushPoint += value;
	}
	
	public Integer getRushKills() {
		return rushKills;
	}

	public void setRushKills(Integer rushKills) {
		this.rushKills = rushKills;
	}
	
	public void addRushKills(Integer rushKills) {
		this.rushKills += rushKills;
	}

	public Integer getRushDeaths() {
		return rushDeaths;
	}

	public void setRushDeaths(Integer rushDeaths) {
		this.rushDeaths = rushDeaths;
	}
	
	public void addRushDeaths(Integer rushDeaths) {
		this.rushDeaths += rushDeaths;
	}

	public Integer getRushBeds() {
		return rushBeds;
	}

	public void setRushBeds(Integer rushBeds) {
		this.rushBeds = rushBeds;
	}
	
	public void addRushBeds(Integer rushBeds) {
		this.rushBeds += rushBeds;
	}

	public Integer getRushWins() {
		return rushWins;
	}

	public void setRushWins(Integer rushWins) {
		this.rushWins = rushWins;
	}
	
	public void addRushWins(Integer rushWins) {
		this.rushWins += rushWins;
	}

	public CustomScoreboard getScoreboard()
	{
		return this.scoreboard;
	}
}
