package fr.badblock.bukkit.games.shootflag.flags;

public enum FlagType
{

	DELTA	("Delta"),
	CHARLIE	("Charlie"),
	BRAVO	("Bravo"),
	OMEGA	("Omega"),
	ALPHA	("Alpha"),
	ECHO	("Echo");
	
	String name;
	
	FlagType (String name)
	{
		this.name = name;
	}
	
}
