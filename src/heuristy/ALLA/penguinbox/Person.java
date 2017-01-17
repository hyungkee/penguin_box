package heuristy.ALLA.penguinbox;

import android.content.ContentValues;
import android.graphics.Bitmap;

//사람 관리를 위한 클래스
public class Person {
	private int userid;
	private String name;
	private String number;
	private Bitmap bitmap;
	private int isBoxOpen;
	private float latitude;
	private float longitude;
	private int comstate;
	private int u_version;
	private int isNew;

	public Person() {
	}

	public Person(int userid, String name, String number, Bitmap bitmap, int isBoxOpen, float latitude, float longitude, int comstate, int u_version, int isNew) {
		this();
		setData(userid, name, number, bitmap, isBoxOpen, latitude, longitude, comstate, u_version, isNew);
	}

	public void setData(int userid, String name, String number, Bitmap bitmap, int isBoxOpen, float latitude, float longitude, int comstate, int u_version,
			int isNew) {
		this.userid = userid;
		this.name = name;
		this.number = number;
		this.bitmap = bitmap;
		this.isBoxOpen = isBoxOpen;
		this.latitude = latitude;
		this.longitude = longitude;
		this.comstate = comstate;
		this.u_version = u_version;
		this.isNew = isNew;
	}
	
	public void setData(ContentValues contentValue){
		if (contentValue.containsKey("userid"))
			setUserId( contentValue.getAsInteger("userid").intValue() );
		if (contentValue.containsKey("name"))
			setName( contentValue.getAsString("name") );
		if (contentValue.containsKey("number"))
			setNumber( contentValue.getAsString("number") );
		if (contentValue.containsKey("isBoxOpen"))
			setIsBoxOpen( contentValue.getAsInteger("isBoxOpen").intValue() );
		if (contentValue.containsKey("latitude"))
			setLatitude( contentValue.getAsFloat("latitude").floatValue() );
		if (contentValue.containsKey("longitude"))
			setLongitude( contentValue.getAsFloat("longitude").floatValue() );
		if (contentValue.containsKey("comstate"))
			setComState( contentValue.getAsInteger("comstate").intValue() );
		if (contentValue.containsKey("u_version"))
			setUploadVersion( contentValue.getAsInteger("u_version").intValue() );
		if (contentValue.containsKey("isNew"))
			setIsNew( contentValue.getAsInteger("isNew").intValue() );
		if (contentValue.containsKey("image"))
			setBitmap( Tools.convertBlobsToBitmap(contentValue.getAsByteArray("image")) );
	}

	public int getUserId() {
		return userid;
	}

	public void setUserId(int pUserId) {
		userid = pUserId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String pNumber) {
		number = pNumber;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap pBitmap) {
		bitmap = pBitmap;
	}

	public int getIsBoxOpen() {
		return isBoxOpen;
	}
	
	public void setIsBoxOpen(int pIsBoxOpen) {
		isBoxOpen = pIsBoxOpen;
	}

	public float getLatitude() {
		return latitude;
	}
	
	public void setLatitude(float pLatitude) {
		latitude = pLatitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float pLongitude) {
		longitude = pLongitude;
	}

	public int getComState() {
		return comstate;
	}
	
	public void setComState(int pComState) {
		comstate = pComState;
	}

	public int getUploadVersion() {
		return u_version;
	}
	
	public void setUploadVersion(int pUploadVersion) {
		u_version = pUploadVersion;
	}

	public int getIsNew() {
		return isNew;
	}
	
	public void setIsNew(int pIsNew) {
		isNew = pIsNew;
	}


}