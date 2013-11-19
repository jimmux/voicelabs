/*
 * Copyright (c) VoiceLabs (James Manley and Dylan Kelly), 2013
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of VoiceLabs.
 */

package edu.voicelabs.vst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

/**
 * Convenience class to facilitate moving of data to usable locations.
 * Also stores some commonly used constants.
 * 
 * @author James Manley
 * @author Dylan Kelly
 *
 */
public class Utilities {
	
	private Context context;
	public static final boolean DEBUG = true;	// Hard coded to enable/disable debugging behaviour
	public static final int GAME_COUNT = 4;		// Number of games per phoneme
	
	public Utilities(Context context) {
		this.context = context;
	}
	
	/** Copy speech data from the .apk to the filesystem where it can be used. */
	public void setupSpeechData() {
		String base_dir = context.getFilesDir().getAbsolutePath();
		this.copyAssetDirectoryToStorage("speech_data/hmm/en_US/hub4wsj_sc_8k", base_dir + "/hmm");
		this.copyAssetDirectoryToStorage("speech_data/lm/en_US_phonemes_initials", base_dir + "/lm");
		this.copyAssetDirectoryToStorage("speech_data/lm/en_US_phonemes_adjusted", base_dir + "/lm");
		this.copyAssetDirectoryToStorage("speech_data/lm/en_US_words", base_dir + "/lm");
		this.copyAssetDirectoryToStorage("speech_data/lm/filler", base_dir + "/lm");
		try {			
			File log = new File(Environment.getExternalStorageDirectory().getPath(), "pocketsphinx.log");
			log.createNewFile();			
			Log.e(getClass().getName(), "Logging to location " + log.getAbsolutePath());
		}
		catch(IOException e) {
			Log.e(getClass().getName(), "Could not create log file.");
		}
	}
	
	/** Copy all files from the asset directory to the application storage directory */
	private void copyAssetDirectoryToStorage(String assets_dir, String storage_dir) {
		
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
