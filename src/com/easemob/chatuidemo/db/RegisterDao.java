package com.easemob.chatuidemo.db;
/**
 * @author AirWizardWong
 * @time 2014年12月17日11:40:38
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easemob.chatuidemo.domain.RegisteredUser;

import java.util.ArrayList;
import java.util.List;


public class RegisterDao {
	
	public static final String TABLE_NAME = "register_user_list";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_REGISTERED_USER_NAME = "userName";

	private DbOpenHelper dbHelper;

	public RegisterDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 
	 * @param registeredUserList
	 */
	public void saveregisteredUserList(List<RegisteredUser> registeredUserList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (RegisteredUser registeredUser : registeredUserList) {
				ContentValues values = new ContentValues();

				values.put(COLUMN_NAME_REGISTERED_USER_NAME, registeredUser.getRegisteredUserName());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * @return
	 */
	public List<RegisteredUser> getregisteredUserList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<RegisteredUser> registeredUserList = new ArrayList<RegisteredUser>();
		//Map<String, Topic> topics = new HashMap<String, Topic>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String registeredUserName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REGISTERED_USER_NAME));
				RegisteredUser registeredUser = new RegisteredUser();
//				Log.e("topicId", topicId);
//				Log.e("topicName", topicName);
				registeredUser.setRegisteredUserName(registeredUserName);
				registeredUserList.add(registeredUser);
				
				//topics.put("topics", topic);
			}
			cursor.close();
		}
		return registeredUserList;
	}
	
	
	/**
	 * @return
	 */
	public List<RegisteredUser> getregisteredUserList(String key) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<RegisteredUser> registeredUserList = new ArrayList<RegisteredUser>();
		if (db.isOpen()) {
			//"SELECT * FROM " + TABLE_NAME +" WHERE "+COLUMN_NAME_TOPIC+" LIKE '%"+key +"%'"
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE "+COLUMN_NAME_REGISTERED_USER_NAME+" = ?", new String[]{key});
			while (cursor.moveToNext()) {
				String registeredUserName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REGISTERED_USER_NAME));
		/*		Log.e("topicId", topicId);
				Log.e("topicName", topicName);*/
				RegisteredUser registeredUser = new RegisteredUser();
				registeredUser.setRegisteredUserName(registeredUserName);
				registeredUserList.add(registeredUser);

			}
			cursor.close();
		}
		return registeredUserList;
	}

	public RegisteredUser getregisteredUser(String key) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		RegisteredUser registeredUser = null;
		if (db.isOpen()) {
			//"SELECT * FROM " + TABLE_NAME +" WHERE "+COLUMN_NAME_TOPIC+" LIKE '%"+key +"%'"
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_REGISTERED_USER_NAME + " LIKE '" + key + "%'" + " OR " + COLUMN_NAME_REGISTERED_USER_NAME + " LIKE '% " + key + "%'"/* + " desc" */, null);
			while (cursor.moveToNext()) {
				String registeredUserName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REGISTERED_USER_NAME));
		/*		Log.e("topicId", topicId);
				Log.e("topicName", topicName);*/
				registeredUser = new RegisteredUser();
				registeredUser.setRegisteredUserName(registeredUserName);

			}
			cursor.close();
		}
		return registeredUser;
	}

	
/*
	*//**
	 * 删除一门课程
	 * @param topicName
	 *//*
	public void deleteTopic(String topicName){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_REGISTERED_USER_NAME + " = ?", new String[]{topicName});
		}
	}*/
	
	
	/**
	 * 保存
	 * @param
	 */
	public void saveRegisteredUser(RegisteredUser registeredUser){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_REGISTERED_USER_NAME, registeredUser.getRegisteredUserName());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
}
