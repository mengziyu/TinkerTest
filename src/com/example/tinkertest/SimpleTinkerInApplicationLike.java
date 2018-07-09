package com.example.tinkertest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;


public class SimpleTinkerInApplicationLike extends DefaultApplicationLike {

	public SimpleTinkerInApplicationLike(Application application,
			int tinkerFlags, boolean tinkerLoadVerifyFlag,
			long applicationStartElapsedTime, long applicationStartMillisTime,
			Intent tinkerResultIntent) {
		super(application, tinkerFlags, tinkerLoadVerifyFlag,
				applicationStartElapsedTime, applicationStartMillisTime,
				tinkerResultIntent);
	}
	@Override
	public void onBaseContextAttached(Context base) {
		super.onBaseContextAttached(base);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TinkerInstaller.install(this);
	}
}
