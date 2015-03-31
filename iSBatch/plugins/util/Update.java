package util;

import java.awt.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ij.IJ;
import ij.Menus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class Update implements PlugIn {
	String[] protocols = {"ftp", "http"};
	String protocol = Prefs.getString("Update.protocol", "ftp");
	String host = Prefs.getString("Update.host", "singlemolecule.nl");
	String path = Prefs.getString("Update.path", "new/single_molecule_biophysics.jar");
	String username = Prefs.getString("Update.username", "plugins");
	String password = Prefs.getString("Update.password", "ImageJSMB!");
	
	@Override
	public void run(String arg0) {
		
		GenericDialog dialog = new GenericDialog("Update");
		dialog.addChoice("Protocol", protocols, protocol);
		dialog.addStringField("Host", host, 30);
		dialog.addStringField("Path", path, 30);
		dialog.addStringField("Username", username);
		dialog.addStringField("Password", password);
		
		// make sure we don't see the password
		TextField passwordTextField = (TextField)dialog.getStringFields().get(3);
		passwordTextField.setEchoChar('*');
		
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		protocol = dialog.getNextChoice();
		host = dialog.getNextString();
		path = dialog.getNextString();
		username = dialog.getNextString();
		password = dialog.getNextString();
		
		String filename = new File(path).getName();
		String url = String.format("%s://%s:%s@%s/%s", protocol, username, password, host, path);
		
		try {
			InputStream is = new URL(url).openStream();
			
			File jarFile = new File(Menus.getPlugInsPath(), filename);
			FileOutputStream os = new FileOutputStream(jarFile);
			
			byte[] buffer = new byte[1024];
			int bytesInBuffer;
			int bytesReceived = 0;
			
			while ((bytesInBuffer = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesInBuffer);
				bytesReceived += bytesInBuffer;
				
				IJ.showStatus("" + bytesReceived + " bytes received"); 
			}
			
			Menus.updateImageJMenus();
			
			IJ.showMessage("Plugin saved as " + jarFile.getPath());
			
			is.close();
			os.close();
		}
		catch (IOException e) {
			IJ.showMessage(e.getMessage());
		}
	}

	
	
}
