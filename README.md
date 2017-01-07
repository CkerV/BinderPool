# Binder连接池

* 当不同的业务模块需要建立不同的Aidl接口意味着如果不进行统一管理的话会产生大量的service，这显然是不合理的，为此必须用同一个service统一管理不同的Aidl Binder

* 对于服务端来说，只需要一个Service，服务端提供一个queryBinder接口，这个接口能够根据业务模块的特征来返回相应的Binder对象给它们

* 关键

    1.建立不同的业务模块的Aidl接口，然后建立一个`IBinderPool`的Aidl接口，里面有`queryBinder(int binderCode)`方法

    2.BinderPoolService，onBind返回`BinderPool.BinderPoolImpl()`

    3.BinderPool，统一进行管理，实例化（getInstance）的时候`bindService(BinderPoolService)`，然后再在`mBinderPoolConnection`得到`mBinderPool`，向外部提供`queryBinder(int binderCode)`方法，内部调用`mBinderPool#queryBinder(int binderCode)`

    4.BinderPoolImpl，IBinderPool的实现类，`queryBinder(int binderCode)`方法根据不同的业务返回不同业务aidl接口的实现类

* 具体实现可看代码以及注释