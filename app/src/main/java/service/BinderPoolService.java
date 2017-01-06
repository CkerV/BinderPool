package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import common.BinderPool;

/**
 * Created by ckerv on 17/1/6.
 */
public class BinderPoolService extends Service {

    private IBinder mBinder = new BinderPool.BinderPoolImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
