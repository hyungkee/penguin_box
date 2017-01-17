package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Tab4Fragment extends TabFragment{
	Tab4Fragment(Activity activity) {	super(activity);}

	public void onCreate(){
	}
	
	public void onItemSelected(MenuItem item){

		int id = item.getItemId();

        switch(id){
        case R.id.tab4_add:
        	break;
        case R.id.tab4_share:
        	break;
        }

	}
	
	public void destroyTabFragment(){
	}
}
