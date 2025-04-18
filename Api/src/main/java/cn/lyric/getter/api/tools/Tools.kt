package cn.lyric.getter.api.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.Base64
import cn.lyric.getter.api.listener.LyricReceiver
import java.io.ByteArrayOutputStream

object Tools {
    /**
     * 将 Base64 转换成 Drawable
     *
     * @param [base64] 图片的 Base64
     * @return [Bitmap] 返回图片的 Bitmap?，传入 Base64 无法转换则为 null
     */
    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val bitmapArray: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将 Drawable 转换成 Base64
     *
     * @param drawable 图片
     * @return [String] 返回图片的 Base64
     */
    fun drawableToBase64(drawable: Drawable): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (drawable is AdaptiveIconDrawable) {
                return adaptiveIconDrawableBase64(drawable)
            }
        }
        when (drawable) {
            is BitmapDrawable -> {
                return bitmapToBase64(drawable.bitmap)
            }

            is VectorDrawable -> {
                return bitmapToBase64(makeDrawableToBitmap(drawable))
            }

            else -> {
                return try {
                    bitmapToBase64((drawable as BitmapDrawable).bitmap)
                } catch (_: Exception) {
                    ""
                }
            }
        }
    }

    /**
     * 将 Bitmap 转换成 Base64
     *
     * @param bitmap 图片
     * @return [String] 返回图片的 Base64
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * 将自适应图标转换为位图
     *
     * @param drawable
     * @return [String] 返回自适应图的 Base64
     */
    private fun adaptiveIconDrawableBase64(drawable: AdaptiveIconDrawable): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val background = drawable.background
            val foreground = drawable.foreground
            if (background != null && foreground != null) {
                val layerDrawable = LayerDrawable(arrayOf(background, foreground))
                val createBitmap = Bitmap.createBitmap(layerDrawable.intrinsicWidth, layerDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(createBitmap)
                layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
                layerDrawable.draw(canvas)
                bitmapToBase64(createBitmap)
            } else {
                ""
            }
        } else {
            ""
        }
    }

    private fun makeDrawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.apply {
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return bitmap
    }

    @Deprecated(message = "UnUsed ApiVersion", replaceWith = ReplaceWith("registerLyricListener(context, lyricReceiver)", "cn.lyric.getter.api.tools.Tools.registerLyricListener"))
    fun registerLyricListener(context: Context, apiVersion: Int, lyricReceiver: LyricReceiver) {
        registerLyricListener(context, lyricReceiver)
    }

    /**
     * 注册歌词监听器
     * @param context [context] Context
     * @param lyricReceiver [LyricReceiver]
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerLyricListener(context: Context, lyricReceiver: LyricReceiver) {
        val intentFilter = IntentFilter().apply { addAction("Lyric_Data") }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(lyricReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(lyricReceiver, intentFilter)
        }
    }

    /**
     * 注销歌词侦听器
     *
     * @param context [Context]
     * @param lyricReceiver
     */
    fun unregisterLyricListener(context: Context, lyricReceiver: LyricReceiver) {
        runCatching { context.unregisterReceiver(lyricReceiver) }
    }
}