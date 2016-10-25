/*
*
* This class was made by HyChrod
* All rights reserved, 2016
*
*/
package de.HyChrod.Party.Uti;

import java.util.concurrent.TimeUnit;

import de.HyChrod.Party.Party;
import de.HyChrod.Party.SQL.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class AsyncMySQLReconnecter {
	
	public static ScheduledTask scheduler;
	
	public AsyncMySQLReconnecter() {
		scheduler = BungeeCord.getInstance().getScheduler().schedule(Party.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				System.out.print("PERMORF : DISCONNECT");
				while(MySQL.isConnected()) {
					MySQL.disconnect();
				}
				System.out.print("PERFORM : CONNECT");
				while(!MySQL.isConnected()) {
					MySQL.connect();
				}
				System.out.println("PERFORM : END");
			}
		}, 20*60*5, 20*60*5, TimeUnit.SECONDS);
	}

}