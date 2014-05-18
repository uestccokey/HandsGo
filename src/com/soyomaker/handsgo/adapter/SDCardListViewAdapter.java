package com.soyomaker.handsgo.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.StringUtil;

public class SDCardListViewAdapter extends BaseAdapter {

	/** The m back. */
	private Bitmap mBack;

	/** The m sgf. */
	private Bitmap mSgf;

	/** The m folder. */
	private Bitmap mFolder;

	/** The m context. */
	private Context mContext;

	/** The m file name list. */
	private List<String> mFileNameList;

	/** The m file path list. */
	private List<File> mFilePathList;

	/**
	 * Instantiates a new file adapter.
	 * 
	 * @param context
	 *            the context
	 * @param fileNames
	 *            the file names
	 * @param filePaths
	 *            the file paths
	 */
	public SDCardListViewAdapter(Context context, List<String> fileNames,
			List<File> filePaths) {
		mContext = context;
		mFileNameList = fileNames;
		mFilePathList = filePaths;
		mBack = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.back);
		mSgf = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.sgf);
		mFolder = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.folder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return mFilePathList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int position) {
		return mFilePathList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLI.inflate(R.layout.file_listview_item, null);
			viewHolder.mFileIconImageView = (ImageView) convertView
					.findViewById(R.id.image_file_icon);
			viewHolder.mFileNameTextView = (TextView) convertView
					.findViewById(R.id.text_file_name);
			viewHolder.mFileInfoTextView = (TextView) convertView
					.findViewById(R.id.text_file_info);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (mFileNameList.get(position).toString().equals("BacktoRoot")) {
			viewHolder.mFileIconImageView.setImageBitmap(mBack);
			viewHolder.mFileNameTextView.setText("返回根目录");
			viewHolder.mFileInfoTextView.setVisibility(View.GONE);
		} else if (mFileNameList.get(position).toString().equals("BacktoUp")) {
			viewHolder.mFileIconImageView.setImageBitmap(mBack);
			viewHolder.mFileNameTextView.setText("返回上一级");
			viewHolder.mFileInfoTextView.setVisibility(View.GONE);
		} else {
			final File file = mFilePathList.get(position);
			String fileName = file.getName();
			if (file.isDirectory()) {
				viewHolder.mFileIconImageView.setImageBitmap(mFolder);
				viewHolder.mFileInfoTextView.setVisibility(View.GONE);
			} else {
				viewHolder.mFileIconImageView.setImageBitmap(mSgf);
				viewHolder.mFileInfoTextView.setVisibility(View.VISIBLE);
				viewHolder.mFileInfoTextView.setText(StringUtil
						.formatFileSize(file.length()));
			}
			viewHolder.mFileNameTextView.setText(fileName);
		}
		return convertView;
	}

	/**
	 * The Class ViewHolder.
	 */
	class ViewHolder {

		/** The m iv. */
		ImageView mFileIconImageView;

		/** The m tv. */
		TextView mFileNameTextView;

		/** The m file info text view. */
		TextView mFileInfoTextView;
	}
}