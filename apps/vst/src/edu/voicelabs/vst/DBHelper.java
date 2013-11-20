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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Convenience class for management of persistent data store in an SQLite database
 * 
 * @author James Manley
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "vst";
	
	/** 
	 * Populate the database with default data, if not done already.
	 * 
	 * @param force If true, recreates the data even if it already exists
	 */
	public void initialiseWithDefaults(boolean force) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		if (force) {
			onUpgrade(db, 0, 0);
		}
		
		Cursor cursor = db.rawQuery("select * from Profile", null);
		if (cursor == null) {
			Log.d(getClass().getName(), "Could not get data. Database available?");
			return;
		}
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
		
	    	ContentValues values;
	     	
	    	// Profile
	    	values = new ContentValues();
	        values.put("Name", "Default");
	        db.insert("Profile", null, values);
	        
	        // Phoneme
	    	values = new ContentValues();
	        values.put("ARPAbet", "L");
	        db.insert("Phoneme", null, values);
	        values.put("ARPAbet", "S");
	        db.insert("Phoneme", null, values);
	        values.put("ARPAbet", "CH");
	        db.insert("Phoneme", null, values);
	        
	        // Stage
	    	values = new ContentValues();
	        values.put("Name", "Phoneme");
	        db.insert("Stage", null, values);
	        values.put("Name", "Syllable");
	        db.insert("Stage", null, values);
	        values.put("Name", "Word");
	        db.insert("Stage", null, values);
	        values.put("Name", "Choose");
	        db.insert("Stage", null, values);
	        
	        // Progress
	    	values = new ContentValues();
	        values.put("ID_Profile", 1);
	        values.put("ID_Phoneme", 1);
	        values.put("ID_Stage", 1);
	        values.put("Complete", false);
	        db.insert("Progress", null, values);
	        values.put("ID_Profile", 1);
	        values.put("ID_Phoneme", 1);
	        values.put("ID_Stage", 2);
	        values.put("Complete", false);
	        db.insert("Progress", null, values);
	        values.put("ID_Profile", 1);
	        values.put("ID_Phoneme", 1);
	        values.put("ID_Stage", 3);
	        values.put("Complete", false);
	        db.insert("Progress", null, values);
	        values.put("ID_Profile", 1);
	        values.put("ID_Phoneme", 1);
	        values.put("ID_Stage", 4);
	        values.put("Complete", false);
	        db.insert("Progress", null, values);
	    }
        db.close();
	}

	/** Constructor */
	public DBHelper(Context context) {
		super(context, DBHelper.DATABASE_NAME, null, 1 /* DB version */);
	}

	/** Define the database with table creation commands, run when a new database is needed. */
	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL(
			" create table Profile ( " +
			" 	ID_Profile integer primary key, " +
			"   Name char(20) " +
			" ) "
		);
		db.execSQL(
			" create table Progress ( " +
			"   ID_Progress integer primary key, " +
			"   ID_Profile integer, " +
			"   ID_Phoneme integer, " +
			"   ID_Stage integer, " +
			"   Complete boolean " +
			" ) "
		);
		db.execSQL(
			" create table Phoneme ( " +
			" 	ID_Phoneme integer primary key, " +
			"   ARPAbet char(4) " +
			" ) "
		);
		db.execSQL(
			" create table Stage ( " +
			" 	ID_Stage integer primary key, " +
			"   Name char(40) " +
			" ) "
		);
	}

	/** Prepare for a new database schema by destroying the old schema */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Should never happen, but need to recreate if necessary, 
		// despite this destroying all data.
		
		// Drop older table if existed
        db.execSQL(" drop table if exists Profile ");
        db.execSQL(" drop table if exists Progress ");
        db.execSQL(" drop table if exists Phoneme ");
        db.execSQL(" drop table if exists Stage ");
 
        // Create tables again
        onCreate(db);
	}
	
	/**
	 * Get the number of games completed for a given phoneme.
	 * 
	 * @param profileName This will always be "Default", until multiple user profiles are implemented in the future
	 * @param phoneme
	 * @return
	 */
	public int getProgressCount(String profileName, String phoneme) {
		SQLiteDatabase db = this.getReadableDatabase();
		int result = 0;
		
		Cursor cursor = db.rawQuery(
			" select count(ID_Progress) " +
			" from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? " +
			" and Progress.Complete = 1 ",
			new String[] {profileName, phoneme}
		);
		cursor.moveToFirst();
		if (cursor != null && cursor.getCount() > 0) {
			result = cursor.getInt(0);
		}
		else {
			Log.e(getClass().getName(), "Could not get progress.");
		}
		cursor.close();
		return result;
	}
	
	/**
	 * Get the completion status for a given phoneme and game, e.g. "L", "Syllable".
	 * 
	 * @param profileName 
	 * @param phoneme
	 * @param stage
	 * @return
	 */
	public boolean getProgress(String profileName, String phoneme, String stageName) {
		SQLiteDatabase db = this.getReadableDatabase();
		boolean result = false;
		
		Cursor cursor = db.rawQuery(
			" select Progress.Complete " +
			" from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" join Stage on Progress.ID_Stage = Stage.ID_Stage " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? " +
			" and Stage.Name = ? ",
			new String[] {profileName, phoneme, stageName}
		);
		cursor.moveToFirst();
		if (cursor != null && cursor.getCount() > 0) {
			result = (cursor.getInt(0) != 0);
		}
		else {
			Log.e(getClass().getName(), "Could not get progress.");
		}
		cursor.close();
		return result;		
	}
	
	/**
	 * Tell us if all the games are completed for a phoneme.
	 * 
	 * @param profileName
	 * @param phoneme
	 * @return
	 */
	public boolean getComplete(String profileName, String phoneme) {
		return getProgressCount(profileName, phoneme) >= Utilities.GAME_COUNT;
	}
	
	
	/**
	 * Set the progress for a given phoneme and game to complete.
	 * 
	 * @param profileName
	 * @param phoneme
	 * @param stageName
	 */
	public void setProgress(String profileName, String phoneme, String stageName) {
		int id_progress;
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor;
		
	    // Get the id of the row to update
		cursor = db.rawQuery(
			" select Progress.ID_Progress " +
			" from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" join Stage on Progress.ID_Stage = Stage.ID_Stage " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? " +
			" and Stage.Name = ? ", 
			new String[] {profileName, phoneme, stageName}
		);
		cursor.moveToFirst();  // Should always get a result
		if (cursor != null && cursor.getCount() > 0) {
			id_progress = cursor.getInt(0);
		}
		else {
			Log.e(getClass().getName(), "Could not update progress.");
			return;
		}

		ContentValues values = new ContentValues();
	    values.put("Complete", 1 /* True */);
	    db.update(
	    	"Progress", 
	    	values, 
	    	"ID_Progress = ?",
	    	new String[] {Integer.toString(id_progress)}
	    );
	    
		cursor.close();
	}
	
}
