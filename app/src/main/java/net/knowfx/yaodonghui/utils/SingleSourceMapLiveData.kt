package net.knowfx.yaodonghui.utils

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 设置并监听单一数据源，并做数据转换时使用 LiveData
 * 方便于当需要切换数据源时自动取消掉前一个数据源的监听
 *
 * @param <F> 数据源类型
 * @param <R> 转换的结果类型
</R></F> */
class SingleSourceMapLiveData<F, R>
/**
 * 创建对象时传入需要转换数据的方法
 *
 * @param mapFunction 将数据源的F类型转为结果数据类型R
 */(private val lastMapFunction: Function<F, R>) : MutableLiveData<R>() {
    private var lastSource: LiveData<F>? = null
    private var lastData: F? = null
    private var lastResult: R? = null
    private val observer = Observer<F> { t ->
        if (t != null && t === lastData) {
            return@Observer
        }
        lastData = t
        val mapResult = lastMapFunction.apply(t)
        lastResult = mapResult
        value = lastResult
    }

    /**
     * 设置数据源，当有已设置过的数据源时会取消该数据源的监听
     *
     * @param source
     */
    fun setSource(source: LiveData<F>) {
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