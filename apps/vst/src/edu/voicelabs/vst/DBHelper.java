package edu.voicelabs.vst;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "vst";
	
	/** 
	 * Populate the database with default data, if not done already.
	 */
	public void InitialiseWithDefaults() {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery("select * from Profile", null);
	    if (cursor == null) {
	    	ContentValues values;
	    	
	    	// Profile
	    	values = new ContentValues();
	        values.put("Name", "Default");
	        db.insert("Profile", null, values);
	        
	        // Phoneme
	    	values = new ContentValues();
	        values.put("ARPAbet", "L");
	        values.put("ARPAbet", "S");
	        values.put("ARPAbet", "CH");
	        db.insert("Phoneme", null, values);
	        
	        // Stage
	    	values = new ContentValues();
	        values.put("Name", "Phoneme");
	        values.put("Name", "Syllable");
	        values.put("Name", "Word");
	        values.put("Name", "Choose");
	        db.insert("Stage", null, values);
	        
	        // Progress
	    	values = new ContentValues();
	        values.put("ID_Profile", "0");
	        values.put("ID_Phoneme", "0");
	        values.put("ID_Stage", "0");
	        values.put("Complete", "0");
	        db.insert("Progress", null, values);
	        values.put("ID_Profile", "0");
	        values.put("ID_Phoneme", "1");
	        values.put("ID_Stage", "0");
	        values.put("Complete", "0");
	        db.insert("Progress", null, values);
	        values.put("ID_Profile", "0");
	        values.put("ID_Phoneme", "2");
	        values.put("ID_Stage", "0");
	        values.put("Complete", "0");
	        db.insert("Progress", null, values);
	    }
	    
        db.close();
	}

	public DBHelper(Context context) {
		super(context, DBHelper.DATABASE_NAME, null, 1 /* DB version */);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL(
			" create table Profile ( " +
			" 	ID_Profile integer primary key, " +
			"   Name char(20) " +
			" ); "
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
	
	public String getProgress(String profileName, String phoneme) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(
			" select Stage.Name from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" join Stage on Progress.ID_Stage = Stage.ID_Stage " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? ", 
			new String[] {profileName, phoneme}
		);
		cursor.moveToFirst();  // Should always get a result
		
		return cursor.getString(0);
	}
	
	public boolean getComplete(String profileName, String phoneme) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(
			" select Progress.Complete from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? ", 
			new String[] {profileName, phoneme}
		);
		cursor.moveToFirst();  // Should always get a result
		
		return (cursor.getInt(0) != 0);
	}
	
	
	public void setProgress(String profileName, String phoneme, String stageName) {
		int id_progress;
		int id_stage;
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor;
		
		cursor = db.rawQuery(
			" select Progress.ID_Progress from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? ", 
			new String[] {profileName, phoneme}
		);
		cursor.moveToFirst();  // Should always get a result
		id_progress = cursor.getInt(0);

		cursor = db.rawQuery(
			" select ID_Stage from Stage where Name = ? ", 
			new String[] {stageName}
		);
		cursor.moveToFirst();  // Should always get a result
		id_stage = cursor.getInt(0);
		
		ContentValues values = new ContentValues();
	    values.put("ID_Stage", id_stage);
	    db.update(
	    	"Progress", 
	    	values, 
	    	"ID_Progress = ?",
	    	new String[] {Integer.toString(id_progress)}
	    );
	}
	

	public void setComplete(String profileName, String phoneme) {
		int id_progress;
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor;
		
		cursor = db.rawQuery(
			" select Progress.ID_Progress from Progress " +
			" join Profile on Progress.ID_Profile = Profile.ID_Profile " +
			" join Phoneme on Progress.ID_Phoneme = Phoneme.ID_Phoneme " +
			" where Profile.Name = ? " +
			" and Phoneme.ARPAbet = ? ", 
			new String[] {profileName, phoneme}
		);
		cursor.moveToFirst();  // Should always get a result
		id_progress = cursor.getInt(0);
		
		ContentValues values = new ContentValues();
	    values.put("Complete", 1 /* True */);
	    db.update(
	    	"Progress", 
	    	values, 
	    	"ID_Progress = ?",
	    	new String[] {Integer.toString(id_progress)}
	    );
	}
}
