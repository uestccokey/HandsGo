/*
 * 
 */
package com.soyomaker.handsgo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Class ChessMatchExpandableListAdapter.
 */
public class SinaChessMatchExpandableListViewAdapter extends
		BaseExpandableListAdapter {

	// 设置组视图的显示文字
	/** The Constant MATCH_TYPES. */
	public static final String[] MATCH_TYPES = new String[] { "世界棋战", "中国棋战",
			"中韩棋战", "韩国棋战", "中日棋战", "日本棋战" };
	// 子视图显示文字
	/** The Constant MATCH_NAME. */
	public static final String[][] MATCH_NAME = new String[][] {
			{ "富士通杯世界围棋锦标赛", "春兰杯世界围棋锦标赛", "三星杯世界围棋公开赛", "LG杯世界围棋棋王赛",
					"农心杯三国擂台赛", "应氏杯世界围棋锦标赛", "正官庄杯世界女子锦标赛" },
			{ "中国围棋甲级联赛", "中国围棋乙级联赛", "中国围棋名人战", "中国围棋天元战", "理光杯围棋赛",
					"阿含桐山杯围棋公开赛" },
			{ "中韩天元对抗赛", "中韩新人王战" },
			{ "韩国王位战", "韩国棋圣战", "韩国名人战", "韩国LG精油杯", "韩国天元战", "韩国霸王战",
					"韩国KBS围棋王战", "韩国新人王战", "韩国国手战", "韩国KT杯", "韩国KTF杯",
					"韩国女流名人战", "韩国王中王战" }, { "中日阿含桐山杯对抗赛" },
			{ "本因坊战", "日本棋圣战", "日本名人战", "日本十段战", "日本天元战", "日本王座战", "日本女子棋战" } };

	/** The Constant MATCH_URL. */
	public static final String[][] MATCH_URL = new String[][] {
			{
					"http://duiyi.sina.com.cn/gibo/fushitong_gibo.asp?cur_page=%d&key=2&keyword=%%B8%%BB%%CA%%BF",
					"http://duiyi.sina.com.cn/gibo/chunlan_gibo.asp?cur_page=%d&key=2&keyword=%%B4%%BA%%C0%%BC",
					"http://duiyi.sina.com.cn/gibo/sanxing_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%FD%%D0%%C7",
					"http://duiyi.sina.com.cn/gibo/lg_gibo.asp?cur_page=%d&key=2&keyword=LG%%B1%%AD",
					"http://duiyi.sina.com.cn/gibo/nongxin_gibo.asp?cur_page=%d&key=2&keyword=%%C5%%A9%%D0%%C4",
					"http://duiyi.sina.com.cn/gibo/yingshi_gibo.asp?cur_page=%d&key=2&keyword=%%D3%%A6%%CA%%CF",
					"http://duiyi.sina.com.cn/gibo/zhengguanzhuang_gibo.asp?cur_page=%d&key=2&keyword=%%D5%%FD%%B9%%D9" },
			{
					"http://duiyi.sina.com.cn/gibo/weijia_gibo.asp?cur_page=%d&key=2&keyword=%%CE%%A7%%BC%%D7",
					"http://duiyi.sina.com.cn/gibo/weiyi_gibo.asp?cur_page=%d&key=2&keyword=%%CE%%A7%%D2%%D2",
					"http://duiyi.sina.com.cn/gibo/mingren_gibo.asp?cur_page=%d&key=2&keyword=%%C3%%FB%%C8%%CB",
					"http://duiyi.sina.com.cn/gibo/tianyuan_gibo.asp?cur_page=%d&key=2&keyword=%%D6%%D0%%B9%%FA%%CE%%A7%%C6%%E5%%CC%%EC%%D4%%AA",
					"http://duiyi.sina.com.cn/gibo/liguang_gibo.asp?cur_page=%d&key=2&keyword=%%C0%%ED%%B9%%E2",
					"http://duiyi.sina.com.cn/gibo/ahan_gibo.asp?cur_page=%d&key=2&keyword=%%B0%%A2%%BA%%AC" },
			{
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%D6%%D0%%BA%%AB%%CE%%A7%%C6%%E5%%CC%%EC%%D4%%AA",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%D6%%D0%%BA%%AB%%CE%%A7%%C6%%E5%%D0%%C2%%C8%%CB" },
			{
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%CD%%F5%%CE%%BB",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%C6%%E5%%CA%%A5",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%C3%%FB%%C8%%CB",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=LG%%BE%%AB%%D3%%CD",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%CC%%EC%%D4%%AA",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%B0%%D4%%CD%%F5",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FAKBS",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%D0%%C2%%C8%%CB",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%B9%%FA%%CA%%D6",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=KT",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=KTF",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%BA%%AB%%B9%%FA%%C5%%AE",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%CD%%F5%%D6%%D0%%CD%%F5" },
			{ "http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%D6%%D0%%C8%%D5%%B0%%A2%%BA%%AC" },
			{
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%B1%%BE%%D2%%F2%%B7%%BB",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%D5%%B1%%BE%%C6%%E5%%CA%%A5",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%D5%%B1%%BE%%C3%%FB%%C8%%CB",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%CA%%AE%%B6%%CE",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%D5%%B1%%BE%%CC%%EC%%D4%%AA",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%D5%%B1%%BE%%CD%%F5%%D7%%F9",
					"http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d&key=2&keyword=%%C8%%D5%%B1%%BE%%C5%%AE" } };

	/** The m context. */
	private Context mContext;

	/**
	 * Instantiates a new chess match expandable list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public SinaChessMatchExpandableListViewAdapter(Context context) {
		this.mContext = context;

	}

	// 自己定义一个获得文字信息的方法
	/**
	 * Gets the text view.
	 * 
	 * @return the text view
	 */
	private TextView getTextView() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, Math.round(48 * density));
		TextView textView = new TextView(mContext);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(Math.round(48 * density), 0, 0, 0);
		textView.setTextSize(18);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	// 重写ExpandableListAdapter中的各个方法
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return MATCH_TYPES.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return MATCH_TYPES[groupPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return MATCH_NAME[groupPosition].length;
	}

	/**
	 * Gets the match url.
	 * 
	 * @param groupPosition
	 *            the group position
	 * @param childPosition
	 *            the child position
	 * @return the match url
	 */
	public String getMatchUrl(int groupPosition, int childPosition) {
		return MATCH_URL[groupPosition][childPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return MATCH_NAME[groupPosition][childPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(0);
		TextView textView = getTextView();
		textView.setTextColor(Color.BLACK);
		textView.setText(getGroup(groupPosition).toString());
		ll.addView(textView);
		return ll;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(0);
		TextView textView = getTextView();
		textView.setText(getChild(groupPosition, childPosition).toString());
		ll.addView(textView);
		return ll;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}
