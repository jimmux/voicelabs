package edu.voicelabs.vst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Utilities {
	
	public Context context;
	
	public Utilities(Context context) {
		this.context = context;
	}
	
	/** Copy all files from the asset directory to the application storage directory */
	public void CopyAssetDirectoryToStorage(String assets_dir, String storage_dir) {
		
		// Make sure the destination directory is created.
		File target_dir = new File(storage_dir);
		target_dir.mkdirs();
		
		// Copy code is adapted from example at http://www.technotalkative.com/android-copy-files-from-assets-to-sd-card/
		AssetManager asset_manager = context.getAssets();
        String[] files = null;
        try {
            files = asset_manager.list(assets_dir);
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
            	in = asset_manager.open(assets_dir + "/" + filename);
            	out = new FileOutputStream(new File(target_dir, filename));
            	
	  	        byte[] buffer = new byte[1024];
		        int read;
		        while((read = in.read(buffer)) != -1){
		          out.write(buffer, 0, read);
		        }         
		        in.close();
		        in = null;
		        out.flush();
		        out.close();
		        out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }
        }	
	}
}
