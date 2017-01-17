// #55962c : �⺻ ��!

package heuristy.ALLA.penguinbox;

import heuristy.ALLA.penguinbox.phpDown.OnResponseListener;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab5Page1 extends ImageSelectHelperActivity {

	ImageView ProfileImage;
	View ProfileImageOuter;
	EditText ProfileName;
	TextView ProfileNumber;
	SQLManager dbManager;
	String name = null;
	String number = null;
	Bitmap bitmap = null;
	boolean isImageChanged = false;
	int version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab5_page1);

		ProfileName = (EditText) findViewById(R.id.myProfileName);
		ProfileNumber = (TextView) findViewById(R.id.myProfileNumber);

		ProfileImageOuter = (View) findViewById(R.id.imageBackView);
		ProfileImage = (ImageView) findViewById(R.id.myProfileImage);
		ProfileImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setImageSizeBoundary(200);
				setCropOption(1, 1); // optional. default is no crop.
				startSelectImage();
			}
		});

		// �̹��� ������ �Ϸ�� ���� �ݹ� �Լ�
		OnOperationFinishedCB callBack = new OnOperationFinishedCB() {
			@Override
			public void onOperationFinished(Bitmap bm) {
				isImageChanged = true;
				bitmap = bm;
				if (bitmap == null) {
					ProfileImage.setImageDrawable(new RoundedAvatarDrawable(DBStorage.getDefaultBitmap()));
				} else {
					ProfileImage.setImageDrawable(new RoundedAvatarDrawable(bitmap));
				}
				
			}
		};
		setOnOperationFinishedCB(callBack);

		// ������ �ε�
		loadData();

		Button SaveButton = (Button) findViewById(R.id.myProfileSave);
		Button CancelButton = (Button) findViewById(R.id.myProfileCancel);

		SaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name = ProfileName.getText().toString();

				// ���� bitmap ��ȭ�� �־����� ������;
				if (isImageChanged)
					version++;

				// SQL���ε�
				DBStorage.getSQLManager().updateName(DBStorage.getMyUserId(), name);
				DBStorage.getSQLManager().updateUploadVersion(DBStorage.getMyUserId(), version);
				if (isImageChanged)
					DBStorage.getSQLManager().updateBitmap(DBStorage.getMyUserId(), bitmap);

				// PeopleList���ε�
				DBStorage.getPeopleList().updateName(DBStorage.getMyUserId(), name);
				DBStorage.getPeopleList().updateUploadVersion(DBStorage.getMyUserId(), version);
				if (isImageChanged)
					DBStorage.getPeopleList().updateBitmap(DBStorage.getMyUserId(), bitmap);

				// ������ ���ε�
				// TODO : ����
				phpDown task = new phpDown();
				String image = null;
				if(isImageChanged){
					byte[] bytes = Tools.convertBitmapToBlobs(bitmap);
					image = Tools.convertBlobsToBase64String(bytes);
					try {
						image = URLEncoder.encode(image, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if(bitmap==null)	image=".";
				}
				task.execute("http://phk952.dothome.co.kr/index.php", "query_type=3&name=" + name + "&number=" + number + "&image=" + image + "&u_version="
						+ version);

				// ��Ƽ��Ƽ ����
				finish();
			}
		});

		CancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public void loadData() {
		int myUserId = DBStorage.getMyUserId();
		Person person = DBStorage.getPeopleList().GetPerson(myUserId);
		name = person.getName();
		number = person.getNumber();
		bitmap = person.getBitmap();
		version = person.getUploadVersion();

		ProfileName.setText(name);
		ProfileNumber.setText(number);
		
		if (bitmap == null)
			ProfileImage.setImageDrawable(new RoundedAvatarDrawable(DBStorage.getDefaultBitmap()));
		else
			ProfileImage.setImageDrawable(new RoundedAvatarDrawable(bitmap));
		
		// �̹����� ���δ� ��
		Bitmap roundbitmap = Bitmap.createBitmap(210, 210, Config.ARGB_8888);
		roundbitmap.eraseColor(getResources().getColor(R.color.AppBaselight));
		ProfileImageOuter.setBackground(new RoundedAvatarDrawable(roundbitmap));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

}
