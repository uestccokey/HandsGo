/*
 * 
 */
package com.soyomaker.handsgo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * The Class MainTabActivity.
 */
public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	/** The Constant TAG_SITUATION_STR. */
	public static final String TAG_SITUATION_STR = "tag situation";

	/** The Constant TAG_LIVE_STR. */
	public static final String TAG_LIVE_STR = "tag live";

	/** The Constant TAG_FAMOUS_STR. */
	public static final String TAG_STUDY_STR = "tag study";

	/** The Constant TAG_FAVORITE_STR. */
	public static final String TAG_FAVORITE_STR = "tag favorite";

	/** The Constant TAG_SORT_STR. */
	public static final String TAG_SORT_STR = "tag sort";

	/** The m host. */
	private TabHost mHost;

	/** The m radio group. */
	private RadioGroup mRadioGroup;

	/** The m radio buttons. */
	private RadioButton[] mRadioButtons;

	private FeedbackAgent agent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tabs);
		initTabContents();
		initRadios();
		agent = new FeedbackAgent(this);
		agent.sync();
		MobclickAgent.setDebugMode(false);
		MobclickAgent.onError(this);
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.update(this);
	}

	/**
	 * Inits the tab contents.
	 */
	private void initTabContents() {
		mHost = getTabHost();
		mHost.clearAllTabs();
		Intent situationIntent = new Intent();
		situationIntent.setClass(this, SituationActivity.class);
		mHost.addTab(mHost
				.newTabSpec(TAG_SITUATION_STR)
				.setIndicator("situation",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(situationIntent));
		Intent liveIntent = new Intent();
		liveIntent.setClass(this, LiveActivity.class);
		mHost.addTab(mHost
				.newTabSpec(TAG_LIVE_STR)
				.setIndicator("live",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(liveIntent));
		Intent famousIntent = new Intent();
		famousIntent.setClass(this, StudyActivity.class);
		mHost.addTab(mHost
				.newTabSpec(TAG_STUDY_STR)
				.setIndicator("study",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(famousIntent));
		Intent favoriteIntent = new Intent();
		favoriteIntent.setClass(this, FavoriteActivity.class);
		mHost.addTab(mHost
				.newTabSpec(TAG_FAVORITE_STR)
				.setIndicator("favorite",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(favoriteIntent));
		Intent sortIntent = new Intent();
		sortIntent.setClass(this, SortActivity.class);
		mHost.addTab(mHost
				.newTabSpec(TAG_SORT_STR)
				.setIndicator("sort",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(sortIntent));
	}

	/**
	 * Inits the radios.
	 */
	private void initRadios() {
		mRadioGroup = (RadioGroup) findViewById(R.id.main_radio);
		final int RADIO_COUNT = 5;
		mRadioButtons = new RadioButton[RADIO_COUNT];
		for (int i = 0; i < RADIO_COUNT; i++) {
			mRadioButtons[i] = (RadioButton) mRadioGroup
					.findViewWithTag("radio_button" + i);
			mRadioButtons[i].setOnCheckedChangeListener(this);
		}
		mRadioButtons[0].setChecked(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
	 * (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked && mHost != null) {
			if (buttonView == mRadioButtons[0]) {
				mHost.setCurrentTabByTag(TAG_SITUATION_STR);
			} else if (buttonView == mRadioButtons[1]) {
				mHost.setCurrentTabByTag(TAG_LIVE_STR);
			} else if (buttonView == mRadioButtons[2]) {
				mHost.setCurrentTabByTag(TAG_STUDY_STR);
			} else if (buttonView == mRadioButtons[3]) {
				mHost.setCurrentTabByTag(TAG_FAVORITE_STR);
			} else if (buttonView == mRadioButtons[4]) {
				mHost.setCurrentTabByTag(TAG_SORT_STR);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.main_menu_feedback);
		menu.add(0, 1, 0, R.string.main_menu_setting);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// 调用反馈提供的接口，进入反馈界面
			agent.startFeedbackActivity();
			return true;
		case 1:
			Intent intent = new Intent(MainTabActivity.this,
					SettingActivity.class);
			startActivity(intent);
			break;
		default:
		}
		return false;
	}

}
