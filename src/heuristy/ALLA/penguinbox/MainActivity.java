// #55962c : 기본 색!

package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.viewpagerindicator.UnderlinePageIndicator;

public class MainActivity extends Activity {

	Activity mActivity = this;

	LinearLayout mTitleLayout; // 직접 아이콘들을 배열함.
	RelativeLayout[] TitleTab;
	TabFragment tab[] = new TabFragment[5];
	ViewPager mViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;
	UnderlinePageIndicator mPageIndicator;
	ImageView[] tabimage;
	int[] mLayoutflag;
	int[] mTitleflag;
	int[] mMenuflag;
	int[] mPIimageflag_g;// gray
	int[] mPIimageflag_c;// colored
	int presentPage = 0;

	SQLManager dbManager;
	
	static int JoinUsActivityCode = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 기본적인 메뉴 속성 정의
		getActionBar().setTitle(getString(R.string.tab1_title));

		mLayoutflag = new int[] { R.layout.tab1_layout, R.layout.tab2_layout, R.layout.tab3_layout, R.layout.tab4_layout, R.layout.tab5_layout };

		mTitleflag = new int[] { R.string.tab1_title, R.string.tab2_title, R.string.tab3_title, R.string.tab4_title, R.string.tab5_title };

		mMenuflag = new int[] { R.menu.tab1_menu, R.menu.tab2_menu, R.menu.tab3_menu, R.menu.tab4_menu, R.menu.tab5_menu };

		mPIimageflag_g = new int[] { R.drawable.tab1_gray, R.drawable.tab2_gray, R.drawable.tab3_gray, R.drawable.tab4_gray, R.drawable.tab5_gray };

		mPIimageflag_c = new int[] { R.drawable.tab1_colored, R.drawable.tab2_colored, R.drawable.tab3_colored, R.drawable.tab4_colored,
				R.drawable.tab5_colored };

		// 메뉴 알림막대 정의
		mTitleLayout = (LinearLayout) findViewById(R.id.TitleLayout);
		TitleTab = new RelativeLayout[5];
		tabimage = new ImageView[5];
		for (int i = 0; i < 5; i++) {
			TitleTab[i] = new RelativeLayout(this);
			tabimage[i] = new ImageView(this);

			if (i == presentPage)
				tabimage[i].setImageResource(mPIimageflag_c[i]);
			if (i != presentPage)
				tabimage[i].setImageResource(mPIimageflag_g[i]);
			RelativeLayout.LayoutParams tabimageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			tabimageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			tabimage[i].setLayoutParams(tabimageParams);
			TitleTab[i].addView(tabimage[i]);

			LinearLayout.LayoutParams TitleTabParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			TitleTabParams.weight = 1;
			TitleTab[i].setLayoutParams(TitleTabParams);
			mTitleLayout.addView(TitleTab[i]);

			TitleTab[i].setClickable(true);
			TitleTab[i].setOnTouchListener(new tabOnTouchListener(i));
		}

		// 메뉴 어뎁터 정의
		mSectionsPagerAdapter = new SectionsPagerAdapter(MainActivity.this, mLayoutflag, mTitleflag, mPIimageflag_g, mPIimageflag_c);

		// 페이지 뷰 정의
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(presentPage);

		// 메뉴 지정자 정의
		mPageIndicator = (UnderlinePageIndicator) findViewById(R.id.pageIndicator);
		mPageIndicator.setFades(false);
		mPageIndicator.setViewPager(mViewPager);
		mPageIndicator.setOnPageChangeListener(new UserPageChangeListener());

		// 데이터베이스 센터 초기화
		DBStorage.initialize(mActivity, "people");

		
		// 첫 사용자인지 확인.
		if(DBStorage.getPeopleList().getCount()==0){
			//내 번호의 데이터가 없다 : 첫 사용자, 가입 권유 
			startActivityForResult(new Intent(this,JoinUsActivity.class), JoinUsActivityCode);
		}else{
			// 나의 ID 갱신
			SQLiteDatabase db = DBStorage.getSQLManager().getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from " + DBStorage.getSQLManager().getTableName(), null);
			int myid = 0;
			String mynumber = "";
			while (cursor.moveToNext()) {
				int userid = cursor.getInt(1);
				String name = cursor.getString(3);
				if(userid == 1){ // 기기 주인의 아이디는 이곳 name에 userid를 저장해두었다.
					myid = Integer.parseInt(name);
					break;
				}
			}
			mynumber = DBStorage.getPeopleList().GetPerson(myid).getNumber();
			DBStorage.setMyUserId(myid);
			DBStorage.setMyNumber(mynumber);
			db.close();

			// 나의 핸드폰 번호 갱신
		}
		 

