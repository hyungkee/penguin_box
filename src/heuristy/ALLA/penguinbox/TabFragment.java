package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public abstract class TabFragment {

	Activity mActivity;
	Context mContext;
	Menu mMenu;
	View mainView; // inflate되어있는 객체
	TabFragment(Activity activity){
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
	}
	
	public void onCreate(){
	}

	public void onCreate(View itemView){
		mainView = itemView;
		onCreate();
	}
	
	protected void onResume(Activity activity) {
		//갱신
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
	}

	protected void onPause() {
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
	
	public void onPrepareOptionsMenu(Menu menu) {
		mMenu = menu;
	}


	
	public abstract void onItemSelected(MenuItem item);		
	public abstract void destroyTabFragment();

}
