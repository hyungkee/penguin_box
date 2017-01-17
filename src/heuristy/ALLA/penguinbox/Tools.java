package heuristy.ALLA.penguinbox;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

public class Tools {
		
	public static String convertBlobsToBase64String(byte[] bytes){
		if(bytes==null)	return "";
		byte[] encoded = Base64.encode(bytes,Base64.DEFAULT);
		String string = new String(encoded);
		//이미지 구조의 엔터가 결과에 영향을 준다.(Json으로 인식하는데 걸림돌이 된다.) 그래서 엔터를 제거하는데, 이는 나중에 되돌리는데 아무런 영향을 주지 않는다.
		string = string.replace("\n", "");
		return string;
	}
	
	public static byte[] convertBase64StringToBlobs(String string){
		if(string==null || string=="")	return null;
		if (string.equals("."))			return null; // 이미지 없음을 알리기 위한 문자가 .이다.
		byte[] decoded = Base64.decode(string, Base64.DEFAULT);
		return decoded;
	}
	
	public static byte[] convertBitmapToBlobs(Bitmap bitmap){
		byte[] imageInByte = null;
		// 출력 스트림 생성, 압축, 바이트 어래이로 변환
		if(bitmap!=null){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageInByte = stream.toByteArray();
		}
		return imageInByte;
	}

	public static Bitmap convertBlobsToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
		// 넘어온 Byte 배열에서 비트맵을 만들어낸다.
		if (byteArrayToBeCOnvertedIntoBitMap == null)
			return null;
		Bitmap bitMapImage = BitmapFactory.decodeByteArray(byteArrayToBeCOnvertedIntoBitMap, 0, byteArrayToBeCOnvertedIntoBitMap.length);

		return bitMapImage;
	}

	public static String convertToPhoneNumber(String str) {
		if (str == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				sb.append(str.charAt(i));
			}
		}

		String newStr = sb.toString();
		if (newStr.contains("+82")) {
			newStr.replace("+82", "0");
		}
		return newStr;
	}

	public static ContentValues convertToContentValues(int userid, Bitmap bitmap, String name, String number, int isBoxOpen, float latitude, float longitude,
			int comstate, int u_version, int isNew) {

		byte[] imageInByte = null;
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageInByte = stream.toByteArray();
		}
		// ContextValue로 쿼리를 저장
		ContentValues recordValue = new ContentValues();

		recordValue.put("userid", userid);
		recordValue.put("image", imageInByte);
		recordValue.put("name", name);
		recordValue.put("number", number);
		recordValue.put("isBoxOpen", isBoxOpen);
		recordValue.put("latitude", Float.toString(latitude));
		recordValue.put("longitude", Float.toString(longitude));
		recordValue.put("comstate", comstate);
		recordValue.put("u_version", u_version);
		recordValue.put("isNew", isNew);

		return recordValue;
	}

	public static ContentValues convertToContentValues(int userid, ImageView imageView, String name, String number, int isBoxOpen, float latitude,
			float longitude, int comstate, int u_version, int isNew) {
		Bitmap bitmap = null;

		if (imageView != null) {
			Drawable d = imageView.getDrawable();
			bitmap = convertDrawableToBitmap(d);
		}

		return convertToContentValues(userid, bitmap, name, number, isBoxOpen, latitude, longitude, comstate, u_version, isNew);
	}
	
	public static Bitmap convertDrawableToBitmap(Drawable drawable){
		int h = drawable.getIntrinsicHeight();
		int w = drawable.getIntrinsicWidth();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		
		return bitmap;
	}
	

}
