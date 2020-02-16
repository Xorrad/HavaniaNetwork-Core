package net.havania.core.utils;

public enum Rank {
	
	PLAYER("§fJoueur"),
	MODERATOR("§2Moderateur"),
	MANAGER("§4Respon.Moderateur"),
	ADMIN("§cAdmin"),
	DEVELOPPER("§bDeveloppeur"),
	OWNER("§l§4Owner");
	
	public String tag;
	
	Rank(String tag)
	{
		this.tag = tag;
	}
	
	public String getTag()
	{
		return this.tag;
	}
	
	public boolean isUpper(Rank rank)
	{
		return (this.ordinal() >= rank.ordinal());
	}

}
