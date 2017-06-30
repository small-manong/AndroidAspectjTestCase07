package com.juelian.mipop;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {

    private String appLabel;
    private Drawable appIcon;
    private String pkgName;
    private String clsName;
    
	public String getClsName() {
		return this.clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}
	private Intent intent;

    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
}

