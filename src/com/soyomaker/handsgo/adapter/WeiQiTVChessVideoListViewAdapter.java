package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.go.ChessVideo;
import com.soyomaker.handsgo.net.weiqitv.ReadWeiQiTV;
import com.soyomaker.handsgo.util.DialogUtils;

public class WeiQiTVChessVideoListViewAdapter extends BaseAdapter {
	/**
	 * The Class ListViewHolder.
	 */
	private final class ListViewHolder {

		public RelativeLayout videoLayout;

		public ImageView videoImageView;

		/** The match name text view. */
		public TextView videoTitleTextView;

		public RelativeLayout videoLayout2;

		public ImageView videoImageView2;

		/** The match name text view. */
		public TextView videoTitleTextView2;
	}

	private Context mContext;

	private Handler mMainThreadHandler;

	/** The m inflater. */
	private LayoutInflater mInflater;

	/** The m chess videos. */
	private ArrayList<ChessVideo> mChessVideos;

	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Config.RGB_565)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher)
			.showStubImage(R.drawable.ic_launcher).build();

	/**
	 * Instantiates a new list view adapter.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param chessManuals
	 *            the chess manuals
	 */
	public WeiQiTVChessVideoListViewAdapter(Context ctx,
			ArrayList<ChessVideo> chessVideos) {
		this.mMainThreadHandler = new Handler(Looper.getMainLooper());
		this.mContext = ctx;
		this.mChessVideos = chessVideos;
		this.mInflater = LayoutInflater.from(ctx);
	}

	/**
	 * Instantiates a new list view adapter.
	 * 
	 * @param ctx
	 *            the ctx
	 */
	public WeiQiTVChessVideoListViewAdapter(Context ctx) {
		this.mMainThreadHandler = new Handler(Looper.getMainLooper());
		this.mContext = ctx;
		this.mChessVideos = new ArrayList<ChessVideo>();
		this.mInflater = LayoutInflater.from(ctx);
	}

	/**
	 * Gets the chess manuals.
	 * 
	 * @return the chess manuals
	 */
	public ArrayList<ChessVideo> getChessVideos() {
		return mChessVideos;
	}

	/**
	 * Sets the chess manuals.
	 * 
	 * @param mChessManuals
	 *            the new chess manuals
	 */
	public void setChessVideos(ArrayList<ChessVideo> mChessVideos) {
		this.mChessVideos = mChessVideos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (mChessVideos.size() + 1) / 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ListViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.weiqitv_listview_item,
					null);
			viewHolder = new ListViewHolder();
			viewHolder.videoLayout = (RelativeLayout) convertView
					.findViewById(R.id.video_layout);
			viewHolder.videoTitleTextView = (TextView) convertView
					.findViewById(R.id.video_title);
			viewHolder.videoImageView = (ImageView) convertView
					.findViewById(R.id.video_image);
			viewHolder.videoLayout2 = (RelativeLayout) convertView
					.findViewById(R.id.video_layout_2);
			viewHolder.videoTitleTextView2 = (TextView) convertView
					.findViewById(R.id.video_title_2);
			viewHolder.videoImageView2 = (ImageView) convertView
					.findViewById(R.id.video_image_2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		int id1 = position * 2;
		if (mChessVideos.size() > id1) {
			final ChessVideo chessVideo = mChessVideos.get(id1);
			viewHolder.videoLayout.setVisibility(View.VISIBLE);
			viewHolder.videoLayout
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							goIntoPlayVideo(chessVideo);
						}
					});
			viewHolder.videoTitleTextView.setText(chessVideo.getVideoTitle());
			ImageLoader.getInstance().displayImage(
					chessVideo.getVideoImageUrl(), viewHolder.videoImageView,
					options);
		} else {
			viewHolder.videoLayout.setVisibility(View.GONE);
		}
		int id2 = position * 2 + 1;
		if (mChessVideos.size() > id2) {
			final ChessVideo chessVideo2 = mChessVideos.get(id2);
			viewHolder.videoLayout2.setVisibility(View.VISIBLE);
			viewHolder.videoLayout2
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							goIntoPlayVideo(chessVideo2);
						}
					});
			viewHolder.videoTitleTextView2.setText(chessVideo2.getVideoTitle());
			ImageLoader.getInstance().displayImage(
					chessVideo2.getVideoImageUrl(), viewHolder.videoImageView2,
					options);
		} else {
			viewHolder.videoLayout2.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void openWebActivity(String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(url);
			intent.setData(uri);
			mContext.startActivity(intent);
		} catch (Exception exception) {
			exception.printStackTrace();
			Toast.makeText(mContext,
					R.string.video_open_fail_can_not_find_browser,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void openVideoActivity(String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String type = "video/* ";
			Uri uri = Uri.parse(url);
			intent.setDataAndType(uri, type);
			mContext.startActivity(intent);
		} catch (Exception exception) {
			exception.printStackTrace();
			Toast.makeText(mContext,
					R.string.video_open_fail_can_not_find_video_player,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void goIntoPlayVideo(final ChessVideo chessVideo) {
		String url = chessVideo.getVideoUrl();
		if (!TextUtils.isEmpty(url)) {
			Log.e("视频播放地址", url);
			if (url.endsWith(".ovp")) {
				openWebActivity(url);
			} else {
				openVideoActivity(url);
			}
		} else {
			DialogUtils.showProgressDialog(mContext, R.string.video_loading,
					true);
			new Thread() {
				public void run() {
					final String url2 = ReadWeiQiTV.getChessVideoUrl(mContext,
							chessVideo).getVideoUrl();
					mMainThreadHandler.post(new Runnable() {

						@Override
						public void run() {
							DialogUtils.dismissProgressDialog();
							if (!TextUtils.isEmpty(url2)) {
								Log.e("视频播放地址", url2);
								if (url2.endsWith(".ovp")) {
									openWebActivity(url2);
								} else {
									openVideoActivity(url2);
								}
							} else {
								DialogUtils.showMessageDialog(mContext,
										R.string.server_connect_timeout);
							}
						}
					});
				}
			}.start();
		}
	}

}
