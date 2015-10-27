/*
 * Copyright (C) 2015 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.floatviewfinal.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cn.finalteam.floatviewfinal.widget.FloatView;
import java.lang.ref.WeakReference;

/**
 * Desction:Float view service
 * Author:pengjianbo
 * Date:15/10/26 下午5:15
 */
public class FloatViewService extends Service{

    private FloatView mFloatView;
    private IBinder mFloatViewServiceBinder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mFloatViewServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatView != null) {
            return START_STICKY;
        }

        mFloatView = new FloatView(this);
        mFloatViewServiceBinder = new FloatViewServiceBinder(this);

        return START_REDELIVER_INTENT;
    }

    public void showFloat() {
        if ( mFloatView != null ) {
            mFloatView.show();
        }
    }

    public void hideFloat() {
        if ( mFloatView != null ) {
            mFloatView.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mFloatView != null ) {
            mFloatView.destroy();
        }
    }

    public class FloatViewServiceBinder extends Binder {

        private final WeakReference<FloatViewService> mService;

        FloatViewServiceBinder(FloatViewService service) {
            mService = new WeakReference<>(service);
        }

        public FloatViewService getService() {
            return mService.get();
        }
    }
}
