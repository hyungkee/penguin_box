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
			if(userid==1)	continue; // �������� �ʾƾ��� ������ userid�� ����ִ�.
			// ������ ����� �Է�
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

		// ������ ��ġ�� ������ �߰�
		int i;
		for (i = 0; i < getCount(); i++)
			if (compare(person.getName(), mPeopleList.get(i).getName()) > 0)
				break;
		mPeopleList.add(i, person);
	}

	public void insert(ContentValues contentValue) {

		Person person = new Person();
		person.setData(contentValue);

		// ������ ��ġ�� ������ �߰�
		int i;
		for (i = 0; i < getCount(); i++)
			if (compare(person.getName(), mPeopleList.get(i).getName()) > 0)
				break;
		mPeopleList.add(i, person);

	}


	public int getCategory(String string) {
		// �ѱ�(0), ����(1), ����(2), ������(3)
		if (SoundSearcher.isHangul(string))
			return 0;
		else if (string.compareTo("a") >= 0 && string.compareTo("z") <= 0)
			return 1;
		if (string.compareTo("0") >= 0 && string.compareTo("9") <= 0)
			return 2;
		return 3;
	}

	// string1�� string2���� ���̶�� ���, ������0, �ڶ�� ����
	public int compare(String string1, String string2) {
		int len1 = string1.length();
		int len2 = string2.length();
		int position = 0;

		// ������ ����,�ѱ�,����,������ (��ҹ��� ����X)
		while (position < len1 && position < len2) {
			String S1 = string1.substring(position, position + 1).toLowerCase(); // �ٷ� �ҹ��� ��ȯ
			String S2 = string2.substring(position, position + 1).toLowerCase(); // �ٷ� �ҹ��� ��ȯ
			int k1, k2;
			// ������ �������� �˻�
			if (S1.equals(S2)) {
				position++;
				continue;
			}

			// �ѱ�(0), ����(1), ����(2), ������(3)
			k1 = getCategory(S1);
			k2 = getCategory(S2);

			if (k1 != k2) {
				return k1 < k2 ? 1 : -1;
			} else {
				if (k1 == 0) {

					Character.UnicodeBlock block1 = Character.UnicodeBlock.of(S1.charAt(0));
					Character.UnicodeBlock block2 = Character.UnicodeBlock.of(S2.charAt(0));
					int count = (block1 == Character.UnicodeBlock.HANGUL_SYLLABLES ? 1 : 0) + (block2 == Character.UnicodeBlock.HANGUL_SYLLABLES ? 1 : 0);

					// �ڸ��� ���� SoundSearcher�� getInitialSound()�� ������ ����.
					String initS1, initS2;
					initS1 = SoundSearcher.isHangulSYLLABLES(S1.charAt(0)) ? String.valueOf(SoundSearcher.getInitialSound(S1.charAt(0))) : S1;
					initS2 = SoundSearcher.isHangulSYLLABLES(S2.charAt(0)) ? String.valueOf(SoundSearcher.getInitialSound(S2.charAt(0))) : S2;
					// �ʼ��� �����鼭 �� �ϳ��� SYLLABLES�� ���
					if(initS1.equals(initS2) && count==1){
						return (block1==Character.UnicodeBlock.HANGUL_SYLLABLES?-1:1);
					}
					// �ʼ��� �ٸ��� �ʼ����� ������
					if(!initS1.equals(initS2)){
						return initS2.compareTo(initS1);
					}
				}
				return S2.compareTo(S1);
			}
		}

		// while�ȿ��� return���� ������ �̴� ���̷� �Ǵ��� �����̴�.
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
		// peopleList ����
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
