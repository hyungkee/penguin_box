package heuristy.ALLA.penguinbox;

//Reference : https://github.com/DrKein/ImageSelectHelperActivity.git

//Edit : Park Hyung Kee
//setImageSizeBoundary(400); // optional. default is 500.
//setCustomButtons(btnGallery, btnCamera, btnCancel); // you can set these buttons.
// getSelectedImageFile(); : extract selected & saved image file. FILE���·� return�ȴ�.

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author kein
 * 
 */
public class ImageSelectHelperActivity extends Activity {
	/** Buttons for selector dialog */
	private View mBtnGallery = null, mBtnCamera = null, mBtnCancel = null, mBtnDelete = null;

	private final int REQ_CODE_PICK_GALLERY = 900001;
	private final int REQ_CODE_PICK_CAMERA = 900002;
	private final int REQ_CODE_PICK_CROP = 900003;
	
	
	interface OnOperationFinishedCB{
		void onOperationFinished(Bitmap bm);
	}
	
	OnOperationFinishedCB finishedCB;
	
	public void setOnOperationFinishedCB(OnOperationFinishedCB pFinishedCB){
		finishedCB = pFinishedCB;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("mCropRequested", mCropRequested);
		savedInstanceState.putInt("mCropAspectWidth", mCropAspectWidth);
		savedInstanceState.putInt("mCropAspectHeight", mCropAspectHeight);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mCropRequested = savedInstanceState.getBoolean("mCropRequested");
		mCropAspectWidth = savedInstanceState.getInt("mCropAspectWidth");
		mCropAspectHeight = savedInstanceState.getInt("mCropAspectHeight");
	}

	/**
	 * Call this to start!
	 */
	public void startSelectImage() {
		if (!checkWriteExternalPermission()) {
			showAlert("we need android.permission.WRITE_EXTERNAL_STORAGE");
			return;
		}
		if (!checkSDisAvailable()) {
			showAlert("Check External Storage.");
			return;
		}
		if (mBtnGallery == null) {
			setDefaultButtons();
		}
		setButtonsClick();
		showSelectDialog();
	}

	public File getSelectedImageFile() {
		return getTempImageFile();
	}

	private boolean mCropRequested = false;

	/**
	 * crop �� �ʿ��� ��� ������. �������� ������ crop ���� ����.
	 * 
	 * @param width
	 *            crop size width.
	 * @param height
	 *            crop size height.
	 */
	private int mCropAspectWidth = 1, mCropAspectHeight = 1;

	public void setCropOption(int aspectX, int aspectY) {
		mCropRequested = true;
		mCropAspectWidth = aspectX;
		mCropAspectHeight = aspectY;
	}

	/**
	 * ����� �̹����� �ִ� ũ�� ����. ����, ���� ������ ũ�⺸�� ���� ������� �̹��� ũ�⸦ ������. default size :
	 * 500
	 * 
	 * @param sizePixel
	 *            �⺻ 500
	 */
	private int mImageSizeBoundary = 500;

	public void setImageSizeBoundary(int sizePixel) {
		mImageSizeBoundary = sizePixel;
	}

	private boolean checkWriteExternalPermission() {
		String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
		int res = checkCallingOrSelfPermission(permission);
		return (res == PackageManager.PERMISSION_GRANTED);
	}

	private boolean checkSDisAvailable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Set your own button views for selector dialog.
	 */
	public void setCustomButtons(View btnGallery, View btnCamera, View btnCancel, View btnDelete) {
		if (btnGallery == null || btnCamera == null || btnCancel == null || btnDelete == null) {
			showAlert("All buttons should not null.");
		} else {
			mBtnGallery = btnGallery;
			mBtnCamera = btnCamera;
			mBtnDelete = btnDelete;
			mBtnCancel = btnCancel;
		}
	}

	/**
	 * Set default buttons for selector dialog, unless you set.
	 */
	private void setDefaultButtons() {
		Button btn1 = new Button(this);
		Button btn2 = new Button(this);
		Button btn3 = new Button(this);
		Button btn4 = new Button(this);
		btn1.setText("From Gallery");
		btn2.setText("From Camera");
		btn3.setText("Delete");
		btn4.setText("Cancel");
		mBtnGallery = btn1;
		mBtnCamera = btn2;
		mBtnDelete = btn3;
		mBtnCancel = btn4;
	}

	private File getTempImageFile() {
		File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/temp/");
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(path, "tempimage.png");
		return file;
	}

	private void setButtonsClick() {
		mBtnGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectDialog.dismiss();
				Intent i = new Intent(Intent.ACTION_PICK);
				i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, REQ_CODE_PICK_GALLERY);
			}
		});
		mBtnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectDialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempImageFile()));
				intent.putExtra("return-data", true);
				startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
			}
		});
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectDialog != null && mSelectDialog.isShowing()) {
					mSelectDialog.dismiss();
				}
			}
		});
		mBtnDelete.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