		// 임시 친구 입력
		/*
		DBManager dbManager = DBManager.getWorldDBManager();
		dbManager.insertbyBitmap(100, null, "친구", "123", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(101, null, "친구1", "124", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(102, null, "친구2", "125", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(103, null, "친구3", "126", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(104, null, "친구4", "123", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(105, null, "친구5", "124", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(106, null, "친구6", "125", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(107, null, "친구7", "126", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(108, null, "친구8", "123", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(109, null, "친구9", "124", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(110, null, "친구10", "125", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(111, null, "친구11", "126", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(112, null, "친구12", "126", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(113, null, "친구13", "123", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(114, null, "친구14", "124", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(115, null, "친구15", "125", 1, 0.0f, 0.0f, 0, 0);
		dbManager.insertbyBitmap(116, null, "친구16", "126", 1, 0.0f, 0.0f, 0, 0);
		*/
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==JoinUsActivityCode){
			if(tab[0]!=null){
				((Tab1Fragment)tab[0]).dataRenew();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		invalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(mMenuflag[presentPage], menu);
		for (int i = 0; i < 5; i++)
			if (tab[i] != null)
				tab[i].onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// 옵션 선택 전달.
		for (int i = 0; i < 5; i++)
			if (tab[i] != null)
				tab[i].onItemSelected(item);

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
/*		for (int i = 0; i < 5; i++)
			if (tab[i] != null)
				if (!tab[i].onKeyDown(keyCode, event))
					return true; // 종료 안됨
					*/
		if(tab[presentPage]!=null)
			if (!tab[presentPage].onKeyDown(keyCode, event))
				return true; // 종료 안됨			

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		for (int i = 0; i < 5; i++)
			if (tab[i] != null)
				tab[i].onResume(mActivity);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < 5; i++)
			if (tab[i] != null)
				tab[i].onPause();
		super.onPause();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends PagerAdapter {
		
		Context context;
		int[] layoutflag;
		int[] titleflag;
		int[] imageflag_g;
		int[] imageflag_c;
		LayoutInflater inflater;

		public SectionsPagerAdapter(Context context, int[] layoutflag, int[] titleflag, int[] imageflag_g, int[] imageflag_c) {
			this.layoutflag = layoutflag;
			this.context = context;
			this.titleflag = titleflag;
			this.imageflag_g = imageflag_g;
			this.imageflag_c = imageflag_c;
		}

		@Override
		public int getCount() {
			return titleflag.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(layoutflag[position], container, false);

			// 탭 java 생성

			if (position == 0)
				tab[0] = new Tab1Fragment(mActivity);
			if (position == 1)
				tab[1] = new Tab2Fragment(mActivity);
			if (position == 2)
				tab[2] = new Tab3Fragment(mActivity);
			if (position == 3)
				tab[3] = new Tab4Fragment(mActivity);
			if (position == 4)
				tab[4] = new Tab5Fragment(mActivity);
			tab[position].onCreate(itemView);

			// 페이지 생성!
			((ViewPager) container).addView(itemView);

			return itemView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// Remove viewpager_item.xml from ViewPager

			// 정지
			if (tab[position] != null)
				tab[position].destroyTabFragment();
			tab[position] = null; // 옵션값 전달 원천 차단(완전히 이전 객체를 버림 -> 가비지 컬렉터행)

			((ViewPager) container).removeView((RelativeLayout) object);

		}
	}

	// 페이지 추적을 위한 OnPageChangeListener
	public class UserPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			presentPage = arg0;
			getActionBar().setTitle(getString(mTitleflag[arg0])); // 액션바 수정
			invalidateOptionsMenu();

			for (int i = 0; i < 5; i++) {
				if (i == arg0)
					tabimage[i].setImageResource(mPIimageflag_c[i]);
				else
					tabimage[i].setImageResource(mPIimageflag_g[i]);
			}

			// 키보드 내리기
			if (tab[0] != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(findViewById(R.id.searchText).getWindowToken(), 0);
			}
		}

	}

	// 각각의 Tab들을 위한 OnTouchListener들
	public class tabOnTouchListener implements View.OnTouchListener {

		int buttonId;
		boolean sw;

		tabOnTouchListener(int buttonId) {
			this.buttonId = buttonId;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				TitleTab[buttonId].setBackgroundColor(Color.parseColor(getString(R.color.NavigationBarSelected)));
				sw = true;
			}

			if (event.getAction() == MotionEvent.ACTION_MOVE && v.isPressed() == false) {
				TitleTab[buttonId].setBackgroundColor(Color.parseColor(getString(R.color.NavigationBarDefault)));
				sw = false;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				TitleTab[buttonId].setBackgroundColor(Color.parseColor(getString(R.color.NavigationBarDefault)));
				if (sw == true)
					mViewPager.setCurrentItem(buttonId);
			}
			return false;
		}

	}

}
