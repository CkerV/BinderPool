package impl;

import android.os.RemoteException;

import com.ckev.bindpooltest.IMinusOperation;

/**
 * Created by ckerv on 17/1/6.
 */
public class MinusOperationImpl extends IMinusOperation.Stub {
    @Override
    public int minus(int a, int b) throws RemoteException {
        return  a - b;
    }
}
