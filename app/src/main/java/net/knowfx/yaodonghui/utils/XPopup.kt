package net.knowfx.yaodonghui.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.lxj.xpopup.animator.PopupAnimator
import com.lxj.xpopup.core.*
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.enums.PopupPosition
import com.lxj.xpopup.enums.PopupType
import com.lxj.xpopup.impl.*
import com.lxj.xpopup.interfaces.*
import com.lxj.xpopup.util.XPermission

object XPopup {

    /**
     * 全局弹窗的设置
     */
    private var primaryColor = Color.parseColor("#121212")
    private var animationDuration = 350
    var statusBarShadowColor = Color.parseColor("#55000000")
    private var shadowBgColor = Color.parseColor("#9F000000")

    fun setShadowBgColor(color: Int) {
        shadowBgColor = color
    }

    fun getShadowBgColor(): Int {
        return shadowBgColor
    }

    /**
     * 设置主色调
     *
     * @param color
     */
    fun setPrimaryColor(color: Int) {
        primaryColor = color
    }

    fun getPrimaryColor(): Int {
        return primaryColor
    }

    fun setAnimationDuration(duration: Int) {
        if (duration >= 0) {
            animationDuration = duration
        }
    }

    fun getAnimationDuration(): Int {
        return animationDuration
    }

    class Builder(private val context: Context) {
        private val popupInfo = PopupInfo()
        fun popupType(popupType: PopupType?): Builder {
            popupInfo.popupType = popupType
            return this
        }

        /**
         * 设置按下返回键是否关闭弹窗，默认为true
         *
         * @param isDismissOnBackPressed
         * @return
         */
        fun dismissOnBackPressed(isDismissOnBackPressed: Boolean?): Builder {
            popupInfo.isDismissOnBackPressed = isDismissOnBackPressed
            return this
        }

        /**
         * 设置点击弹窗外面是否关闭弹窗，默认为true
         *
         * @param isDismissOnTouchOutside
         * @return
         */
        fun dismissOnTouchOutside(isDismissOnTouchOutside: Boolean?): Builder {
            popupInfo.isDismissOnTouchOutside = isDismissOnTouchOutside
            return this
        }

        /**
         * 设置当操作完毕后是否自动关闭弹窗，默认为true。比如：点击Confirm弹窗的确认按钮默认是关闭弹窗，如果为false，则不关闭
         *
         * @param autoDismiss
         * @return
         */
        fun autoDismiss(autoDismiss: Boolean?): Builder {
            popupInfo.autoDismiss = autoDismiss
            return this
        }

        /**
         * 弹窗是否有半透明背景遮罩，默认是true
         *
         * @param hasShadowBg
         * @return
         */
        fun hasShadowBg(hasShadowBg: Boolean?): Builder {
            popupInfo.hasShadowBg = hasShadowBg
            return this
        }

        /**
         * 是否设置背景为高斯模糊背景。默认为false
         * @param hasBlurBg
         * @return
         */
        fun hasBlurBg(hasBlurBg: Boolean): Builder {
            popupInfo.hasBlurBg = hasBlurBg
            return this
        }

        /**
         * 设置弹窗依附的View，Attach弹窗必须设置这个
         *
         * @param atView
         * @return
         */
        fun atView(atView: View?): Builder {
            popupInfo.atView = atView
            return this
        }

        /**
         * 设置弹窗监视的View
         *
         * @param watchView
         * @return
         */
        fun watchView(watchView: View?): Builder {
            popupInfo.watchView = watchView
            popupInfo.watchView.setOnTouchListener { v, event ->
                if (popupInfo.touchPoint == null || event.action == MotionEvent.ACTION_DOWN) popupInfo.touchPoint =
                    PointF(event.rawX, event.rawY)
                false
            }
            return this
        }

        /**
         * 为弹窗设置内置的动画器，默认情况下，已经为每种弹窗设置了效果最佳的动画器；如果你不喜欢，仍然可以修改。
         *
         * @param popupAnimation
         * @return
         */
        fun popupAnimation(popupAnimation: PopupAnimation?): Builder {
            popupInfo.popupAnimation = popupAnimation
            return this
        }

