package impl;

import android.os.RemoteException;

import com.ckev.bindpooltest.IAddOperation;

/**
 * Created by ckerv on 17/1/6.
 */
public class AddOperationImpl extends IAddOperation.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return  a + b;
    }
}
