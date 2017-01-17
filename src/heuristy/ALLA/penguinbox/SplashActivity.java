package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
 
public class SplashActivity extends Activity{
    
	Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        intent = new Intent(this, MainActivity.class);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // Mainȭ�� �̵� -> �� ����� ���� �ε��� �ݿ����� ����
            	overridePendingTransition(0,0);
                startActivity(intent);                
                finish();
            }
        };
        
        handler.sendEmptyMessageDelayed(0, 1000);
    } //end onCreate Method
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//Splash���� ���Ű�� ���� ����.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}