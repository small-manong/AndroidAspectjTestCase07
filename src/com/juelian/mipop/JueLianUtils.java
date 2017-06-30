package com.juelian.mipop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.juelian.mipop.widget.MeterBase;

public class JueLianUtils {

	public static Context mContext = MeterBase.mContext;
	private static AudioManager audioManager;
	private static boolean isEnable = false;
	
	public static final String MCWB = "mipop_choose_what_back";
	public static final String MCWH = "mipop_choose_what_home";
	public static final String MCWM = "mipop_choose_what_menu";
	public static final String MCWR = "mipop_choose_what_recl";
	public static final String FIRKEY = "firkey";
	
	public static int getAlpha() {
		return Settings.System.getInt(mContext.getContentResolver(),
				"juelian_button_alpha_md", 255);
	}

	public static ArrayList<String> getWhiteList() {
		BufferedReader br = null;
		ArrayList<String> aList = new ArrayList<String>();
		// default white list
		aList.add("com.android.phone");
		aList.add("com.android.systemui");
		aList.add("com.android.mms");
		aList.add("com.android.contacts");
		aList.add(mContext.getPackageName());
		File file = new File(mContext.getFilesDir().getAbsolutePath()
				+ "/white_list.txt");
		try {
			br = new BufferedReader(new FileReader(file));
			String packname = null;
			while ((packname = br.readLine()) != null) {
				if (!aList.contains(packname)) {
					aList.add(packname);
				}
			}
		} catch (FileNotFoundException e) {
			Log.e("mijl-->", "can't not found" + file.toString());
			Log.e("mijl-->", "add default packname list");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("mijl-->",
					"reader is closed or some other I/O error occurs");
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return aList;
	}

	public static void KillApp(Context context) {
		ArrayList<String> aList = getWhiteList();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> mList = mActivityManager.getRunningTasks(1);
		if (mList.iterator().hasNext()) {
			// ������ȡһ��Ԫ��
			PackageManager pm = context.getPackageManager();
			RunningTaskInfo info = mList.iterator().next();
			String packName;
			String appName;
			// ��ǰջ�Ļ�Ծactibity
			packName = info.topActivity.getPackageName();
			//
			try {
				// ApplicationInfo ai = ;
				appName = pm.getApplicationLabel(
						pm.getApplicationInfo(packName, 0)).toString();
				if (aList.contains(packName)) {
					Toast.makeText(context,
							"�޷������������еģ�" + "\"" + appName + "\"", Toast.LENGTH_LONG).show();
					return;
				}
				/*
				 * for(String s : aList){ if (packName.equals(s)) {
				 * Log.d("mijl-->", "this"+ packName + "is whitelist");
				 * Toast.makeText(context, "�޷������������еģ�" + "\""+appName + "\"",
				 * 1).show(); return; } }
				 */
				Toast.makeText(context, "��ֹͣ" + "\"" + appName + "\"", Toast.LENGTH_LONG)
						.show();
				// �½�һ��ʵ��
				IActivityManager iActivityManager = ActivityManagerNative
						.getDefault();
				// ��ǳ�����Ƴ����������
				iActivityManager.removeTask(info.id, 1);
				// �رճ���
				iActivityManager.forceStopPackage(packName, -2);

			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getSystemInt(Context context, String strKey, int defValues) {
		return Settings.System.getInt(context.getContentResolver(), strKey,
				defValues);
	}
	public static void setSystemInt(Context context,String strKey,int values){
		Settings.System.putInt(context.getContentResolver(), strKey, values);
	}
	/*
	public static boolean isMIUI(){
		return !SystemProperties.get("ro.miui.ui.version.code").isEmpty();
	}
	*/
	public static void switchFunction() {
		int firstkey = getSystemInt(mContext, FIRKEY, 0);
		String whatKey = "";
		if (firstkey == 0) {
			whatKey = MCWB;
		} else if (firstkey == 1) {
			whatKey = MCWH;
		}
		Log.e("mijl-->", firstkey + "---------");
		Log.e("mijl-->", whatKey + "---------");
		switchFunction(whatKey);
	}

	public static void switchFunction2() {
		int firstkey = getSystemInt(mContext, FIRKEY, 0);
		String whatKey = "";
		if (firstkey == 0) {
			whatKey = MCWH;
		} else if (firstkey == 1) {
			whatKey = MCWB;
		}
		Log.e("mijl-->", firstkey + "---------");
		Log.e("mijl-->", whatKey + "---------");
		switchFunction(whatKey);
	}

	public static void switchFunction(String whatKey) {
		switch (getSystemInt(mContext, whatKey, 0)) {
		case 0:// nothing
			break;

		case 1:// lock screen
			new Thread() {
				boolean flag = true;

				public void run() {
					if (flag) {
						try {
							new Instrumentation()
									.sendKeyDownUpSync(KeyEvent.KEYCODE_POWER);
						} catch (Exception e) {
							Log.e("mijl-->",
									"deadobjectException,sendKeyDown fail can not shutdown screen");
						}
						flag = false;
					}
				};
			}.start();
			break;

		case 2:// kill current app
			KillApp(mContext);
			break;
			
		case 3:
			audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
			break;
			
		case 4:
			audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			audioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
			break;
		
		// start app
		case 5:
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);//mContext.getSharedPreferences("appIntent", Context.MODE_WORLD_READABLE);
			String packNameString  = sp.getString(whatKey+"_packname", "");
			String clsNameString = sp.getString(whatKey+"_classname", "");
			if (packNameString.isEmpty() && clsNameString.isEmpty()) {
				Toast.makeText(mContext, R.string.not_select_app, Toast.LENGTH_SHORT).show();
			}else {
				Log.d("mijl-->", "mipop-packName:"+packNameString);
				Log.d("mijl-->", "mipop-clsName:"+packNameString);
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName(packNameString, clsNameString);
				mContext.startActivity(intent);
			}
			break;
			
		case 6:
			Intent localIntent = new Intent("miui.intent.action.TOGGLE_TORCH");
			if (!isEnable) {//false
				localIntent.putExtra("miui.intent.extra.IS_ENABLE", true);
				isEnable = true;
			}else {//true
				localIntent.putExtra("miui.intent.extra.IS_ENABLE", false);
				isEnable = false;
			}
			mContext.sendBroadcast(localIntent);
			break;
		case 7:
			Intent intent = new Intent("android.intent.action.CropImage");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
			break;
		case 8:
			String keyString = "ss_s_btn_enabled";
			if (getSystemInt(mContext, keyString, 0) == 0) {
				setSystemInt(mContext, keyString, 1);
			} else {
				setSystemInt(mContext, keyString, 0);
			}
			break;
		case 9:
			Intent localIntent1 = new Intent("android.intent.action.MAIN");
			localIntent1.setClassName("com.haxor", "com.haxor.ScreenFilter");
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(localIntent1);
			break;
		case 10:
	        Intent localIntent2 = new Intent("com.powermo.smartbar.action");
	        localIntent2.putExtra("cmd_toggle_shopm", "last");
	        mContext.sendBroadcast(localIntent2);
			break;
		case 11:
			/* anthor way....
			Intent rebootIntent = new Intent(Intent.ACTION_REBOOT);
			rebootIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
			rebootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(rebootIntent);
			*/
			PowerManager pm = (PowerManager)mContext.getSystemService("power");
			pm.reboot("");
			break;
		case 12:
			Intent shutdownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
			shutdownIntent.putExtra("android.intent.extra.KEY_CONFIRM", true);
			shutdownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(shutdownIntent);
			break;
		}
		
	}
}
