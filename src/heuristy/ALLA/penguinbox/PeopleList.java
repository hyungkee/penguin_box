package heuristy.ALLA.penguinbox;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class PeopleList {
	private Context mContext;
	private Vector<Person> mPeopleList = new Vector<Person>();

	// Constructor
	public PeopleList(Context context) {
		mContext = context;
	}

	public void loadTable(String table) {
		SQLiteDatabase db = DBStorage.getSQLManager().getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + table, null);
		while (cursor.moveToNext()) {
			int userid = cursor.getInt(1);
			if(userid==1)	continue; // 공개되지 않아야할 주인의 userid가 들어있다.
			// 순서를 고려해 입력
			insert(cursor);
		}
		db.close();
	}
	
	public Person GetPersonInLocation(int location){
		return mPeopleList.get(location);
	}
	
	// Getters
 	public Person GetPerson(int userid) {
		for (int i = 0; i < getCount(); i++) {
			Person person = getList().elementAt(i);
			if (person.getUserId() == userid) {
				return person;
			}
		}
		return null;
	}

 	public Person GetPerson(String number) {
		for (int i = 0; i < getCount(); i++) {
			Person person = getList().elementAt(i);
			if (person.getNumber().equals(number)) {
				return person;
			}
		}
		return null;
	}
 	
	public int getCount() {
		return mPeopleList.size();
	}

	public Vector<Person> getList() {
		return mPeopleList;
	}

	// Setters
	public void insert(Cursor cursor) {

		Person person = new Person();

		int userid = cursor.getInt(1);
		Bitmap bitmap = Tools.convertBlobsToBitmap(cursor.getBlob(2));
		String name = cursor.getString(3);
		String number = cursor.getString(4);
		int isBoxOpen = cursor.getInt(5);
		float latitude = Float.valueOf(cursor.getString(6));
		float longitude = Float.valueOf(cursor.getString(7));
		int comstate = cursor.getInt(8);
		int u_version = cursor.getInt(9);
		int isNew = cursor.getInt(10);


		person.setData(userid, name, number, bitmap, isBoxOpen, latitude, longitude, comstate, u_version, isNew);

		// 적절한 위치에 데이터 추가
		int i;
		for (i = 0; i < getCount(); i++)
			if (compare(person.getName(), mPeopleList.get(i).getName()) > 0)
				break;
		mPeopleList.add(i, person);
	}

	public void insert(ContentValues contentValue) {

		Person person = new Person();
		person.setData(contentValue);

		// 적절한 위치에 데이터 추가
		int i;
		for (i = 0; i < getCount(); i++)
			if (compare(person.getName(), mPeopleList.get(i).getName()) > 0)
				break;
		mPeopleList.add(i, person);

	}


	public int getCategory(String string) {
		// 한글(0), 영어(1), 숫자(2), 나머지(3)
		if (SoundSearcher.isHangul(string))
			return 0;
		else if (string.compareTo("a") >= 0 && string.compareTo("z") <= 0)
			return 1;
		if (string.compareTo("0") >= 0 && string.compareTo("9") <= 0)
			return 2;
		return 3;
	}

	// string1이 string2보다 앞이라면 양수, 같으면0, 뒤라면 음수
	public int compare(String string1, String string2) {
		int len1 = string1.length();
		int len2 = string2.length();
		int position = 0;

		// 순서는 숫자,한글,영어,나머지 (대소문자 구분X)
		while (position < len1 && position < len2) {
			String S1 = string1.substring(position, position + 1).toLowerCase(); // 바로 소문자 변환
			String S2 = string2.substring(position, position + 1).toLowerCase(); // 바로 소문자 변환
			int k1, k2;
			// 같으면 다음문자 검사
			if (S1.equals(S2)) {
				position++;
				continue;
			}

			// 한글(0), 영어(1), 숫자(2), 나머지(3)
			k1 = getCategory(S1);
			k2 = getCategory(S2);

			if (k1 != k2) {
				return k1 < k2 ? 1 : -1;
			} else {
				if (k1 == 0) {

					Character.UnicodeBlock block1 = Character.UnicodeBlock.of(S1.charAt(0));
					Character.UnicodeBlock block2 = Character.UnicodeBlock.of(S2.charAt(0));
					int count = (block1 == Character.UnicodeBlock.HANGUL_SYLLABLES ? 1 : 0) + (block2 == Character.UnicodeBlock.HANGUL_SYLLABLES ? 1 : 0);

					// 자모의 경우는 SoundSearcher의 getInitialSound()를 못쓰기 때문.
					String initS1, initS2;
					initS1 = SoundSearcher.isHangulSYLLABLES(S1.charAt(0)) ? String.valueOf(SoundSearcher.getInitialSound(S1.charAt(0))) : S1;
					initS2 = SoundSearcher.isHangulSYLLABLES(S2.charAt(0)) ? String.valueOf(SoundSearcher.getInitialSound(S2.charAt(0))) : S2;
					// 초성이 같으면서 단 하나만 SYLLABLES인 경우
					if(initS1.equals(initS2) && count==1){
						return (block1==Character.UnicodeBlock.HANGUL_SYLLABLES?-1:1);
					}
					// 초성이 다르면 초성으로 순서비교
					if(!initS1.equals(initS2)){
						return initS2.compareTo(initS1);
					}
				}
				return S2.compareTo(S1);
			}
		}

		// while안에서 return되지 않으면 이는 길이로 판단할 문제이다.
		if (len1 < len2)
			return 1;
		if (len1 > len2)
			return -1;
		return 0;
	}

	public void delete(int userid) {
		int i;
		for (i = 0; i < getCount(); i++) {
			if (mPeopleList.get(i).getUserId() == userid) {
				mPeopleList.remove(i);
				break;
			}
		}
	}
	
	public boolean exist(int userid) {
		for (int i = 0; i < getCount(); i++) {
			Person person = getList().elementAt(i);
			if (person.getUserId() == userid)
				return true;
		}
		return false;
	}
	
	public boolean exist(String number) {
		for (int i = 0; i < getCount(); i++) {
			Person person = getList().elementAt(i);
			String number1 = Tools.convertToPhoneNumber(person.getNumber());
			String number2 = Tools.convertToPhoneNumber(number);
			if (number1.equals(number2))
				return true;
		}
		return false;
	}
	
	public ContentValues GetContentValues(int userid){
		ContentValues value = null;
		return value;
		
	}


	// Updaters(Setters)
	public void update(int userid, ContentValues recordValue) {
		// peopleList 갱신
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setData(recordValue);
				return;
			}
		}

	}

	public void updateName(int userid, String name) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setName(name);
				return;
			}
		}
	}

	public void updateNumber(int userid, String number) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setNumber(number);
				return;
			}
		}
	}

	public void updateIsBoxOpen(int userid, int isBoxOpen) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setIsBoxOpen(isBoxOpen);
				return;
			}
		}
	}

	public void updateLongitude(int userid, float longitude) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setLongitude(longitude);
				return;
			}
		}
	}

	public void updateLatitude(int userid, float latitude) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setLatitude(latitude);
				return;
			}
		}
	}

	public void updateComstate(int userid, int comstate) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setComState(comstate);
				return;
			}
		}
	}

	public void updateUploadVersion(int userid, int u_version) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setUploadVersion(u_version);
				return;
			}
		}
	}

	public void updateIsNew(int userid, int isNew) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setIsNew(isNew);
				return;
			}
		}
	}

	public void updateBitmap(int userid, Bitmap bitmap) {
		for (int i = 0; i < getCount(); i++) {
			Person person = mPeopleList.elementAt(i);
			if (person.getUserId() == userid) {
				person.setBitmap(bitmap);
				return;
			}
		}
	}
}