        /**
         * 自定义弹窗动画器
         *
         * @param customAnimator
         * @return
         */
        fun customAnimator(customAnimator: PopupAnimator?): Builder {
            popupInfo.customAnimator = customAnimator
            return this
        }

        /**
         * 设置最大宽度，如果重写了弹窗的getMaxWidth，则以重写的为准
         *
         * @param maxWidth
         * @return
         */
        fun maxWidth(maxWidth: Int): Builder {
            popupInfo.maxWidth = maxWidth
            return this
        }

        /**
         * 设置最大高度，如果重写了弹窗的getMaxHeight，则以重写的为准
         *
         * @param maxHeight
         * @return
         */
        fun maxHeight(maxHeight: Int): Builder {
            popupInfo.maxHeight = maxHeight
            return this
        }

        /**
         * 是否自动打开输入法，当弹窗包含输入框时很有用，默认为false
         *
         * @param autoOpenSoftInput
         * @return
         */
        fun autoOpenSoftInput(autoOpenSoftInput: Boolean?): Builder {
            popupInfo.autoOpenSoftInput = autoOpenSoftInput
            return this
        }

        /**
         * 当弹出输入法时，弹窗是否要移动到输入法之上，默认为true。如果不移动，弹窗很有可能被输入法盖住
         *
         * @param isMoveUpToKeyboard
         * @return
         */
        fun moveUpToKeyboard(isMoveUpToKeyboard: Boolean?): Builder {
            popupInfo.isMoveUpToKeyboard = isMoveUpToKeyboard
            return this
        }

        /**
         * 设置弹窗出现在目标的什么位置，有四种取值：Left，Right，Top，Bottom。这种手动设置位置的行为
         * 只对Attach弹窗和Drawer弹窗生效。
         *
         * @param popupPosition
         * @return
         */
        fun popupPosition(popupPosition: PopupPosition?): Builder {
            popupInfo.popupPosition = popupPosition
            return this
        }

        /**
         * 设置是否给StatusBar添加阴影，目前对Drawer弹窗生效。如果你的Drawer的背景是白色，建议设置为true，因为状态栏文字的颜色也往往
         * 是白色，会导致状态栏文字看不清；如果Drawer的背景色不是白色，则忽略即可
         *
         * @param hasStatusBarShadow
         * @return
         */
        fun hasStatusBarShadow(hasStatusBarShadow: Boolean): Builder {
            popupInfo.hasStatusBarShadow = hasStatusBarShadow
            return this
        }

        /**
         * 弹窗在x方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetX
         * @return
         */
        fun offsetX(offsetX: Int): Builder {
            popupInfo.offsetX = offsetX
            return this
        }

        /**
         * 弹窗在y方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetY
         * @return
         */
        fun offsetY(offsetY: Int): Builder {
            popupInfo.offsetY = offsetY
            return this
        }

        /**
         * 是否启用拖拽，比如：Bottom弹窗默认是带手势拖拽效果的，如果禁用则不能拖拽
         *
         * @param enableDrag
         * @return
         */
        fun enableDrag(enableDrag: Boolean): Builder {
            popupInfo.enableDrag = enableDrag
            return this
        }

        /**
         * 是否水平居中，默认情况下Attach弹窗依靠着目标的左边或者右边，如果isCenterHorizontal为true，则与目标水平居中对齐
         *
         * @param isCenterHorizontal
         * @return
         */
        fun isCenterHorizontal(isCenterHorizontal: Boolean): Builder {
            popupInfo.isCenterHorizontal = isCenterHorizontal
            return this
        }

        /**
         * 是否抢占焦点，默认情况下弹窗会抢占焦点，目的是为了能处理返回按键事件。如果为false，则不在抢焦点，但也无法响应返回按键了
         *
         * @param isRequestFocus 默认为true
         * @return
         */
        fun isRequestFocus(isRequestFocus: Boolean): Builder {
            popupInfo.isRequestFocus = isRequestFocus
            return this
        }

