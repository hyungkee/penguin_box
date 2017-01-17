package heuristy.ALLA.penguinbox;

//userid (자신:1, 타인 1001~)
//image
//name
//number
//isBoxOpen
//latitude, longitude
//comstate : wifi(0), LTE(1), 근거리 통신(2)
//u_version : 현재 프로필이 몇번쨰 업데이트인가.
//isNew : 그래서 지금 하이라이트 상태인가.(새로운 프사인가.) : 0(아님) 1(새로운프사) 2(새로운사람)


import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.DropBoxManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class SQLManager extends SQLiteOpenHelper {

	// Member Variables
	static Context mContext;
	static String mTableName;

	// Constructor
	public SQLManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	// Override Methods
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ mTableName
				+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER, image BLOB, name TEXT, number TEXT, isBoxOpen INTEGER, latitude TEXT, longitude TEXT, comstate INTEGER, u_version INTEGER, isNew INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}


	// Instance Methods

	// 이녀석은 없어져야 한다.
	public static int newUserId() {
		int max = 100;
		for (int i = 0; i < DBStorage.getPeopleList().getCount(); i++) {
			Person person = (Person) DBStorage.getPeopleList().getList().elementAt(i);
			if (max < person.getUserId())
				max = person.getUserId();
		}
		return max + 1;
	}

	public boolean isEmpty() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + mTableName, null);
		if (cursor.getCount()==0) { // 데이터가 없다.
			db.close();
			return false;
		}
		db.close();
		return true;
	}

	public String getTableName() {
		return mTableName;
	}

	public void setTableName(String pTableName) {
		mTableName = pTableName;
	}


	public void insert(ContentValues recordValue) {
		// DB에 추가
		SQLiteDatabase db = DBStorage.getSQLManager().getWritableDatabase();
		db.insert(mTableName, null, recordValue);
		db.close();
	}
	
	public void update(int userid, ContentValues recordValue) {
		// DB에 갱신
		SQLiteDatabase db = DBStorage.getSQLManager().getWritableDatabase();
		String[] args = { Integer.toString(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}
	
	public void update(int userid, Person person) {
		// DB에 갱신
		SQLiteDatabase db = DBStorage.getSQLManager().getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("name", person.getName());
		recordValue.put("number", person.getNumber());
		recordValue.put("comstate", person.getComState());
		recordValue.put("isBoxOpen", person.getIsBoxOpen());
		recordValue.put("isNew", person.getIsNew());
		recordValue.put("latitude", Float.toString(person.getLatitude()));
		recordValue.put("longitude", Float.toString(person.getLongitude()));
		recordValue.put("u_version", person.getUploadVersion());
		byte[] imageInByte = Tools.convertBitmapToBlobs(person.getBitmap());
		recordValue.put("image", imageInByte);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}
	

	public void updateName(int userid, String name) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("name", name);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateNumber(int userid, String number) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("number", number);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateIsBoxOpen(int userid, int isBoxOpen) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("isBoxOpen", isBoxOpen);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updatesLongitude(int userid, float longitude) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("longitude", Float.toString(longitude));
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updatesLatitude(int userid, float latitude) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("latitude", Float.toString(latitude));
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateComstate(int userid, int comstate) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("comstate", comstate);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateUploadVersion(int userid, int u_version) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("u_version", u_version);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateIsNew(int userid, int isNew) {
		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		recordValue.put("isNew", isNew);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void updateBitmap(int userid, Bitmap bitmap) {

		SQLManager sqlManager = DBStorage.getSQLManager();
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		ContentValues recordValue = new ContentValues();
		byte[] imageInByte = Tools.convertBitmapToBlobs(bitmap);
		recordValue.put("image", imageInByte);
		String[] args = { String.valueOf(userid) };
		db.update(mTableName, recordValue, "userid = ?", args);
		db.close();
	}

	public void delete(int userid) {
		SQLiteDatabase db = DBStorage.getSQLManager().getWritableDatabase();
		String[] args = { String.valueOf(userid) };
		db.delete(mTableName, "userid = ?", args);
		db.close();
	}
}
