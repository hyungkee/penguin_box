package heuristy.ALLA.penguinbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class NetConnection {
}

class phpDown extends AsyncTask<String, Integer, String> {

	private OnResponseListener mResponseListener = null;
	
	interface OnResponseListener{
		void onResponse(String str);
	}
	
	void setOnResponseListener(OnResponseListener onResponseListener){
		mResponseListener = onResponseListener;
	}


	@Override
	protected String doInBackground(String... urls) {
		StringBuilder jsonHtml = new StringBuilder();
		try {
			// ���� url ����
			URL url = new URL(urls[0]);
			// Ŀ�ؼ� ��ü ����
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);

			// ����Ǿ�����.
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				
				// GET����� ���
				if(urls.length==1){
				}
				// POST����� ���
				if(urls.length==2){
					String body = urls[1];
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					OutputStream os = conn.getOutputStream();
					os.write( body.getBytes("UTF-8") );
					os.flush();
					os.close();
				}
				
				// ����Ǿ��� �ڵ尡 ���ϵǸ�.
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					for (;;) {
						// ���� �������� �ؽ�Ʈ�� ���δ����� �о� ����.
						String line = br.readLine();
						if (line == null)
							break;
						// ����� �ؽ�Ʈ ������ jsonHtml�� �ٿ�����
						jsonHtml.append(line + "\n");
					}
					br.close();
				}
				conn.disconnect();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonHtml.toString();

	}
	
	protected void onPostExecute(String str) {
		
		if(mResponseListener!=null)
			mResponseListener.onResponse(str);
	}

}
