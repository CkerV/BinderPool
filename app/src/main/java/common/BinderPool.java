package common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ckev.bindpooltest.IBinderPool;

import java.util.concurrent.CountDownLatch;

import impl.AddOperationImpl;
import impl.MinusOperationImpl;
import service.BinderPoolService;

/**
 * Binder连接池
 *
 * 注意:
 * 1.此类在被实例化的时候将ApplicationContext和BinderPoolService进行绑定,BinderPoolService的生命周期和整个应用的绑定
 * 2.外部调用{@link BinderPool#queryBinder(int)}得到不同业务的Binder
 * 3.在{@link BinderPool.BinderPoolImpl}中统一对不同业务的Binder进行管理
 *
 * Created by ckerv on 17/1/6.
 */
public class BinderPool {

    private static final String TAG = BinderPool.class.getSimpleName();

    public static final int BINDER_ADD = 1;
    public static final int BINDER_MINUS = 2;


    private Context mContext;
    private IBinderPool mBinderPool;
    private static BinderPool sInstance;
    /**
     * CountDownLatch,同步用
     */
    private CountDownLatch mBinderPoolLatch;

    private BinderPool(Context context) {
        this.mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if(sInstance == null) {
            synchronized (BinderPool.class) {
                if(sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private void connectBinderPoolService() {
        mBinderPoolLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        //绑定service
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            //同步
            mBinderPoolLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinderPool = IBinderPool.Stub.asInterface(iBinder);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //释放同步
            mBinderPoolLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 不做处理
        }
    };

    /**
     * 死亡容器,用于监听Binder死亡时重启,重启时重新绑定service
     */
    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w(TAG, "binder died");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    /**
     * 对外暴露的方法,通过此方法根据参数获取到不同业务的Binder
     * @param binderCode
     * @return
     */
    public IBinder queryBinder(int binderCode) {
        if(mBinderPool != null) {
            try {
                return mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 统一对不同业务的Binder进行管理
     *
     * 要添加不同业务的Binder,只需设置相应的BinderCode,并在{@link #queryBinder(int)}返回相应的业务Binder实现类即可
     *
     * Created by ckerv on 17/1/6.
     */
    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder iBinder = null;
            switch (binderCode) {
                case BINDER_ADD :
                    iBinder = new AddOperationImpl();
                    break;
                case BINDER_MINUS :
                    iBinder = new MinusOperationImpl();
                    break;
                default:
                    break;
            }
            return iBinder;
        }
    }



}
