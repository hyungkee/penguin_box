package heuristy.ALLA.penguinbox;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab5Fragment extends TabFragment {
	Tab5Fragment(Activity activity) {
		super(activity);
	}

	ListView settingList;
	IconTextListAdapter adapter;
	static String[] listTextFlag;
	static Activity[] linkedActivityFlag;

	public void onCreate() {

		settingList = (ListView) mainView.findViewById(R.id.settingList);

		// 각 리스트의 정보
		listTextFlag = new String[] { "내 프로필", "알림 설정", "공유 범위", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A" };
		linkedActivityFlag = new Activity[3];

		adapter = new IconTextListAdapter(mContext);
		for (int i = 0; i < listTextFlag.length; i++)
			adapter.addItem(new IconTextItem(mActivity.getResources().getDrawable(R.drawable.penguin_box), listTextFlag[i]));

		settingList.setAdapter(adapter);

		settingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				IconTextItem curItem = (IconTextItem) adapter.getItem(position);
				switch (position) {
				case 0:// 내 프로필
					Intent intent;
					intent = new Intent(mActivity, Tab5Page1.class);
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(0, 0);// 애니메이션 제거
					break;
				case 1:// 알림 설정
					break;
				case 2:// 공유 범위
					break;
				}
			}

		});

	}

	public void onItemSelected(MenuItem item) {

		int id = item.getItemId();
	}

	public void destroyTabFragment() {
		// settingList이 프로젝트 파기시 detect하는 경우를 방지
		settingList.setOnItemClickListener(null);
		settingList.setAdapter(null);
		settingList = null;
		adapter = null;
	}

	// 사용자 정의 ListView를 위한 컴퍼넌트들

	public class IconTextItem {

		private Drawable mIcon;
		private String[] mData;
		private boolean mSelectable = true;

		public IconTextItem(Drawable icon, String[] obj) {
			mIcon = icon;
			mData = obj;
		}

		public IconTextItem(Drawable icon, String obj01) {
			mIcon = icon;

			mData = new String[3];
			mData[0] = obj01;
		}

		public boolean isSelectable() {
			return mSelectable;
		}

		public void setSelectable(boolean selectable) {
			mSelectable = selectable;
		}

		public String[] getData() {
			return mData;
		}

		public String getData(int index) {
			if (mData == null || index >= mData.length) {
				return null;
			}

			return mData[index];
		}

		public void setData(String[] obj) {
			mData = obj;
		}

		public void setIcon(Drawable icon) {
			mIcon = icon;
		}

		public Drawable getIcon() {
			return mIcon;
		}

		public int compareTo(IconTextItem other) {
			if (mData != null) {
				String[] otherData = other.getData();
				if (mData.length == otherData.length) {
					for (int i = 0; i < mData.length; i++) {
						if (!mData[i].equals(otherData[i])) {
							return -1;
						}
					}
				} else {
					return -1;
				}
			} else {
				throw new IllegalArgumentException();
			}

			return 0;
		}

	}

	public class IconTextListAdapter extends BaseAdapter {

		private Context mContext;

		private List<IconTextItem> mItems = new ArrayList<IconTextItem>();

		public IconTextListAdapter(Context context) {
			mContext = context;
		}

		public void addItem(IconTextItem it) {
			mItems.add(it);
		}

		public void setListItems(List<IconTextItem> lit) {
			mItems = lit;
		}

		public int getCount() {
			return mItems.size();
		}

		public Object getItem(int position) {
			return mItems.get(position);
		}

		public boolean areAllItemsSelectable() {
			return false;
		}

		public boolean isSelectable(int position) {
			try {
				return mItems.get(position).isSelectable();
			} catch (IndexOutOfBoundsException ex) {
				return false;
			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			IconTextView itemView;
			if (convertView == null) {
				itemView = new IconTextView(mContext, mItems.get(position));
			} else {
				itemView = (IconTextView) convertView;

				itemView.setIcon(mItems.get(position).getIcon());
				itemView.setText(0, mItems.get(position).getData(0));
			}

			return itemView;
		}

	}

	public class IconTextView extends LinearLayout {

		private ImageView mIcon;
		private TextView mText01;

		public IconTextView(Context context, IconTextItem aItem) {
			super(context);

			// Layout Inflation
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.tab5_listitem, this, true);

			// Set Icon
			mIcon = (ImageView) findViewById(R.id.tab5ListImage);
			mIcon.setImageDrawable(aItem.getIcon());

			// Set Text 01
			mText01 = (TextView) findViewById(R.id.tab5ListText);
			mText01.setText(aItem.getData(0));
		}

		public void setText(int index, String data) {
			if (index == 0) {
				mText01.setText(data);
			} else {
				throw new IllegalArgumentException();
			}
		}

		public void setIcon(Drawable icon) {
			mIcon.setImageDrawable(icon);
		}

	}

}
