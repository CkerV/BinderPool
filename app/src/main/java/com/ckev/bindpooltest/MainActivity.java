package com.ckev.bindpooltest;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import common.BinderPool;
import impl.AddOperationImpl;
import impl.MinusOperationImpl;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private IAddOperation mAddOperaion;
    private IMinusOperation mMinusOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doWork();
    }

    /**
     * 测试,开启新的线程
     */
    private void doWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                testAdd();
                testMinus();
            }
        }).start();
    }

    /**
     * 测试加法
     */
    private void testAdd() {
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
        IBinder addBinder = binderPool.queryBinder(BinderPool.BINDER_ADD);
        mAddOperaion = AddOperationImpl.asInterface(addBinder);
        try {
            Log.d(TAG, "add opertion" + mAddOperaion.add(1, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试减法
     */
    private void testMinus() {
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
        IBinder minusBinder = binderPool.queryBinder(BinderPool.BINDER_MINUS);
        mMinusOperation = MinusOperationImpl.asInterface(minusBinder);
        try {
            Log.d(TAG, "minus opertion" + mMinusOperation.minus(1, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
