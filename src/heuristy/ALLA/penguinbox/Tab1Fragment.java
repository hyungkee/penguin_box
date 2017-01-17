package heuristy.ALLA.penguinbox;

import heuristy.ALLA.penguinbox.phpDown.OnResponseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Tab1Fragment extends TabFragment {
	Tab1Fragment(Activity activity) {
		super(activity);
	}

	EditText searchText;
	ImageView deleteImage;

	LinearLayout navLayout;
	RelativeLayout[] navTxLayout;
	TextView[] navTxView;
	RelativeLayout crtTxViewCtnr;
	TextView crtTxView;

	ListView friendList;
	static PersonListAdapter adapter = null;
	static boolean isEditMode = false;
	static private View mBtnDelete, mBtnCancel;
	static private Dialog mProfileDialog;
	static private Dialog mDeleteDialog;
	static private ProgressDialog mImportDialog;

	static private String searchString = "";

	Vector<Integer> checkList = new Vector<Integer>();
	int deleteid;

	phpDown task = null;

	public void onCreate() {

		// List구성
		friendList = (ListView) mainView.findViewById(R.id.friendList);

		// 데이터를 갱신한다. 데이터가 강제로 변할때 사용성을 고려해 분리시켰다.
		if (adapter == null)
			dataRenew();
		else
			friendList.setAdapter(adapter);

		friendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PersonItem curItem = (PersonItem) adapter.getItem(position);
				showProfileDialog(curItem.getPerson().getUserId());
			}

		});
		friendList.setTextFilterEnabled(true);

		// 검색 구성
		searchText = (EditText) mainView.findViewById(R.id.searchText);
		deleteImage = (ImageView) mainView.findViewById(R.id.searchTextDeleteImage);

		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 검색을 수행한다.
				searchString = searchText.getText().toString();
				if (searchString == null)
					searchString = "";
				dataRenew();
				adapter.notifyDataSetChanged();
				deleteImage.setVisibility(searchString.equals("") ? LinearLayout.INVISIBLE : LinearLayout.VISIBLE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}
		});
		searchText.setText(searchString);
		deleteImage.setVisibility(searchString.equals("") ? LinearLayout.INVISIBLE : LinearLayout.VISIBLE);
		deleteImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchText.setText("");
			}
		});

		// 검색 네비게이션 바
		final String[] nav_index = { "Ξ", "ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "A", "F", "J", "O", "S", "Z", "#" };
		navLayout = (LinearLayout) mainView.findViewById(R.id.navigationBar);
		crtTxViewCtnr = (RelativeLayout) mainView.findViewById(R.id.navigationTextContainer);
		crtTxView = (TextView) mainView.findViewById(R.id.navigationText);
		navTxLayout = new RelativeLayout[nav_index.length]; // 가운데정렬을 위해 ㅠ
		navTxView = new TextView[nav_index.length];

		for (int i = 0; i < nav_index.length; i++) {
			navTxView[i] = new TextView(mContext);
			navTxView[i].setText(nav_index[i]);
			navTxView[i].setTextSize(15);
			navTxView[i].setTextColor(Color.WHITE);
			navTxLayout[i] = new RelativeLayout(mContext);
			RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			navTxView[i].setLayoutParams(rparams);
			navTxLayout[i].addView(navTxView[i]);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			navTxLayout[i].setLayoutParams(params);
			navLayout.addView(navTxLayout[i]);
		}

		navLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
					friendList.setEnabled(false);
					crtTxViewCtnr.setVisibility(TextView.VISIBLE);

					float y = event.getY();
					float top = v.getY() + 40;
					float bottom = v.getY() + v.getHeight() - 80;
					int state = (int) ((int) nav_index.length * (y - top) / (bottom - top));
					if (0 <= event.getX() && 0 <= state && state < nav_index.length) {
						crtTxView.setText(nav_index[state]);
						// 실제 목록 이동
						if (state == 0) {
							friendList.setSelection(0);
						} else {
							int i;
							for (i = 0; i < adapter.getCount(); i++) {
								Person person = adapter.mItems.get(i).getPerson();
								if (person.getUserId() <= 0)
									continue;
								if (SoundSearcher.matchString(person.getName().substring(0, 1), nav_index[state]))
									break;
							}
							friendList.setSelection(i);
						}
					}

				}
				if (action == MotionEvent.ACTION_UP) {
					friendList.setEnabled(true);
					crtTxViewCtnr.setVisibility(TextView.INVISIBLE);
				}
				return true;
			}
		});

	}

	public void onResume() {
		if (adapter == null)
			dataRenew();
		else
			friendList.setAdapter(adapter);
	}

	public void dataRenew() {
		if (!DBStorage.existPersonOnPhone(DBStorage.getMyUserId()))
			return;

		adapter = new PersonListAdapter(mContext);

		PeopleList peopleList = DBStorage.getPeopleList();

		// Separator1,2 : 내 프로필과 친구의 프로필의 구분선
		PersonItem Separator1 = new PersonItem(new Person(-2, null, null, null, 0, 0, 0, 0, 0, 0));
		PersonItem Separator2 = new PersonItem(new Person(-1, null, null, null, 0, 0, 0, 0, 0, 0));
		Separator1.setSelectable(false);
		Separator2.setSelectable(false);
		if (searchString.equals("")) {// 검색할때는 사라지게.
			adapter.addItem(Separator1);
			adapter.addItem(new PersonItem(DBStorage.getMyProfile()));
		}
		adapter.addItem(Separator2);

		for (int i = 0; i < peopleList.getCount(); i++) {
			Person person = peopleList.getList().elementAt(i);
			if (person.getUserId() != DBStorage.getMyUserId())
				if (SoundSearcher.matchString(person.getName(), searchString))
					adapter.addItem(new PersonItem(peopleList.getList().elementAt(i)));
		}

		friendList.setAdapter(adapter);
	}

	public void onItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.tab1_sync:
			importContents();// 데이터 갱신은 작업이 끝나고 내부에서 자동으로 진행
			break;
		case R.id.tab1_edit:
			isEditMode = !isEditMode; // Toogle
			checkList.removeAllElements();
			adapter.notifyDataSetChanged();
			if (isEditMode) {
				navLayout.setVisibility(LinearLayout.INVISIBLE);
				mMenu.findItem(R.id.tab1_delete).setVisible(true);
				mMenu.findItem(R.id.tab1_find).setVisible(false);
			} else {
				navLayout.setVisibility(LinearLayout.VISIBLE);
				mMenu.findItem(R.id.tab1_delete).setVisible(false);
				mMenu.findItem(R.id.tab1_find).setVisible(true);
			}
			// 전체 삭제 버튼 생성
			break;
		case R.id.tab1_find:

			break;
		case R.id.tab1_delete:
			if (checkList.size() == 0)
				Toast.makeText(mContext, "체크된 View가 없습니다.", 1000).show();
			else
				showDeleteDialog(1); // 여러명 제거
			break;
		}
	}

	public void destroyTabFragment() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEditMode) {
				isEditMode = false;
				adapter.notifyDataSetChanged();
				navLayout.setVisibility(LinearLayout.VISIBLE);
				mMenu.findItem(R.id.tab1_delete).setVisible(false);
				mMenu.findItem(R.id.tab1_find).setVisible(true);
				return false;
			}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}

		return true;
	}

	String name = null;
	String number = null;

	private void importContents() {

		Runnable runnable = new Runnable() {
			private Cursor mCursor;

			@Override
			public void run() {

				// 연락처 가져오기
				mCursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);

				// 연락처를 서버에 요청할 형식으로 모은다.
				StringBuffer sbNum = new StringBuffer();
				StringBuffer sbVer = new StringBuffer();
				if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
					do{
						String name = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME));
						String number = mCursor.getString(mCursor.getColumnIndex(Phone.NUMBER));
						number = Tools.convertToPhoneNumber(number);
						if(!sbNum.toString().contains(number) && number.length()==11 && !number.equals(DBStorage.getMyNumber())){
							if(sbNum.length()!=0){
								sbNum.append(";");
								sbVer.append(";");
							}
							sbNum.append(number);
							
							String version = "-1";
							if (DBStorage.existPersonOnPhone(number)) {
								Person person = DBStorage.getPeopleList().GetPerson(number);
								version = String.valueOf(person.getUploadVersion());
							}
							sbVer.append(version);
						}
					}while (mCursor.moveToNext());
				}
				String numbers = sbNum.toString();
				String versions = sbVer.toString();

				// 서버에서 번호가 있는가 요청한다.
				task = new phpDown();
				task.setOnResponseListener(new OnResponseListener() {
					@Override
					public void onResponse(String str) {
						try {
							str.replace("\n", "");
							JSONObject root = new JSONObject(str);
							JSONArray ja = root.getJSONArray("results");
							for(int i=0;ja!=null && i<ja.length();i++){
								JSONObject jo = ja.getJSONObject(i);
								// 서버에 존재하는 번호이다.
								int userid = jo.getInt("userid");
								String image = jo.getString("image");
								byte[] bytes = Tools.convertBase64StringToBlobs(image);
								Bitmap bitmap = Tools.convertBlobsToBitmap(bytes);
								// TODO:thumbnail은 나중에 구현
								String name = jo.getString("name");
								String number = jo.getString("number");
								int isBoxOpen = jo.getInt("isBoxOpen");
								float latitude = Float.parseFloat(jo.getString("latitude"));
								float longitude = Float.parseFloat(jo.getString("longitude"));
								int comstate = jo.getInt("comstate");
								int u_version = jo.getInt("u_version");

								if(DBStorage.getPeopleList().exist(userid)){
									//폰에 존재하는 친구, Update
									int isNew = 1;
									ContentValues recordValue = Tools.convertToContentValues(userid, bitmap, name, number, isBoxOpen, latitude,
											longitude, comstate, u_version, isNew);
									DBStorage.updatePerson(userid, recordValue);
								}else{
									//새로운 친구, Insert
									int isNew = 2;
									ContentValues recordValue = Tools.convertToContentValues(userid, bitmap, name, number, isBoxOpen, latitude,
											longitude, comstate, u_version, isNew);
									DBStorage.insertPerson(recordValue);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 로딩 화면 종료
						showImportDialog(false);

					}
				});
				// 서버에 요청을 하고, 결과는 위의 listener로 전달된다.
				task.execute("http://phk952.dothome.co.kr/index.php", "query_type=2&number="+numbers+"&version="+versions);

				mCursor.close();
			}
		};

		// 로딩 화면
		showImportDialog(true);
		Thread thread = new Thread(runnable);
		thread.start();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 사용자 정의 Dialog들 (Profile표시용, 친구 삭제용)
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	private void showImportDialog(boolean show) {
		if (show) {
			// 로딩 화면을 보여준다
			mImportDialog = new ProgressDialog(mActivity);
			mImportDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dataRenew();
				}
			});
			mImportDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mImportDialog.setMessage("연락처를 동기화중입니다.");
			mImportDialog.setCanceledOnTouchOutside(false);
			mImportDialog.setCancelable(false);
			mImportDialog.show();

		} else {
			// 로딩 화면을 제거한다.
			mImportDialog.cancel();
		}
	}

	private void showProfileDialog(int userid) {

		mProfileDialog = new Dialog(mActivity);
		mProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mProfileDialog.setContentView(R.layout.tab1_iteminfo);

		mProfileDialog.getWindow().setWindowAnimations(0);
		mProfileDialog.getWindow().setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams params = mProfileDialog.getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		mProfileDialog.getWindow().setAttributes(params);

		TextView name = (TextView) mProfileDialog.findViewById(R.id.tab1InfoName);
		TextView number = (TextView) mProfileDialog.findViewById(R.id.tab1InfoNumber);
		ImageView image = (ImageView) mProfileDialog.findViewById(R.id.tab1InfoImage);
		View imageOuter = (View) mProfileDialog.findViewById(R.id.tab1InfoImageOuter);
		Button button = (Button) mProfileDialog.findViewById(R.id.tab1InfoButton);

		for (int i = 0; i < adapter.getCount(); i++) {
			Person person = adapter.mItems.get(i).getPerson();
			if (person.getUserId() == userid) {
				name.setText(person.getName());
				String pn = person.getNumber();

				// 010-xxxx-xxxx
				number.setText(pn.substring(0, 3) + "-" + pn.substring(3, 7) + "-" + pn.substring(7));
				
				// 이미지가 없으면 기본이미지
				Bitmap bitmap = null;
				if (person.getBitmap() != null)
					bitmap = person.getBitmap();
				else
					bitmap = DBStorage.getDefaultBitmap();
				image.setImageDrawable(new RoundedAvatarDrawable(bitmap));
				
				// 이미지를 감싸는 뷰
				Bitmap roundbitmap = Bitmap.createBitmap(imageOuter.getLayoutParams().width, imageOuter.getLayoutParams().height, Config.ARGB_8888);
				roundbitmap.eraseColor(mActivity.getResources().getColor(R.color.AppBaselight));
				imageOuter.setBackground(new RoundedAvatarDrawable(roundbitmap));
				
				// 박스 바로가기 버튼 클릭
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "구현 필요", 1000).show();
					}
				});
				break;
			}
		}

		mProfileDialog.show();

	}

	private void showDeleteDialog(final int kind) { // kind=0 : 1명 삭제, kind=1 :
													// 여러명 삭제
		if (mBtnDelete == null) {
			Button btn = new Button(mActivity);
			btn.setText("삭제");
			LinearLayout.LayoutParams btnParams;
			btnParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			btn.setLayoutParams(btnParams);
			mBtnDelete = btn;
		}
		if (mBtnCancel == null) {
			Button btn = new Button(mActivity);
			btn.setText("취소");
			LinearLayout.LayoutParams btnParams;
			btnParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			btn.setLayoutParams(btnParams);
			mBtnCancel = btn;
		}
		mBtnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (kind == 0)
					DeleteOnePersonView();
				if (kind == 1)
					DeletePersonViewbyCheck();
				mDeleteDialog.dismiss();
				mDeleteDialog = null;
				mBtnDelete = null;
				mBtnCancel = null;
			}
		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDeleteDialog.dismiss();
				mDeleteDialog = null;
				mBtnDelete = null;
				mBtnCancel = null;
			}
		});
		if (mDeleteDialog == null) {
			mDeleteDialog = new Dialog(mActivity);
			mDeleteDialog.setTitle("친구 삭제");
			TextView text = new TextView(mContext);
			text.setText("삭제하시겠습니까?");
			text.setTextColor(Color.BLACK);
			text.setTextSize(20);
			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			textParams.setMargins(20, 20, 20, 20);
			text.setLayoutParams(textParams);
			LinearLayout hLinear = new LinearLayout(mActivity);
			hLinear.setOrientation(LinearLayout.HORIZONTAL);
			hLinear.addView(mBtnDelete);
			hLinear.addView(mBtnCancel);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 10, 10, 10);
			hLinear.setLayoutParams(params);
			LinearLayout linear = new LinearLayout(mActivity);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.addView(text);
			linear.addView(hLinear);
			mDeleteDialog.setContentView(linear, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		mDeleteDialog.show();
	}

	private void DeleteOnePersonView() {
		if (deleteid == 0) {
			Toast.makeText(mContext, "삭제할 View가 없습니다.", 1000).show();
			return;
		}

		DBStorage.deletePerson(deleteid);

		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.mItems.get(i).getPerson().getUserId() == deleteid) {
				adapter.mItems.remove(i);
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private void DeletePersonViewbyCheck() {

		for (int j = 0; j < checkList.size(); j++) {
			int deleteid = checkList.elementAt(j);

			DBStorage.deletePerson(deleteid);

			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.mItems.get(i).getPerson().getUserId() == deleteid) {
					adapter.mItems.remove(i);
					adapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 사용자 정의 ListView를 위한 컴퍼넌트들
	// ///////////////////////////////////////////////////////////////////////////////////////////////

	public class PersonItem {

		private Person mPerson = null;
		private boolean mSelectable = true;

		public PersonItem(Person person) {
			mPerson = person;
		}

		public boolean isSelectable() {
			return mSelectable;
		}

		public void setSelectable(boolean selectable) {
			mSelectable = selectable;
		}

		public Person getPerson() {
			return mPerson;
		}

		public void setPerson(Person person) {
			mPerson = person;
		}

		public int compareTo(PersonItem other) {
			if (mPerson != null) {
				Person otherPerson = other.getPerson();
				if (mPerson.getUserId() != otherPerson.getUserId())
					return -1;
			} else {
				throw new IllegalArgumentException();
			}

			return 0;
		}

	}

	public class PersonListAdapter extends BaseAdapter {

		private Context mContext;
		private Filter dataFilter;

		public List<PersonItem> mItems = new ArrayList<PersonItem>();

		public PersonListAdapter(Context context) {
			mContext = context;
		}

		public void addItem(PersonItem it) {
			if (it == null)
				return;
			mItems.add(it);
		}

		public void setListItems(List<PersonItem> lit) {
			mItems = lit;
		}

		public int getCount() {
			return mItems.size();
		}

		public Object getItem(int position) {
			return mItems.get(position);
		}

		public boolean areAllItemsSelectable() {
			return false;
		}

		public boolean isSelectable(int position) {
			try {
				return mItems.get(position).isSelectable();
			} catch (IndexOutOfBoundsException ex) {
				return false;
			}
		}

		@Override
		public boolean isEnabled(int position) {
			return isSelectable(position);
		};

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			PersonView itemView = null;
			int userid = mItems.get(position).getPerson().getUserId();
			String name = mItems.get(position).getPerson().getName();

			if (convertView == null) { // 객체 새로 생성
				itemView = new PersonView(mContext, mItems.get(position));
			} else {
				itemView = (PersonView) convertView;
				if (mItems.get(position).mPerson.getUserId() * itemView.userid < 0) {
					// 재활용 객체와 현재 객체가 다른 종류(View와 Separator) : 객체를 새로 만들어 주어야
					// 한다.
					itemView = new PersonView(mContext, mItems.get(position));
				}
				// userid까지 받아오지만 이는 의도치 않음. 재갱신을 한다.
				itemView.userid = mItems.get(position).getPerson().getUserId();
			}

			itemView.InsertData(mItems.get(position));
			return itemView;
		}

	}

	public class PersonView extends LinearLayout {

		private ImageView mIcon;
		private View mIconOuter;
		private TextView mNameText;
		private View isNewLayout;
		private Button deleteButton;
		private LinearLayout totalLayout;
		private CheckBox mCheckBox;
		private RelativeLayout mCheckBoxLayout;
		private TextView mSeparatorText;

		int i;
		public int userid = -1;

		public PersonView(Context context, PersonItem aItem) {
			super(context);

			if (aItem == null)
				return;

			userid = aItem.getPerson().getUserId();

			if (aItem.getPerson().getUserId() >= 0) {

				// Layout Inflation
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				inflater.inflate(R.layout.tab1_listitem, this, true);

				mIcon = (ImageView) findViewById(R.id.tab1ListImage);
				mIconOuter = (View) findViewById(R.id.tab1ListImageOuter);
				mNameText = (TextView) findViewById(R.id.tab1ListText);
				isNewLayout = (View) findViewById(R.id.tab1ListAlarmBar);
				totalLayout = (LinearLayout) findViewById(R.id.tab1ListItemLayout);
				mCheckBox = (CheckBox) findViewById(R.id.tab1ListCheckBox);
				mCheckBoxLayout = (RelativeLayout) findViewById(R.id.tab1ListCheckBoxLayout);
				deleteButton = (Button) findViewById(R.id.tab1DeleteButton);

				deleteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteid = userid;
						showDeleteDialog(0); // 1명 제거
					}
				});

				mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						for (i = 0; i < checkList.size(); i++)
							if (checkList.get(i).intValue() == userid) {
								if (!isChecked) {
									checkList.remove(i);
								}
								break;
							}
						if (i == checkList.size()) {
							if (isChecked)
								checkList.add(Integer.valueOf(userid));
						}
					}
				});

				totalLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mCheckBox.setChecked(!mCheckBox.isChecked());
					}
				});
				totalLayout.setClickable(false);

			} else {

				// Layout Inflation
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				inflater.inflate(R.layout.tab1_listitem_seperator, this, true);

				mSeparatorText = (TextView) findViewById(R.id.tab1SeperatorText);

			}

			InsertData(aItem);
		}

		public void InsertData(PersonItem aItem) {
			if (aItem.getPerson().getUserId() >= 0) {
				// Set Icon (동그란 이미지)
				Bitmap bitmap = null;
				if (aItem.getPerson().getBitmap() != null)
					bitmap = aItem.getPerson().getBitmap();
				else
					bitmap = DBStorage.getDefaultBitmap();
				mIcon.setImageDrawable(new RoundedAvatarDrawable(bitmap));
				
				// 이미지를 감싸는 뷰
				Bitmap roundbitmap = Bitmap.createBitmap(mIconOuter.getLayoutParams().width, mIconOuter.getLayoutParams().height, Config.ARGB_8888);
				roundbitmap.eraseColor(getResources().getColor(R.color.AppBaselight));
				mIconOuter.setBackground(new RoundedAvatarDrawable(roundbitmap));
				

				// Set name Text
				mNameText.setText(aItem.getPerson().getName());

				// Set isNew Factor
				if (userid == DBStorage.getMyUserId()) {
					isNewLayout.setVisibility(GONE);
					deleteButton.setVisibility(GONE);
					mCheckBoxLayout.setVisibility(GONE);
				} else {
					for (i = 0; i < checkList.size(); i++)
						if (checkList.get(i).intValue() == userid)
							break;
					if (i == checkList.size())
						mCheckBox.setChecked(false);
					if (i != checkList.size())
						mCheckBox.setChecked(true);

					// 업데이트 상태의 유무
					if (aItem.getPerson().getIsNew() != 1)
						isNewLayout.setVisibility(GONE);
					if (aItem.getPerson().getIsNew() == 1)
						isNewLayout.setVisibility(VISIBLE);

					// 새로운 친구임을 잠시동안 알림.
					if (aItem.getPerson().getIsNew() == 2) 
						totalLayout.setBackground(getResources().getDrawable(R.color.YellowHighlight));
					else
						totalLayout.setBackground(null);

					DBStorage.getSQLManager().updateIsNew(userid, 0);

					if (isEditMode)
						deleteButton.setVisibility(VISIBLE);
					else
						deleteButton.setVisibility(GONE);

					if (isEditMode)
						mCheckBoxLayout.setVisibility(VISIBLE);
					else
						mCheckBoxLayout.setVisibility(GONE);

					if (isEditMode) {
						totalLayout.setClickable(true);
					} else {
						totalLayout.setClickable(false);
					}
				}

			} else {
				// Set Separator Text
				if (aItem.getPerson().getUserId() == -2)
					mSeparatorText.setText("내 프로필");
				if (aItem.getPerson().getUserId() == -1)
					mSeparatorText.setText("내 친구들");
			}
		}

	}

}
