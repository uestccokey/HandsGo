/*
 * 
 */
package com.soyomaker.handsgo;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.soyomaker.handsgo.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * The Class SettingActivity.
 */
public class SettingActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	/** The auto next interval key. */
	private String AUTO_NEXT_INTERVAL_KEY = "AUTO_NEXT_INTERVAL";

	/** The about key. */
	private String ABOUT_KEY = "ABOUT";

	/** The auto next interval edit pref. */
	private EditTextPreference autoNextIntervalEditPref;

	/** The m about pref. */
	private Preference mAboutPref;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.setting_preference);
		setContentView(R.layout.setting);
		autoNextIntervalEditPref = (EditTextPreference) findPreference(AUTO_NEXT_INTERVAL_KEY);
		autoNextIntervalEditPref.setSummary(AppPrefrence
				.getAutoNextInterval(this) + "毫秒");
		mAboutPref = (Preference) findPreference(ABOUT_KEY);
		mAboutPref.setSummary(String.format(
				getResources().getString(R.string.local_version),
				AppUtil.getVersion(this))
				+ " "
				+ String.format(
						getResources().getString(R.string.server_version),
						MobclickAgent.getConfigParams(this,
								Constant.SERVER_VERSION_STRING)));
		autoNextIntervalEditPref.setOnPreferenceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange
	 * (android.preference.Preference, java.lang.Object)
	 */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(AUTO_NEXT_INTERVAL_KEY)) {
			preference.setSummary(newValue.toString() + "毫秒");
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
