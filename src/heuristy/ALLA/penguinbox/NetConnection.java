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
			// 연결 url 설정
			URL url = new URL(urls[0]);
			// 커넥션 객체 생성
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);

			// 연결되었으면.
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				
				// GET방식인 경우
				if(urls.length==1){
				}
				// POST방식인 경우
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
				
				// 연결되었음 코드가 리턴되면.
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					for (;;) {
						// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
						String line = br.readLine();
						if (line == null)
							break;
						// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
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
