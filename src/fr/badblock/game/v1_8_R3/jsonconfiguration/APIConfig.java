package fr.badblock.game.v1_8_R3.jsonconfiguration;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class APIConfig {
	
	// Ladder part
	public String 	ladderIp = "";
	public int	  	ladderPort;

	// SQL Part
	public String 	sqlIp = "";
	public int    	sqlPort;
	public String 	sqlDatabase = "";
	public String 	sqlPassword = "";
	
	// FTP part
	public String 	ftpHostname = "";
	public String 	ftpUsername = "";
	public String 	ftpPassword = "";
	public int	  	ftpPort;
	
	// Rabbit part
	public String	rabbitHostname;
	public int		rabbitPort;
	public String	rabbitUsername;
	public String	rabbitPassword;
	public String	rabbitVirtualHost;
	
	// GameServer part
	public boolean	deleteFiles;
	public int		timeBetweenLogs;
	public int		ticksBetweenMonitoreLogs;
	public int		ticksBetweenKeepAlives;
	public long		uselessUntilTime;
	
}