        /**
         * 是否让弹窗内的输入框自动获取焦点，默认是true。弹窗内有输入法的情况下该设置才有效
         *
         * @param autoFocusEditText
         * @return
         */
        fun autoFocusEditText(autoFocusEditText: Boolean): Builder {
            popupInfo.autoFocusEditText = autoFocusEditText
            return this
        }

        /**
         * 是否让使用暗色主题，默认是false。
         *
         * @param isDarkTheme
         * @return
         */
        fun isDarkTheme(isDarkTheme: Boolean): Builder {
            popupInfo.isDarkTheme = isDarkTheme
            return this
        }

        /**
         * 是否点击弹窗背景时将点击事件透传到Activity下，默认是不透传。由于容易引发其他问题，目前只对PartShadow弹窗有效。
         *
         * @param isClickThrough
         * @return
         */
        fun isClickThrough(isClickThrough: Boolean): Builder {
            popupInfo.isClickThrough = isClickThrough
            return this
        }

        /**
         * 是否允许应用在后台的时候也能弹出弹窗，默认是false。注意如果开启这个开关，需要申请悬浮窗权限才能生效。
         *
         * @param enableShowWhenAppBackground
         * @return
         */
        fun enableShowWhenAppBackground(enableShowWhenAppBackground: Boolean): Builder {
            popupInfo.enableShowWhenAppBackground = enableShowWhenAppBackground
            return this
        }

        /**
         * 是否开启三阶拖拽效果，想高德地图上面的弹窗那样可以拖拽的效果
         *
         * @param isThreeDrag
         * @return
         */
        fun isThreeDrag(isThreeDrag: Boolean): Builder {
            popupInfo.isThreeDrag = isThreeDrag
            return this
        }

        /**
         * 是否在弹窗消失后就立即释放资源，杜绝内存泄漏，仅仅适用于弹窗只用一次的场景，默认为false。
         * 如果你的弹窗对象需要用多次，千万不要开启这个设置
         * @param isDestroyOnDismiss
         * @return
         */
        fun isDestroyOnDismiss(isDestroyOnDismiss: Boolean): Builder {
            popupInfo.isDestroyOnDismiss = isDestroyOnDismiss
            return this
        }

        /**
         * 设置弹窗显示和隐藏的回调监听
         *
         * @param xPopupCallback
         * @return
         */
        fun setPopupCallback(xPopupCallback: XPopupCallback?): Builder {
            popupInfo.xPopupCallback = xPopupCallback
            return this
        }
        /****************************************** 便捷方法  */
        /**
         * 显示确认和取消对话框
         *
         * @param title           对话框标题，传空串会隐藏标题
         * @param content         对话框内容
         * @param cancelBtnText   取消按钮的文字内容
         * @param confirmBtnText  确认按钮的文字内容
         * @param confirmListener 点击确认的监听器
         * @param cancelListener  点击取消的监听器
         * @param isHideCancel    是否隐藏取消按钮
         * @return
         */
        fun asConfirm(
            title: CharSequence?,
            content: CharSequence?,
            cancelBtnText: CharSequence?,
            confirmBtnText: CharSequence?,
            confirmListener: OnConfirmListener?,
            cancelListener: OnCancelListener?,
            isHideCancel: Boolean
        ): ConfirmPopupView {
            popupType(PopupType.Center)
            val popupView = ConfirmPopupView(context)
            popupView.setTitleContent(title, content, null)
            popupView.setCancelText(cancelBtnText)
            popupView.setConfirmText(confirmBtnText)
            popupView.setListener(confirmListener, cancelListener)
            if (isHideCancel) popupView.hideCancelBtn()
            popupView.popupInfo = popupInfo
            return popupView
        }

        fun asConfirm(
            title: CharSequence?,
            content: CharSequence?,
            confirmListener: OnConfirmListener?,
            cancelListener: OnCancelListener?
        ): ConfirmPopupView {
            return asConfirm(title, content, null, null, confirmListener, cancelListener, false)
        }

        fun asConfirm(
            title: CharSequence?,
            content: CharSequence?,
            confirmListener: OnConfirmListener?
        ): ConfirmPopupView {
            return asConfirm(title, content, null, null, confirmListener, null, false)
        }

