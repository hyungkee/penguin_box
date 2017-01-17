package heuristy.ALLA.penguinbox;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSConnection {

	public static void sendSMS(String smsNumber, String scNumber, String smsText, Context pContext) {
		final Context mContext = pContext;
		PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT_ACTION"), 0);
		PendingIntent deliveredIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

		mContext.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// 전송 성공
					// Toast.makeText(mContext, "전송 완료",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// 전송 실패
					// Toast.makeText(mContext, "전송 실패",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					// 서비스 지역 아님
					// Toast.makeText(mContext, "서비스 지역이 아닙니다",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// 무선 꺼짐
					// Toast.makeText(mContext, "무선(Radio)가 꺼져있습니다",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// PDU 실패
					// Toast.makeText(mContext, "PDU Null",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter("SMS_SENT_ACTION"));

		mContext.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					// 도착 완료
					// Toast.makeText(mContext, "SMS 도착 완료",
					// Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					// 도착 안됨
					// Toast.makeText(mContext, "SMS 도착 실패",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter("SMS_DELIVERED_ACTION"));

		SmsManager mSmsManager = SmsManager.getDefault();
		mSmsManager.sendTextMessage(smsNumber, scNumber, smsText, sentIntent, deliveredIntent);
	}

}

class SMSRecvBroadCastReceiver extends BroadcastReceiver {
	static final String TAG = "SmsReceiver";
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	OnReceiveListener listener = null;
	
	interface OnReceiveListener{
		void onReceiveListener(String msg);
	}
	
	public void setOnReceiveListener(OnReceiveListener receiveListener){
		listener = receiveListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED)) {

			Bundle bundle = intent.getExtras();
			if (bundle == null) {
				return;
			}
			Object[] pdusObj = (Object[]) bundle.get("pdus");
			if (pdusObj == null) {
				return;
			}
			String MessageBody = "";

			SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];
			for (int i = 0; i < pdusObj.length; i++) {
				smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				MessageBody += smsMessages[i].getMessageBody();
			}
			
			if(listener!=null)
				listener.onReceiveListener(MessageBody);
		}
	}
}