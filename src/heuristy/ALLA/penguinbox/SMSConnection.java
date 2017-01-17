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
					// ���� ����
					// Toast.makeText(mContext, "���� �Ϸ�",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// ���� ����
					// Toast.makeText(mContext, "���� ����",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					// ���� ���� �ƴ�
					// Toast.makeText(mContext, "���� ������ �ƴմϴ�",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					// ���� ����
					// Toast.makeText(mContext, "����(Radio)�� �����ֽ��ϴ�",
					// Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// PDU ����
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
					// ���� �Ϸ�
					// Toast.makeText(mContext, "SMS ���� �Ϸ�",
					// Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					// ���� �ȵ�
					// Toast.makeText(mContext, "SMS ���� ����",
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