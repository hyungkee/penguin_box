package heuristy.ALLA.penguinbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class DBStorage {
	// Constants
	private static final String SQLFileName = "SQLFriendList.db";

	// Class Variables
	private static SQLManager gSQLManager;
	private static PeopleList gPeopleList;
	private static Bitmap gDefaultBitmap;
	private static int gMyUserId;
	private static String gMyNumber;

	// Initialize Method
	public static void initialize(Context pContext, String pTableName) {
		gDefaultBitmap = Tools.convertDrawableToBitmap(pContext.getResources().getDrawable(R.drawable.defaultprofile));

		SQLManager sqlManager = new SQLManager(pContext, SQLFileName, null, 1);
		sqlManager.setTableName(pTableName);
		setSQLManager(sqlManager);
		
		PeopleList peopleList = new PeopleList(pContext);
		peopleList.loadTable(pTableName);
		setPeopleList(peopleList);

	}

	// Getters & Setters (?)
	public static SQLManager getSQLManager() {
		return gSQLManager;
	}

	public static void setSQLManager(SQLManager pSQLManager) {
		gSQLManager = pSQLManager;
	}

	public static PeopleList getPeopleList() {
		return gPeopleList;
	}

	public static void setPeopleList(PeopleList pPeopleList) {
		gPeopleList = pPeopleList;
	}
	
	public static int getMyUserId(){
		return gMyUserId;
	}
	
	public static void setMyUserId(int pMyUserId){
		gMyUserId = pMyUserId;
	}
	
	public static String getMyNumber(){
		return gMyNumber;
	}
	
	public static void setMyNumber(String pMyNumber){
		gMyNumber = pMyNumber;
	}
	
	public static Bitmap getDefaultBitmap(){
		return gDefaultBitmap;
	}
	
	// Class Methods
	public static void deletePerson(int userid) {
		//DB에서 제거
		gSQLManager.delete(userid);
		//PeopleList에서 제거
		gPeopleList.delete(userid);
	}

	public static void insertPerson(ContentValues recordValue) {
		//DB에서 제거
		gSQLManager.insert(recordValue);
		//PeopleList에서 제거
		gPeopleList.insert(recordValue);
	}
	
	public static void updatePerson(int userid, ContentValues recordValue) {
		ContentValues value = new ContentValues(recordValue);
		if(value.containsKey("userid"))	value.remove("userid");
		//DB에서 업데이트
		gSQLManager.update(userid, recordValue);
		//PeopleList에서 업데이트
		gPeopleList.update(userid, recordValue);
	}

	public static Person getMyProfile(){
		return gPeopleList.GetPerson(gMyUserId);
	}

	public static boolean existPersonOnPhone(int userid) {
		return gPeopleList.exist(userid);
	}
	
	public static boolean existPersonOnPhone(String number) {
		return gPeopleList.exist(number);
	}

}
