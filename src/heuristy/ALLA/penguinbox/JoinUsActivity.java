package heuristy.ALLA.penguinbox;

import heuristy.ALLA.penguinbox.SMSRecvBroadCastReceiver.OnReceiveListener;
import heuristy.ALLA.penguinbox.phpDown.OnResponseListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JoinUsActivity extends ImageSelectHelperActivity {
	
	Context mContext;
	Handler endHandler;
	boolean endFlag = false;
	ImageView profileImage;
	Button nextButton;
	EditText numberText;
	EditText nameText;
	Bitmap bitmap = null;
	boolean confirm = false;
	String confirmString = null;
	
	Dialog mConfirmDialog = null;
	Button mBtnConfirm = null;
	Button mBtnCancel = null;
	EditText mEdtConNum = null;
	SMSRecvBroadCastReceiver mSMSRecvBroadCastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joinus_activity);
		mContext = this;

		// �̹��� ����
		profileImage = (ImageView) findViewById(R.id.joinProfileImage);
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Reference :
				// https://github.com/DrKein/ImageSelectHelperActivity.git
				// Edited : Park Hyung Kee
				 setImageSizeBoundary(200); // optional. default is 500.
				setCropOption(1, 1); // optional. default is no crop.
				// setCustomButtons(btnGallery, btnCamera, btnCancel); // you
				// can set these buttons.
				startSelectImage();
			}
		});

		// �̹��� ������ �Ϸ�� ���� �ݹ� �Լ�
		OnOperationFinishedCB callBack = new OnOperationFinishedCB() {
			@Override
			public void onOperationFinished(Bitmap bm) {
				bitmap = bm;
				if (bm != null)
					profileImage.setImageBitmap(bm);
				else {
					profileImage.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
				}
			}
		};
		setOnOperationFinishedCB(callBack);

		// ��ȣ�� �̸�
		numberText = (EditText) findViewById(R.id.joinNumber);
		nameText = (EditText) findViewById(R.id.joinName);
		
		// ���� �Է�
		numberText.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
		numberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		// ���� ��ư(���� ��ư)
		nextButton = (Button) findViewById(R.id.join_NextButton);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
								
				final String name = nameText.getText().toString();
				final String number = numberText.getText().toString();
				final String convNumber = Tools.convertToPhoneNumber(number);

				// ������ ����
				if (number.contentEquals("")) {
					Toast.makeText(getApplicationContext(), "��ȭ��ȣ�� �Է��� �ּ���.", 1000).show();
				} else if(!Pattern.matches("^(01[016789])-(\\d{3,4})-(\\d{4})$", number)){
					Toast.makeText(getApplicationContext(), "�ùٸ� ��ȭ��ȣ�� �Է��� �ּ���.", 1000).show();
				} else if (name.contentEquals("")) {
					Toast.makeText(getApplicationContext(), "�̸��� �Է��� �ּ���.", 1000).show();
				} else {
//					�׽�Ʈ�� ���� �����н� ���
//					confirm = true;
					
					// ������ �ȵ� ���� '���� ��ȣ �߼�'��ư���� �۵�
					if(!confirm){
						//������ȣ ����
						confirmString = genConStr();
						//���� ���� ����
						SMSConnection.sendSMS(convNumber, null, "[PenguinBox]������ȣ�� "+confirmString+"�Դϴ�.", mContext);
						Toast.makeText(mContext, "���� �޼����� �����Ͽ����ϴ�.", 1000).show();
						//����ȭ��
						showConfirmDialog(true);
						return;
					}
					// ������ �Ϸ�� ���� '����'��ư���� �۵�


					// ������ ��ȣ�� ����Ѵ�.
					phpDown task = new phpDown();
					phpDown.OnResponseListener listener = new OnResponseListener() {
						@Override
						public void onResponse(String str) {
							try {
								JSONObject root = new JSONObject(str);
								JSONArray ja = root.getJSONArray("results");
								JSONObject jo = ja.getJSONObject(0);
								int userid = jo.getInt("userid");
								
								//�� ID�� ���� ���� ID���� Ȯ���Ѵ�.
								ContentValues eigenValue = Tools.convertToContentValues(1, (Bitmap)null, String.valueOf(userid), "", 0, 0.0f, 0.0f, 0, 0, 0);
								DBStorage.getSQLManager().insert(eigenValue);

								//��⿡ ���� ID�� ����صд�.
								DBStorage.setMyNumber(convNumber);
								DBStorage.setMyUserId(userid);
								ContentValues recordValue = Tools.convertToContentValues(userid, bitmap, name, convNumber, 1, 0.0f, 0.0f, 0, 0, 0);
								DBStorage.insertPerson(recordValue);
								// comstate : wifi(0), LTE(1), �ٰŸ� ���(2) : ���� ����

								// ������������ �̵�
								setContentView(R.layout.joinus_done_activity);
								Button startButton = (Button) findViewById(R.id.startPenBox);
								startButton.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										finish();
									}
								});

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					};
					task.setOnResponseListener(listener);
					// ������ �����͸� ������, ����� ���� listener�� ���޵ȴ�.
					
					String image = null;
					{
						byte[] bytes = Tools.convertBitmapToBlobs(bitmap);
						image = Tools.convertBlobsToBase64String(bytes);
						try {
							image = URLEncoder.encode(image, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if(bitmap==null)	image=".";
					}
					
					//POST�� ��û
					task.execute("http://phk952.dothome.co.kr/index.php","query_type=0&name=" + name + "&number=" + convNumber + "&image=" + image
							+ "&isBoxOpen=1&latitude=0&longitude=0&comstate=0&u_version=0");
					
				}

			}
		});

		// 2�� Ŭ�� ���� �ڵ鷯
		endHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					endFlag = false;
				}
			}
		};

	}
	
	//4�ڸ����� �������� ���ڿ��� ��ȯ�Ѵ�.
	private String genConStr(){
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		//4�ڸ��� ������ ��
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		return sb.toString();
	}
	
	private void showConfirmDialog(boolean show) {
		
		if (mBtnConfirm == null) {
			Button btn = new Button(this);
			btn.setText("����");
			LinearLayout.LayoutParams btnParams;
			btnParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			btn.setLayoutParams(btnParams);
			mBtnConfirm = btn;
		}
		mBtnConfirm.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mEdtConNum.getText().toString().equals(confirmString)){
					//������ ����
					confirm = true;
					nextButton.setText("����");
					nameText.setFocusable(false);
					nameText.setClickable(false);
					nameText.setBackgroundColor(Color.LTGRAY);
					numberText.setFocusable(false);
					numberText.setClickable(false);
					numberText.setBackgroundColor(Color.LTGRAY);
					profileImage.setClickable(false);
					profileImage.setFocusable(false);
					mConfirmDialog.cancel();
					Toast.makeText(mContext, "������ �����Ͽ����ϴ�.", 1000).show();

				}else{
					Toast.makeText(getApplicationContext(), "������ȣ�� �߸��Ǿ����ϴ�.", 1000).show();
					mEdtConNum.setText("");
				}

			}
		});
		if (mBtnCancel == null) {
			Button btn = new Button(this);
			btn.setText("���");
			LinearLayout.LayoutParams btnParams;
			btnParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			btn.setLayoutParams(btnParams);
			mBtnCancel = btn;
		}
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mConfirmDialog.cancel();
			}
		});
		if (mEdtConNum == null) {
			EditText edit = new EditText(this);
			LinearLayout.LayoutParams editParams;
			editParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			edit.setLayoutParams(editParams);
			edit.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
//			edit.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
			mEdtConNum = edit;
		}
		mEdtConNum.setText("");
		
		if (mConfirmDialog==null) {
			// �ε� ȭ���� �����ش�
			mConfirmDialog = new Dialog(this);
			mConfirmDialog.setTitle("����� ����");
			mConfirmDialog.setCanceledOnTouchOutside(false);
			mConfirmDialog.setCancelable(false);
			mConfirmDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					//���ڼ����� ���� receiver�� �����Ѵ�.
					unregisterReceiver(mSMSRecvBroadCastReceiver);			
				}
			});

			TextView text = new TextView(this);
			text.setText("������ȣ�� �Է��� �ּ���.");
			text.setTextColor(Color.BLACK);
			text.setTextSize(20);
			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			textParams.setMargins(20, 20, 20, 20);
			text.setLayoutParams(textParams);
			LinearLayout hLinear = new LinearLayout(this);
			hLinear.setOrientation(LinearLayout.HORIZONTAL);
			hLinear.addView(mBtnConfirm);
			hLinear.addView(mBtnCancel);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 10, 10, 10);
			hLinear.setLayoutParams(params);
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.addView(text);
			linear.addView(mEdtConNum);
			linear.addView(hLinear);
			mConfirmDialog.setContentView(linear, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						
		}
		
		// ȭ���� �����Ѵ�.
		if(show){
			mConfirmDialog.show();

			//[PenguinBox]���ڸ� �ڵ����� ĳġ�ϱ� ���� BroadCastReceiver
			mSMSRecvBroadCastReceiver = new SMSRecvBroadCastReceiver();
			mSMSRecvBroadCastReceiver.setOnReceiveListener(new OnReceiveListener() {
				@Override
				public void onReceiveListener(String msg) {
					if(msg.substring(0, 12).equals("[PenguinBox]")){
						String recNumber;
						StringBuffer sb = new StringBuffer(msg);
						StringBuffer ans = new StringBuffer();
						for(int i=0;i<sb.length();i++)
							if(Character.isDigit(sb.charAt(i)))
								ans.append(sb.charAt(i));
						recNumber = ans.toString();
						mEdtConNum.setText(recNumber);
					}
				}
			});
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(SMSRecvBroadCastReceiver.SMS_RECEIVED);
			registerReceiver(mSMSRecvBroadCastReceiver, intentFilter);
		}else {
			mConfirmDialog.cancel();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!endFlag) {
				Toast.makeText(getApplicationContext(), "\'�ڷ�\'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", 1000).show();
				endFlag = true;
				endHandler.sendEmptyMessageDelayed(0, 1000); // 1�ʾȿ� �� ������ ����
			} else {
				// 2�� ����, ������ �� ����
				moveTaskToBack(true);
				android.os.Process.killProcess(android.os.Process.myPid());
			}

			// ���� �߿� ���Ű�� ���� ����.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
