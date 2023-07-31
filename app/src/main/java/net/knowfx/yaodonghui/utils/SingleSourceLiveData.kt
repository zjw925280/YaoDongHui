package net.knowfx.yaodonghui.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 设置并监听单一数据源时使用 LiveData
 * 方便于当需要切换数据源时自动取消掉前一个数据源的监听
 *
 * @param <T> 监听的数据源类型
</T> */
class SingleSourceLiveData<T> : MutableLiveData<T?>() {
    private val mPending = AtomicBoolean(false)
    private var lastSource: LiveData<T>? = null
    private var lastData: T? = null
    private val observer = Observer<T> { t ->
        if (t != null && t === lastData) {
            if (t is Int) {
                value = t
            }
            return@Observer
        }
        lastData = t
        value = t
    }

    /**
     * 判断数据是否是基本数据类型
     *
     * @param t
     * @return true 是 false 否
     */
    private fun isPrimitive(t: T): Boolean {
        try {
            val field: Field = t!!::class.java.getField("TYPE")
            val claz = field[null] as Class<*>
            if (claz.isPrimitive) {
                return true
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return false
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                t?.apply {
                    observer.onChanged(this)
                }
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * 设置数据源，当有已设置过的数据源时会取消该数据源的监听
     *
     * @param source
     */
    fun setSource(source: LiveData<T>) {
        if (lastSource === source) {
            return
        }
        if (lastSource != null) {
            lastSource!!.removeObserver(observer)
        }
        lastSource = source
        if (hasActiveObservers()) {
            lastSource!!.observeForever(observer)
        }
    }

    override fun onActive() {
        super.onActive()
        if (lastSource != null) {
            lastSource!!.observeForever(observer)
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (lastSource != null) {
            lastSource!!.removeObserver(observer)
        }
    }
}