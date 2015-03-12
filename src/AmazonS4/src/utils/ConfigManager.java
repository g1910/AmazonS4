package utils;

import gui.MainGui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigManager {
	public static Properties getProperties(){
		Properties prop = new Properties();
		String filename = "config.properties";

		InputStream input = null;
		input = ConfigManager.class.getClassLoader()
				.getResourceAsStream(filename);
		
		if (input == null) {
			MainGui.log("Sorry, unable to find " + filename + "\nTry connecting using new Access Keys");

		} else {
			//input from config.properties is valid
			//establishing connection with the database
			try {
				//loading the properties
				prop.load(input);
				//System.out.println(prop.getProperty("ipport"));
				//System.out.println(prop.getProperty("database"));
				//System.out.println(prop.getProperty("user"));
				//System.out.println(prop.getProperty("password"));
				
				return prop;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
	
	public static void setProperties(Properties prop){
		try {
			OutputStream output=new FileOutputStream("./src/config.properties");
			prop.store(output, null);
			if(output!=null){
				output.flush();
				output.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
