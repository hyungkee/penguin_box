package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Tab3Fragment extends TabFragment{
	Tab3Fragment(Activity activity) {	super(activity);}

	public void onCreate(){
	}
	
	public void onItemSelected(MenuItem item){

		int id = item.getItemId();
        
        switch(id){
        case R.id.tab3_addfriend:
        	break;
        case R.id.tab3_download:
        	break;
        }
        
	}
	
	public void destroyTabFragment(){
	}
}
