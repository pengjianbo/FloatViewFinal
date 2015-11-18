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
import cn.finalteam.floatviewfinal.widget.FloatView;

/**
 * Desction:Float view service
 * Author:pengjianbo
 * Date:15/10/26 下午5:15
 */
public class FloatViewService extends Service{

    private FloatView mFloatView;

    @Override
    public IBinder onBind(Intent intent) {
        return new FloatViewServiceBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mFloatView = new FloatView(this);
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

    public void destroyFloat() {
        if ( mFloatView != null ) {
            mFloatView.destroy();
        }
        mFloatView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloat();
    }

    public class FloatViewServiceBinder extends Binder {
        public FloatViewService getService() {
            return FloatViewService.this;
        }
    }
}
