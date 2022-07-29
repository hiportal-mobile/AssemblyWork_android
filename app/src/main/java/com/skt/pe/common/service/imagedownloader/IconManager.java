package com.skt.pe.common.service.imagedownloader;

import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.util.ResourceUtil;
import com.skt.pe.util.StringUtil;

/*
 * 트위터에서 사용하는 이미지캐쉬 매니저...
 * 동일한 URL을 동시에 두개에서 요청 시 Pending 하는 기능을 가지고 있다.
 * 하지만 문제점이 다운로드 모듈은 포함하지 않는다는 점... (외부에서 구현 필요)
 * 캐쉬의 최대 크기가 없어 무한정 메모리를 점유한다는 2가지 문제가 있다.
 * Common에 함유된 인터넷에서 다운로드 받은 ImageDownloader 클래스의 기능과 이것과 추후 통합시킬 필요성이 있다.
 */

public class IconManager {
	private static Map<String, Icon> iconMap = new HashMap<String, Icon>();
	
	public static Drawable download(String url) {
		Drawable iconImg = null;
		
		try {
			boolean isStartDownload = IconManager.reserveDrawable(url);
			Log.i("IconManager", "isStartDownload : "+isStartDownload);
			
			if (isStartDownload == true) {
				SKTUtil.log("TEST", "IconManager.getDrawableByUrl: " + url);
				try {
					iconImg = ResourceUtil.getDrawableByUrl(url);
					IconManager.setDrawable(url, iconImg);
				} catch (Exception e) {
					IconManager.removeDrawable(url);
					e.printStackTrace();
				}
			} else {
				SKTUtil.log("TEST", "IconManager.wait: "  + url);
				for (int i=0; i<150; i++) {
					Thread.sleep(100);
					if (IconManager.isDone(url) == true) {
						iconImg = IconManager.getDrawable(url);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return iconImg;
	}
	
	public static boolean reserveDrawable(String url) {
		synchronized (iconMap) {
			if (iconMap.get(url) == null) {
				Icon icon = new Icon();
				iconMap.put(url, icon);
				return true;
			}
			return false;
		}
	}
	
	public static void setDrawable(String url, Drawable drawable) {
		Icon icon = iconMap.get(url);
		icon.drawable = drawable;
		icon.isDone = true;
	}
	
	public static Drawable getDrawable(String url) {
		Icon icon = iconMap.get(url);
		if (icon == null)
			return null;
		
		return icon.drawable;
	}
	
	public static void removeDrawable(String url) {
		synchronized (iconMap) {
			SKTUtil.log("TEST", "URL Removed: " + url);
			iconMap.remove(url);
		}
	}
	
	public static boolean isDone(String url) {
		Icon icon = iconMap.get(url);
		if (icon == null)
			return false;
		
		return icon.isDone;
	}
	
	public static void clear() {
		iconMap.clear();
	}
	
	public static class Icon {
		public Drawable drawable = null;
		public boolean isDone = false;
	}
	
	public class DownloadTask extends AsyncTask<String, Void, Drawable> {
		private String mUrl = null;
		private DownloadListener mListener = null;
		
		public DownloadTask(String url, DownloadListener listener) {
			this.mUrl  = url;
			this.mListener = listener;
		}

		@Override
		protected Drawable doInBackground(String... arg0) {
			Drawable iconImg = null;
			try {
				//TODO 잘못된 경로
				if(StringUtil.isNull(mUrl) == false) {
					try {
						boolean isStartDownload = IconManager.reserveDrawable(mUrl);
						if (isStartDownload == true) {
							try {
								iconImg = ResourceUtil.getDrawableByUrl(mUrl);
								IconManager.setDrawable(mUrl, iconImg);
							} catch (Exception e) {
								IconManager.removeDrawable(mUrl);
							}
						} else {
							for (int i=0; i<150; i++) {
								Thread.sleep(100);
								if (IconManager.isDone(mUrl) == true) {
									iconImg = IconManager.getDrawable(mUrl);
									break;
								}
							}
						}
					} catch (Exception e) {
					}
				}
			} catch(Exception e) {
				cancel(true);
			}
			return iconImg; 
		}

		@Override
		protected void onPostExecute(Drawable iconImg) {
			this.mListener.onDownloaded(iconImg);
		}
	}
	
	public static interface DownloadListener {
		void onDownloaded(Drawable icon);
	}
}