        /**
         * 显示带有输入框，确认和取消对话框
         *
         * @param title           对话框标题，传空串会隐藏标题
         * @param content         对话框内容,，传空串会隐藏
         * @param inputContent    输入框文字内容，会覆盖hint
         * @param hint            输入框默认文字
         * @param confirmListener 点击确认的监听器
         * @param cancelListener  点击取消的监听器
         * @return
         */
        @JvmOverloads
        fun asInputConfirm(
            title: CharSequence?,
            content: CharSequence?,
            inputContent: CharSequence?,
            hint: CharSequence?,
            confirmListener: OnInputConfirmListener?,
            cancelListener: OnCancelListener? = null
        ): InputConfirmPopupView {
            popupType(PopupType.Center)
            val popupView = InputConfirmPopupView(context)
            popupView.setTitleContent(title, content, hint)
            popupView.inputContent = inputContent
            popupView.setListener(confirmListener, cancelListener)
            popupView.popupInfo = popupInfo
            return popupView
        }

        /**
         * 显示带有输入框，确认和取消对话框
         *
         * @param title           对话框标题，传空串会隐藏标题
         * @param content         对话框内容,，传空串会隐藏
         * @param cancelBtnText   取消按钮的文字内容
         * @param confirmBtnText  确认按钮的文字内容
         * @param hint            输入框默认文字
         * @param confirmListener 点击确认的监听器
         * @param cancelListener  点击取消的监听器
         * @return
         */
        fun asInputConfirm(
            title: CharSequence?,
            content: CharSequence?,
            cancelBtnText: CharSequence?,
            confirmBtnText: CharSequence?,
            hint: CharSequence?,
            confirmListener: OnInputConfirmListener?,
            cancelListener: OnCancelListener?
        ): InputConfirmPopupView {
            popupType(PopupType.Center)
            val popupView = InputConfirmPopupView(context)
            popupView.setTitleContent(title, content, "")
            popupView.setCancelText(cancelBtnText)
            popupView.setConfirmText(confirmBtnText)
            popupView.setListener(confirmListener, cancelListener)
            popupView.popupInfo = popupInfo
            return popupView
        }

        fun asInputConfirm(
            title: CharSequence?,
            content: CharSequence?,
            hint: CharSequence?,
            confirmListener: OnInputConfirmListener?
        ): InputConfirmPopupView {
            return asInputConfirm(title, content, null, hint, confirmListener, null)
        }

        fun asInputConfirm(
            title: CharSequence?,
            content: CharSequence?,
            confirmListener: OnInputConfirmListener?
        ): InputConfirmPopupView {
            return asInputConfirm(title, content, null, null, confirmListener, null)
        }

        /**
         * 显示在中间的列表Popup
         *
         * @param title          标题，可以不传，不传则不显示
         * @param data           显示的文本数据
         * @param iconIds        图标的id数组，可以没有
         * @param selectListener 选中条目的监听器
         * @return
         */
        fun asCenterList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            checkedPosition: Int,
            selectListener: OnSelectListener?
        ): CenterListPopupView {
            popupType(PopupType.Center)
            val popupView = CenterListPopupView(context)
                .setStringData(title, data, iconIds)
                .setCheckedPosition(checkedPosition)
                .setOnSelectListener(selectListener)
            popupView.popupInfo = popupInfo
            return popupView
        }

        fun asCenterList(
            title: CharSequence?,
            data: Array<String?>?,
            selectListener: OnSelectListener?
        ): CenterListPopupView {
            return asCenterList(title, data, null, -1, selectListener)
        }

