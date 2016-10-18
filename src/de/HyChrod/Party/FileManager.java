/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import de.HyChrod.Party.SQL.MySQL;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FileManager {
	
	public static Configuration ConfigCfg, MessagesCfg;
	public static File ConfigFile, MessagesFile;
	
	private static File getFile(String path, String name) {
		return new File("plugins/Party" + path, name);
	}
	
	private static Configuration getConfig(String path, String name) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(path, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Configuration getConfig(File file) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void readMySQLData() {
		Configuration cfg = getConfig("", "config.yml");
		
		MySQL.host = cfg.getString("Party.Friends2_0.MySQL.Host");
		MySQL.port = cfg.getString("Party.Friends2_0.MySQL.Port");
		MySQL.database = cfg.getString("Party.Friends2_0.MySQL.Database");
		MySQL.username = cfg.getString("Party.Friends2_0.MySQL.Username");
		MySQL.passwort = cfg.getString("Party.Friends2_0.MySQL.Password");
	}
	
	public static void relaodConfigs() {
		ConfigFile = getFile("", "config.yml");
		ConfigCfg = getConfig(ConfigFile);
		MessagesFile = getFile("", "Messages.yml");
		MessagesCfg = getConfig(MessagesFile);
	}
	
	public void createFolders(Party plugin) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		File MessagesFile = getFile("", "Messages.yml");
		File ConfigFile = getFile("", "config.yml");
		
		if (!ConfigFile.exists()) {
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, ConfigFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		if (!MessagesFile.exists()) {
            try (InputStream in = plugin.getResourceAsStream("Messages.yml")) {
                Files.copy(in, MessagesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
		relaodConfigs();
	}
	
}
