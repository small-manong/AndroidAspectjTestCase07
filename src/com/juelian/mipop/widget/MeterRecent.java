package com.juelian.mipop.widget;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.android.internal.statusbar.IStatusBarService;
import com.juelian.mipop.JueLianUtils;
import com.juelian.mipop.R;

public class MeterRecent extends MeterBase {
	public static final String NAME = MeterRecent.class.getSimpleName();

	public MeterRecent(Context paramContext) {
		super(paramContext);
		Register(NAME, this);
		setSoundEffectsEnabled(true);
		setImageResource(R.drawable.recent_selector);
		setResId(R.mipmap.recent, R.mipmap.recent_pressed);
	}

	public void Click() {
		Log.i("way", "recent  click");
		playSoundEffect(0);
		new Thread() {
			public void run() {
				IStatusBarService iStatusBarService = IStatusBarService.Stub
						.asInterface(ServiceManager.getService("statusbar"));
				if (iStatusBarService != null) {
					try {
						iStatusBarService.topAppWindowChanged(false);
						iStatusBarService.toggleRecentApps();
						return;
					} catch (RemoteException e) {
						Log.i("Input", "DeadOjbectException");
					}
				}
			}
		}.start();
	}

	public void LongClick() {
		Log.i("juelian", "recent long click");
		JueLianUtils.switchFunction(JueLianUtils.MCWR);
	}
}
