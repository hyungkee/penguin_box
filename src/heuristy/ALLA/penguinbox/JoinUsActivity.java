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

		// 이미지 수정
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

		// 이미지 선택이 완료될 떄의 콜백 함수
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

		// 번호와 이름
		numberText = (EditText) findViewById(R.id.joinNumber);
		nameText = (EditText) findViewById(R.id.joinName);
		
		// 숫자 입력
		numberText.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
		numberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		// 다음 버튼(인증 버튼)
		nextButton = (Button) findViewById(R.id.join_NextButton);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
								
				final String name = nameText.getText().toString();
				final String number = numberText.getText().toString();
				final String convNumber = Tools.convertToPhoneNumber(number);

				// 데이터 저장
				if (number.contentEquals("")) {
					Toast.makeText(getApplicationContext(), "전화번호를 입력해 주세요.", 1000).show();
				} else if(!Pattern.matches("^(01[016789])-(\\d{3,4})-(\\d{4})$", number)){
					Toast.makeText(getApplicationContext(), "올바른 전화번호를 입력해 주세요.", 1000).show();
				} else if (name.contentEquals("")) {
					Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", 1000).show();
				} else {
//					테스트를 위한 바이패스 기능
//					confirm = true;
					
					// 인증이 안된 경우는 '인증 번호 발송'버튼으로 작동
					if(!confirm){
						//인증번호 갱신
						confirmString = genConStr();
						//인증 문자 전송
						SMSConnection.sendSMS(convNumber, null, "[PenguinBox]인증번호는 "+confirmString+"입니다.", mContext);
						Toast.makeText(mContext, "인증 메세지를 전송하였습니다.", 1000).show();
						//인증화면
						showConfirmDialog(true);
						return;
					}
					// 인증이 완료된 경우는 '다음'버튼으로 작동


					// 서버에 번호를 기록한다.
					phpDown task = new phpDown();
					phpDown.OnResponseListener listener = new OnResponseListener() {
						@Override
						public void onResponse(String str) {
							try {
								JSONObject root = new JSONObject(str);
								JSONArray ja = root.getJSONArray("results");
								JSONObject jo = ja.getJSONObject(0);
								int userid = jo.getInt("userid");
								
								//이 ID가 나의 고유 ID임을 확인한다.
								ContentValues eigenValue = Tools.convertToContentValues(1, (Bitmap)null, String.valueOf(userid), "", 0, 0.0f, 0.0f, 0, 0, 0);
								DBStorage.getSQLManager().insert(eigenValue);

								//기기에 나의 ID를 기록해둔다.
								DBStorage.setMyNumber(convNumber);
								DBStorage.setMyUserId(userid);
								ContentValues recordValue = Tools.convertToContentValues(userid, bitmap, name, convNumber, 1, 0.0f, 0.0f, 0, 0, 0);
								DBStorage.insertPerson(recordValue);
								// comstate : wifi(0), LTE(1), 근거리 통신(2) : 차후 과제

								// 다음페이지로 이동
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
					// 서버에 데이터를 보내고, 결과는 위의 listener로 전달된다.
					
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
					
					//POST로 요청
					task.execute("http://phk952.dothome.co.kr/index.php","query_type=0&name=" + name + "&number=" + convNumber + "&image=" + image
							+ "&isBoxOpen=1&latitude=0&longitude=0&comstate=0&u_version=0");
					
				}

			}
		});

		// 2번 클릭 종료 핸들러
		endHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					endFlag = false;
				}
			}
		};

	}
	
	//4자리수의 랜덤숫자 문자열을 반환한다.
	private String genConStr(){
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		//4자리의 임의의 수
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		sb.append(rand.nextInt(9));
		return sb.toString();
	}
	
	private void showConfirmDialog(boolean show) {
		
		if (mBtnConfirm == null) {
			Button btn = new Button(this);
			btn.setText("인증");
			LinearLayout.LayoutParams btnParams;
			btnParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
			btn.setLayoutParams(btnParams);
			mBtnConfirm = btn;
		}
		mBtnConfirm.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mEdtConNum.getText().toString().equals(confirmString)){
					//성공적 인증
					confirm = true;
					nextButton.setText("다음");
					nameText.setFocusable(false);
					nameText.setClickable(false);
					nameText.setBackgroundColor(Color.LTGRAY);
					numberText.setFocusable(false);
					numberText.setClickable(false);
					numberText.setBackgroundColor(Color.LTGRAY);
					profileImage.setClickable(false);
					profileImage.setFocusable(false);
					mConfirmDialog.cancel();
					Toast.makeText(mContext, "인증에 성공하였습니다.", 1000).show();

				}else{
					Toast.makeText(getApplicationContext(), "인증번호가 잘못되었습니다.", 1000).show();
					mEdtConNum.setText("");
				}

			}
		});
		if (mBtnCancel == null) {
			Button btn = new Button(this);
			btn.setText("취소");
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
			// 로딩 화면을 보여준다
			mConfirmDialog = new Dialog(this);
			mConfirmDialog.setTitle("사용자 인증");
			mConfirmDialog.setCanceledOnTouchOutside(false);
			mConfirmDialog.setCancelable(false);
			mConfirmDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					//문자수신을 위한 receiver를 해제한다.
					unregisterReceiver(mSMSRecvBroadCastReceiver);			
				}
			});

			TextView text = new TextView(this);
			text.setText("인증번호를 입력해 주세요.");
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
		
		// 화면을 제어한다.
		if(show){
			mConfirmDialog.show();

			//[PenguinBox]문자를 자동으로 캐치하기 위한 BroadCastReceiver
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
				Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", 1000).show();
				endFlag = true;
				endHandler.sendEmptyMessageDelayed(0, 1000); // 1초안에 또 누르면 종료
			} else {
				// 2번 누름, 완전히 앱 종료
				moveTaskToBack(true);
				android.os.Process.killProcess(android.os.Process.myPid());
			}

			// 가입 중에 취소키로 종료 방지.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