        fun asCenterList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            selectListener: OnSelectListener?
        ): CenterListPopupView {
            return asCenterList(title, data, iconIds, -1, selectListener)
        }

        /**
         * 显示在中间加载的弹窗
         *
         * @return
         */
        @JvmOverloads
        fun asLoading(title: CharSequence? = null): LoadingPopupView {
            popupType(PopupType.Center)
            val popupView = LoadingPopupView(context)
                .setTitle(title)
            popupInfo.isDismissOnTouchOutside = false //Loading弹窗点击外部不消失
            popupView.popupInfo = popupInfo
            return popupView
        }

        /**
         * 显示在底部的列表Popup
         *
         * @param title           标题，可以不传，不传则不显示
         * @param data            显示的文本数据
         * @param iconIds         图标的id数组，可以没有
         * @param checkedPosition 选中的位置，传-1为不选中
         * @param selectListener  选中条目的监听器
         * @return
         */
        fun asBottomList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            checkedPosition: Int,
            enableDrag: Boolean,
            selectListener: OnSelectListener?
        ): BottomListPopupView {
            popupType(PopupType.Bottom)
            val popupView = BottomListPopupView(context)
                .setStringData(title, data, iconIds)
                .setCheckedPosition(checkedPosition)
                .setOnSelectListener(selectListener)
            popupView.popupInfo = popupInfo
            return popupView
        }

        fun asBottomList(
            title: CharSequence?,
            data: Array<String?>?,
            selectListener: OnSelectListener?
        ): BottomListPopupView {
            return asBottomList(title, data, null, -1, true, selectListener)
        }

        fun asBottomList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            selectListener: OnSelectListener?
        ): BottomListPopupView {
            return asBottomList(title, data, iconIds, -1, true, selectListener)
        }

        fun asBottomList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            checkedPosition: Int,
            selectListener: OnSelectListener?
        ): BottomListPopupView {
            return asBottomList(title, data, iconIds, checkedPosition, true, selectListener)
        }

        fun asBottomList(
            title: CharSequence?,
            data: Array<String?>?,
            iconIds: IntArray?,
            enableDrag: Boolean,
            selectListener: OnSelectListener?
        ): BottomListPopupView {
            return asBottomList(title, data, iconIds, -1, enableDrag, selectListener)
        }

        /**
         * 显示依附于某View的列表，必须调用atView()方法，指定依附的View
         *
         * @param data           显示的文本数据
         * @param iconIds        图标的id数组，可以为null
         * @param selectListener 选中条目的监听器
         * @return
         */
        fun asAttachList(
            data: Array<String?>?,
            iconIds: IntArray?,
            selectListener: OnSelectListener?
        ): AttachListPopupView {
            popupType(PopupType.AttachView)
            val popupView = AttachListPopupView(context)
                .setStringData(data, iconIds)
                .setOnSelectListener(selectListener)
            popupView.popupInfo = popupInfo
            return popupView
        }

        /**
         * 大图浏览类型弹窗，单张图片使用场景
         *
         * @param srcView 源View，就是你点击的那个ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在Webview点击场景
         * @return
         */
        fun asImageViewer(
            srcView: ImageView?,
            url: Any?,
            imageLoader: XPopupImageLoader?
        ): ImageViewerPopupView {
            popupType(PopupType.ImageViewer)
            val popupView = ImageViewerPopupView(context)
                .setSingleSrcView(srcView, url)
                .setXPopupImageLoader(imageLoader)
            popupView.popupInfo = popupInfo
            return popupView
        }

        /**
         * 大图浏览类型弹窗，单张图片使用场景
         *
         * @param srcView           源View，就是你点击的那个ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在Webview点击场景
         * @param url               资源id，url或者文件路径
         * @param isInfinite        是否需要无限滚动，默认为false
         * @param placeholderColor  占位View的填充色，默认为-1
         * @param placeholderStroke 占位View的边框色，默认为-1
         * @param placeholderRadius 占位View的圆角大小，默认为-1
         * @param isShowSaveBtn     是否显示保存按钮，默认显示
         * @return
         */
        fun asImageViewer(
            srcView: ImageView?,
            url: Any?,
            isInfinite: Boolean,
            placeholderColor: Int,
            placeholderStroke: Int,
            placeholderRadius: Int,
            isShowSaveBtn: Boolean,
            imageLoader: XPopupImageLoader?
        ): ImageViewerPopupView {
            popupType(PopupType.ImageViewer)
            val popupView = ImageViewerPopupView(context)
                .setSingleSrcView(srcView, url)
                .isInfinite(isInfinite)
                .setPlaceholderColor(placeholderColor)
                .setPlaceholderStrokeColor(placeholderStroke)
                .setPlaceholderRadius(placeholderRadius)
                .isShowSaveButton(isShowSaveBtn)
                .setXPopupImageLoader(imageLoader)
            popupView.popupInfo = popupInfo
            return popupView
        }

        /**
         * 大图浏览类型弹窗，多张图片使用场景
         *
         * @param srcView               源View，就是你点击的那个ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在Webview点击场景
         * @param currentPosition       指定显示图片的位置
         * @param urls                  图片url集合
         * @param srcViewUpdateListener 当滑动ViewPager切换图片后，需要更新srcView，此时会执行该回调，你需要调用updateSrcView方法。
         * @return
         */
        fun asImageViewer(
            srcView: ImageView?, currentPosition: Int, urls: List<Any?>?,
            srcViewUpdateListener: OnSrcViewUpdateListener?, imageLoader: XPopupImageLoader?
        ): ImageViewerPopupView {
            return asImageViewer(
                srcView,
                currentPosition,
                urls,
                isInfinite = false,
                isShowPlaceHolder = false,
                placeholderColor = -1,
                placeholderStroke = -1,
                placeholderRadius = -1,
                isShowSaveBtn = true,
                srcViewUpdateListener = srcViewUpdateListener,
                imageLoader = imageLoader
            )
        }

        /**
         * 大图浏览类型弹窗，多张图片使用场景
         *
         * @param srcView               源View，就是你点击的那个ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在Webview点击场景
         * @param currentPosition       指定显示图片的位置
         * @param urls                  图片url集合
         * @param isInfinite            是否需要无限滚动，默认为false
         * @param isShowPlaceHolder     是否显示默认的占位View，默认为false
         * @param placeholderColor      占位View的填充色，默认为-1
         * @param placeholderStroke     占位View的边框色，默认为-1
         * @param placeholderRadius     占位View的圆角大小，默认为-1
         * @param isShowSaveBtn         是否显示保存按钮，默认显示
         * @param srcViewUpdateListener 当滑动ViewPager切换图片后，需要更新srcView，此时会执行该回调，你需要调用updateSrcView方法。
         * @return
         */
        fun asImageViewer(
            srcView: ImageView?,
            currentPosition: Int,
            urls: List<Any?>?,
            isInfinite: Boolean,
            isShowPlaceHolder: Boolean,
            placeholderColor: Int,
            placeholderStroke: Int,
            placeholderRadius: Int,
            isShowSaveBtn: Boolean,
            srcViewUpdateListener: OnSrcViewUpdateListener?,
            imageLoader: XPopupImageLoader?
        ): ImageViewerPopupView {
            popupType(PopupType.ImageViewer)
            val popupView = ImageViewerPopupView(context)
                .setSrcView(srcView, currentPosition)
                .setImageUrls(urls)
                .isInfinite(isInfinite)
                .isShowPlaceholder(isShowPlaceHolder)
                .setPlaceholderColor(placeholderColor)
                .setPlaceholderStrokeColor(placeholderStroke)
                .setPlaceholderRadius(placeholderRadius)
                .isShowSaveButton(isShowSaveBtn)
                .setSrcViewUpdateListener(srcViewUpdateListener)
                .setXPopupImageLoader(imageLoader)
            popupView.popupInfo = popupInfo
            return popupView
        }

        fun asCustom(popupView: BasePopupView): BasePopupView {
            when (popupView) {
                is CenterPopupView -> {
                    popupType(PopupType.Center)
                }
                is BottomPopupView -> {
                    popupType(PopupType.Bottom)
                }
                is AttachPopupView -> {
                    popupType(PopupType.AttachView)
                }
                is ImageViewerPopupView -> {
                    popupType(PopupType.ImageViewer)
                }
                is PositionPopupView -> {
                    popupType(PopupType.Position)
                }
            }
            popupView.popupInfo = popupInfo
            return popupView
        }
    }

    /**
     * 跳转申请悬浮窗权限界面
     * @param context
     * @param callback
     */
    fun requestOverlayPermission(context: Context?, callback: XPermission.SimpleCallback?) {
        XPermission.create(context).requestDrawOverlays(callback)
    }
}