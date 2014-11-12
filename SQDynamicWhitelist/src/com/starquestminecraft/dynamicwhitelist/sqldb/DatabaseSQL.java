package com.starquestminecraft.dynamicwhitelist.sqldb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.starquestmineraft.dynamicwhitelist.Database;
import com.starquestmineraft.dynamicwhitelist.Whitelister;

public final class DatabaseSQL extends Database{
	
	public static String driver = "com.mysql.jdbc.Driver";
	public static String hostname = "localhost";
	public static String port = "3306";
	public static String db_name = "minecraft";
	public static String username = "minecraft";
	public static String password = "R3b!rth!ng";
	public static String dsn = ("jdbc:mysql://" + hostname + ":" + port + "/" + db_name);
	public static Connection cntx = null;
	
	static{
		// called from onEnable

		String bedspawn_table = "CREATE TABLE IF NOT EXISTS " + "PRIORITY (" + "`uuid` VARCHAR(32) NOT NULL," + "`hourspurchased` int(11) DEFAULT 0," + "`timelastpurchased` BIGINT DEFAULT 0," + "`permanent` BIT DEFAULT 0," + "PRIMARY KEY (`uuid`)" + ")";
		getContext();
		try {
			Driver driver = (Driver) Class.forName(DatabaseSQL.driver).newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			System.out.println("[SQBedSpawns] Driver error: " + e);
		}
		Statement s = null;
		try {
			s = cntx.createStatement();
			s.executeUpdate(bedspawn_table);
			s.close();
			System.out.println("[SQBedSpawn] Table check/creation sucessful");
		} catch (SQLException ee) {
			System.out.println("[SQBedSpawn] Table Creation Error");
		} finally {
			close(s);
		}

	}
	@Override
	public void addPremiumTime(final UUID u, final int hours) {
		Whitelister.getInstance().getProxy().getScheduler().runAsync(Whitelister.getInstance(), new Runnable(){
			public void run(){
				addPremiumTimeAsync(u, hours);
			}
		});
	}
	
	private void addPremiumTimeAsync(UUID u, int hours){
		PreparedStatement s = null;
		try {
			s = cntx.prepareStatement("UPDATE PRIORITY SET `hourspurchased` = ? WHERE `uuid` = ?");
			s.setLong(1, hours);
			s.setString(2, u.toString());
			s.execute();
		} catch (SQLException e) {
			System.out.print("[SQBedSpawn] SQL Error" + e.getMessage());
		} catch (Exception e) {
			System.out.print("[SQBedSpawn] SQL Error (Unknown)");
			e.printStackTrace();
		} finally {
			close(s);
		}
	}

	@Override
	public void addPremiumTime(final String playername, final int hours) {
		Whitelister.getInstance().getProxy().getScheduler().runAsync(Whitelister.getInstance(), new Runnable(){
			public void run(){
				addPremiumTimeAsync(playername, hours);
			}
		});
	}
	
	private void addPremiumTimeAsync(String playername, int hours){
		UUID u = super.uuidFromUsername(playername);
		addPremiumTimeAsync(u, hours);
	}

	@Override
	public int getRemainingTime(UUID u) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasPlayedBefore(UUID u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerNewPlayer(UUID u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPermanent(UUID u, boolean permanent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPermanent(UUID u) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static boolean getContext() {

		try {
			if (cntx == null || cntx.isClosed() || !cntx.isValid(1)) {
				if (cntx != null && !cntx.isClosed()) {
					try {
						cntx.close();
					} catch (SQLException e) {
						System.out.print("Exception caught");
					}
					cntx = null;
				}
				if ((DatabaseSQL.username.equalsIgnoreCase("")) && (DatabaseSQL.password.equalsIgnoreCase(""))) {
					cntx = DriverManager.getConnection(DatabaseSQL.dsn);
				} else
					cntx = DriverManager.getConnection(DatabaseSQL.dsn, DatabaseSQL.username, DatabaseSQL.password);

				if (cntx == null || cntx.isClosed())
					return false;
			}

			return true;
		} catch (SQLException e) {
			System.out.print("Error could not Connect to db " + DatabaseSQL.dsn + ": " + e.getMessage());
		}
		return false;
	}
	
	private static void close(Statement s){
		if(s == null) return;
		try{
			s.close();
			System.out.println("[Movecraft] Closing statement");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}