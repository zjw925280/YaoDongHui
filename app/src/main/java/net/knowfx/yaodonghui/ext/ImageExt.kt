package net.knowfx.yaodonghui.ext

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.drawable.Drawable
import android.os.Environment
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.load
import coil.size.Scale
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ui.activity.ArticleContentActivity
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.ui.activity.ClassContentActivity
import net.knowfx.yaodonghui.ui.activity.DrawContentActivity
import net.knowfx.yaodonghui.ui.activity.ExploreCommentContentActivity
import net.knowfx.yaodonghui.utils.ExploreTypeEnum
import java.io.File
import java.nio.ByteBuffer

val BASE_IMAGE_SAVE_PATH =
    Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "knowfxImg/"

fun ImageView.intoCorners(url: Any, radius: Float, placeHolder: Int = 0) {
    CoroutineScope(Dispatchers.IO).launch {
        into(
            url = url,
            topLeft = radius,
            topRight = radius,
            bottomLeft = radius,
            bottomRight = radius,
            placeHolder = placeHolder
        )
    }
}

fun ImageView.intoCorners(
    url: Any,
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    placeHolder: Int = 0
) {
    CoroutineScope(Dispatchers.IO).launch {
        into(
            url,
            topLeft = topLeft,
            topRight = topRight,
            bottomLeft = bottomLeft,
            bottomRight = bottomRight,
            placeHolder = placeHolder
        )
    }
}

fun ImageView.intoCircle(url: Any, placeHolder: Int = 0) {
    this.load(url) {
        placeholder(placeHolder)
        error(placeHolder)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.into(
    url: Any, placeHolder: Int = 0,
    success: ((drawable: Drawable) -> Unit)? = null,
    error: ((drawable: Drawable?) -> Unit)? = null,
    start: ((drawable: Drawable?) -> Unit)? = null,
) {
    CoroutineScope(Dispatchers.IO).launch {
        this@into.into(
            url = url,
            topLeft = 0f,
            topRight = 0f,
            bottomLeft = 0f,
            bottomRight = 0f,
            placeHolder = placeHolder,
            success = success,
            error = error,
            start = start
        )
    }
}

fun ImageView.intoLogoOrCover(
    url: Any,
    height: Int = 0,
    corner: Int = 0
) {
    val finalHeight = if (height > 0) height else {
        context.resources.getDimensionPixelOffset(R.dimen.dp_60)
    }
    val finalWidth = finalHeight * 1.618
    val params = layoutParams
    params.width = finalWidth.toInt()
    params.height = finalHeight
    layoutParams = params
    intoWithSize(url, finalWidth.toInt(), finalHeight, corner = corner.toFloat())
}


fun ImageView.intoWithSize(url: Any, width: Int = 0, height: Int = 0, corner: Float) {
    CoroutineScope(Dispatchers.Main).launch {
        val builder = Glide.with(this@intoWithSize).load(url)
            .transform(CenterInside())
            .transform(RoundedCorners(corner.toInt()))
        builder.preload(width, height)
        builder.into(this@intoWithSize)
    }
}

private fun ImageView.into(
    url: Any,
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    success: ((drawable: Drawable) -> Unit)? = null,
    error: ((drawable: Drawable?) -> Unit)? = null,
    start: ((drawable: Drawable?) -> Unit)? = null,
    placeHolder: Int = 0
) {
    load(url) {
        if (placeHolder > 0) {
            placeholder(placeHolder)
            error(placeHolder)
        }
        scale(Scale.FIT)
        allowHardware(false)
        size(ViewSizeResolver(this@into))
        crossfade(false)
        (topLeft > 0f || topRight > 0f || bottomLeft > 0f || bottomRight > 0f).trueLet {
            transformations(
                RoundedCornersTransformation(
                    topLeft,
                    topRight,
                    bottomLeft,
                    bottomRight
                ),
            )
        }
        target(
            onSuccess = { drawable ->
                success?.invoke(drawable)
                load(drawable)
            },
            onError = { error?.invoke(it) },
            onStart = { start?.invoke(it) }
        )
    }
}

fun ImageView.intoCache(url: Any, placeHolder: Int = 0) {
    this.load(url) {
        placeholder(placeHolder)
        error(placeHolder)
        crossfade(true)
    }
}

fun ArrayList<PicData>.getPathString(): String {
    val buffer = StringBuffer()
    for (i in 0 until size) {
        if (this[i].isEmpty()) continue
        if (buffer.isEmpty()) {
            buffer.append(this[i].picLocalPath)
        } else {
            buffer.append(",")
            buffer.append(this[i].picLocalPath)
        }
    }
    return buffer.toString()
}

fun ImageView.createQRCode(content: String) {
    val writer = MultiFormatWriter()
    val matrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500)
    val encoder = BarcodeEncoder()
    into(encoder.createBitmap(matrix))
}

fun Bitmap.getByteArray(): ByteArray {
    val bytes = byteCount
    val buffer = ByteBuffer.allocate(bytes)
    copyPixelsToBuffer(buffer)
    return buffer.array()
}

fun Activity.getArticleShareBitmap(model: String): Bitmap {
    val id = when (model) {
        "ZXH" -> {//真相汇
            R.drawable.icon_share_truth
        }

        "HQST" -> {//汇圈神探
            R.drawable.icon_share_dict
        }

        "DSPH" -> {
            //毒舌评汇
            R.drawable.icon_share_snake
        }

        "HHQ" -> {
            //"画汇圈"
            R.drawable.icon_share_draw
        }

        "JGXT" -> {//"经哥学堂"
            R.drawable.icon_share_class
        }

        else -> {
            R.drawable.icon_share_comment
        }
    }
    return BitmapFactory.decodeResource(resources, id)
}

fun Activity.getExploreCommentBitmap(type: Int, reason: Int): Bitmap {
    val id = if (type == ExploreCommentContentActivity.LAYOUT_TYPE_EXPLORE) {
        when (reason) {
            ExploreTypeEnum.TYPE_EXPLORE_NO_CASH.value -> R.drawable.icon_share_no_cash
            ExploreTypeEnum.TYPE_EXPLORE_DROP_FAST.value -> R.drawable.icon_share_slipt
            ExploreTypeEnum.TYPE_EXPLORE_CHEAT.value -> R.drawable.icon_share_trick
            else -> {
                R.drawable.icon_share_other
            }
        }
    } else {
        R.drawable.icon_share_comment
    }
    return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, id), 80, 80, true)
}