//				ImageView imageview = (ImageView)findViewById(ImageSelected);
//				imageview.setImageDrawable(getResources().getDrawable(R.drawable.defaultprofile));
				if(finishedCB!=null)	finishedCB.onOperationFinished(null);
				mSelectDialog.dismiss();
			}
		});
	}

	private Dialog mSelectDialog;

	private void showSelectDialog() {
		if (mSelectDialog == null) {
			mSelectDialog = new Dialog(this);
			mSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.addView(mBtnGallery);
			linear.addView(mBtnCamera);
			linear.addView(mBtnDelete);
			linear.addView(mBtnCancel);
			int dialogWidth = getResources().getDisplayMetrics().widthPixels / 2;
			if (dialogWidth / 2 > 700) {
				dialogWidth = 700;
			}
			mSelectDialog.setContentView(linear, new LayoutParams(dialogWidth, LayoutParams.WRAP_CONTENT));
		}
		mSelectDialog.show();
	}

	private void showAlert(String msg) {
		new AlertDialog.Builder(this).setTitle("Error").setMessage(msg).setPositiveButton(android.R.string.ok, null).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
			// �������� ��� ��ٷ� data �� uri�� �Ѿ��.
			Uri uri = data.getData();
			copyUriToFile(uri, getTempImageFile());
			if (mCropRequested) {
				cropImage();
			} else {
				doFinalProcess();
			}
		} else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
			// ī�޶��� ��� file �� ������� ���ƿ�.
			// ī�޶� ȸ�� ����.
			correctCameraOrientation(getTempImageFile());
			if (mCropRequested) {
				cropImage();
			} else {
				doFinalProcess();
			}
		} else if (requestCode == REQ_CODE_PICK_CROP && resultCode == Activity.RESULT_OK) {
			// crop �� ����� file�� ���ƿ�.
			doFinalProcess();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void doFinalProcess() {
		// sample size �� �����Ͽ� bitmap load.
		Bitmap bitmap = loadImageWithSampleSize(getTempImageFile());

		// image boundary size �� �µ��� �̹��� ���.
		bitmap = resizeImageWithinBoundary(bitmap);

		// ��� file �� �� �� �ִ� �޼��� ����.
		saveBitmapToFile(bitmap);

		// show image on ImageView
		Bitmap bm = BitmapFactory.decodeFile(getTempImageFile().getAbsolutePath());
		finishedCB.onOperationFinished(bm);
//		((ImageView) findViewById(ImageSelected)).setImageBitmap(bm);
	}

	private void saveBitmapToFile(Bitmap bitmap) {
		File target = getTempImageFile();
		try {
			FileOutputStream fos = new FileOutputStream(target, false);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** �̹��� ������ ���� ��, ī�޶� rotation ������ ������ ȸ�� ������. */
	private void correctCameraOrientation(File imgFile) {
		Bitmap bitmap = loadImageWithSampleSize(imgFile);
		try {
			ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int exifRotateDegree = exifOrientationToDegrees(exifOrientation);
			bitmap = rotateImage(bitmap, exifRotateDegree);
			saveBitmapToFile(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Bitmap rotateImage(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
			} catch (OutOfMemoryError ex) {
			}
		}
		return bitmap;
	}

	/**
	 * EXIF������ ȸ�������� ��ȯ�ϴ� �޼���
	 * 
	 * @param exifOrientation
	 *            EXIF ȸ����
	 * @return ���� ����
	 */
	private int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	/** ���ϴ� ũ���� �̹����� options ����. */
	private Bitmap loadImageWithSampleSize(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		int width = options.outWidth;
		int height = options.outHeight;
		int longSide = Math.max(width, height);
		int sampleSize = 1;
		if (longSide > mImageSizeBoundary) {
			sampleSize = longSide / mImageSizeBoundary;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		options.inPurgeable = true;
		options.inDither = false;

		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		return bitmap;
	}

	/**
	 * mImageSizeBoundary ũ��� �̹��� ũ�� ����. mImageSizeBoundary ���� ���� ��� resize����
	 * ����.
	 */
	private Bitmap resizeImageWithinBoundary(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width > height) {
			if (width > mImageSizeBoundary) {
				bitmap = resizeBitmapWithWidth(bitmap, mImageSizeBoundary);
			}
		} else {
			if (height > mImageSizeBoundary) {
				bitmap = resizeBitmapWithHeight(bitmap, mImageSizeBoundary);
			}
		}
		return bitmap;
	}

	private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
		if (source == null)
			return null;

		int width = source.getWidth();
		int height = source.getHeight();

		float resizeFactor = wantedHeight * 1f / height;

		int targetWidth, targetHeight;
		targetWidth = (int) (width * resizeFactor);
		targetHeight = (int) (height * resizeFactor);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

		return resizedBitmap;
	}

	private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
		if (source == null)
			return null;

		int width = source.getWidth();
		int height = source.getHeight();

		float resizeFactor = wantedWidth * 1f / width;

		int targetWidth, targetHeight;
		targetWidth = (int) (width * resizeFactor);
		targetHeight = (int) (height * resizeFactor);

		Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

		return resizedBitmap;
	}

	private void copyUriToFile(Uri srcUri, File target) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// ��Ʈ�� ����
			inputStream = (FileInputStream) getContentResolver().openInputStream(srcUri);
			outputStream = new FileOutputStream(target);

			// ä�� ����
			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();

			// ä���� ���� ��Ʈ�� ����
			long size = fcin.size();
			fcin.transferTo(0, size, fcout);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fcout.close();
			} catch (IOException ioe) {
			}
			try {
				fcin.close();
			} catch (IOException ioe) {
			}
			try {
				outputStream.close();
			} catch (IOException ioe) {
			}
			try {
				inputStream.close();
			} catch (IOException ioe) {
			}
		}
	}

	private void cropImage() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> cropToolLists = getPackageManager().queryIntentActivities(intent, 0);
		int size = cropToolLists.size();
		if (size == 0) {
			// crop �� ó���� ���� ����. ��ٷ� ó��.
			doFinalProcess();
		} else {
			intent.setData(Uri.fromFile(getTempImageFile()));
			intent.putExtra("aspectX", mCropAspectWidth);
			intent.putExtra("aspectY", mCropAspectHeight);
			intent.putExtra("output", Uri.fromFile(getTempImageFile()));
			Intent i = new Intent(intent);
			ResolveInfo res = cropToolLists.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, REQ_CODE_PICK_CROP);
		}
	}
}