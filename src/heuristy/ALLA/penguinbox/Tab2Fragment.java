package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Tab2Fragment extends TabFragment{
	Tab2Fragment(Activity activity) {	super(activity);}

	public void onCreate(){
	}
	
	public void onItemSelected(MenuItem item){

		int id = item.getItemId();		
        
        switch(id){
        case R.id.tab2_find:
        	break;
        }

	}
	
	public void destroyTabFragment(){
	}
}
