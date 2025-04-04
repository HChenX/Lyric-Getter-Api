package cn.lyric.getter.api.data

import android.graphics.Bitmap
import android.media.MediaMetadata
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import cn.lyric.getter.api.tools.Tools

class ExtraData() {
    var extra: HashMap<String, Any> = HashMap()

    companion object {
        private val keys = arrayOf(MediaMetadata.METADATA_KEY_ART, MediaMetadata.METADATA_KEY_ALBUM_ART, MediaMetadata.METADATA_KEY_DISPLAY_ICON)
    }

    @Deprecated(message = "Not Recommended")
    constructor(customIcon: Boolean, base64Icon: String, useOwnMusicController: Boolean, packageName: String, delay: Int) : this() {
        extra["customIcon"] = customIcon
        extra["base64Icon"] = base64Icon
        extra["useOwnMusicController"] = useOwnMusicController
        extra["packageName"] = packageName
        extra["delay"] = delay
    }

    /**
     * customIcon [Boolean] 是否使用自定义图标
     */
    @Deprecated(message = "Unused, Just set base64Icon or check it is not empty")
    var customIcon: Boolean
        get() = getBoolean("customIcon", false)
        set(value) = setBoolean("customIcon", value)

    /**
     * base64Icon [String] 用于自定义图标的 Base64
     */
    var base64Icon: String
        get() = getString("base64Icon", "")
        set(value) = setString("base64Icon", value)

    /**
     * useOwnMusicController [Boolean] 是否使用自定义音乐控制器（不会系统控制暂停，由音乐软件自行控制）
     */
    var useOwnMusicController: Boolean
        get() = getBoolean("useOwnMusicController", false)
        set(value) = setBoolean("useOwnMusicController", value)

    /**
     * packageName [String] 音乐软件包名
     */
    var packageName: String
        get() = getString("packageName", "")
        set(value) = setString("packageName", value)

    /**
     * artist [String] 当前音乐的艺术家
     *
     * 数据来源于 [MediaMetadata]
     */
    var artist: String
        get() = getString("artist", "")
        set(value) = setString("artist", value)

    /**
     * album [String] 当前音乐的专辑
     *
     * 数据来源于 [MediaMetadata]
     * */
    var album: String
        get() = getString("album", "")
        set(value) = setString("album", value)

    /**
     * title [String] 当前音乐的标题
     *
     * 数据来源于 [MediaMetadata]
     *
     * 部分应用可能会使用此标志传递歌词，请注意判断
     */
    var title: String
        get() = getString("title", "")
        set(value) = setString("title", value)

    /**
     * mediaMetadata [MediaMetadata] 当前音乐的元数据
     */
    var mediaMetadata: MediaMetadata?
        get() {
            val metadata = getParcelable("mediaMetadata")
            return if (metadata == null) null
            else metadata as MediaMetadata
        }
        set(value) {
            val fieldBundle = value!!::class.java.getDeclaredField("mBundle")
            fieldBundle.isAccessible = true
            val bundle = fieldBundle.get(value) as Bundle
            bundle.transferBitmapToBase64()

            setParcelable("mediaMetadata", value)
        }

    /**
     * 因为传输数据大小限制，已自动将 [MediaMetadata] 中 Bitmap 转换为 Base64 数据
     *
     * 请使用本接口获取 [MediaMetadata] 中的 Bitmap 数据！
     *
     * key 可以是 [MediaMetadata.METADATA_KEY_ART], [MediaMetadata.METADATA_KEY_ALBUM_ART], [MediaMetadata.METADATA_KEY_DISPLAY_ICON]
     * 与 [MediaMetadata] 原方法包含的 key 相同
     */
    fun MediaMetadata.getBitmapBase64(key: String): String {
        return this.getString(key);
    }

    /**
     * delay [Int] 延迟时间（毫秒）（此句歌词显示时间，用于控制歌词速度）
     */
    var delay: Int
        get() = getInt("delay", 0)
        set(value) = setInt("delay", value)

    /**
     * 合并 ExtraData
     *
     * @param other [ExtraData]
     */
    fun mergeExtra(other: ExtraData) {
        extra.putAll(other.extra)
    }

    /**
     * 合并 ExtraData 的 HashMap（核心数据）
     *
     * @param other [HashMap<String, Any>]
     */
    fun mergeExtra(other: HashMap<String, Any>) {
        extra.putAll(other)
    }

    private fun Bundle.transferBitmapToBase64() {
        keys.forEach {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.getParcelable<Bitmap>(it, Bitmap::class.java)
            } else {
                this.getParcelable<Bitmap>(it)
            }

            if (bitmap == null) this.putString(it, "")
            else this.putString(it, Tools.bitmapToBase64(bitmap))

            this.remove(it)
        }
    }

    // ---------------------- 数据读写 --------------------------
    private fun getString(key: String, default: String): String {
        return (extra[key] ?: default).toString()
    }

    private fun getBoolean(key: String, default: Boolean): Boolean {
        return (extra[key] ?: default) as Boolean
    }

    private fun getInt(key: String, default: Int): Int {
        return (extra[key] ?: default) as Int
    }

    private fun getFloat(key: String, default: Float): Float {
        return (extra[key] ?: default) as Float
    }

    private fun getLong(key: String, default: Long): Long {
        return (extra[key] ?: default) as Long
    }

    private fun getDouble(key: String, default: Double): Double {
        return (extra[key] ?: default) as Double
    }

    private fun getParcelable(key: String): Parcelable? {
        val parcelable = extra[key]

        return if (parcelable == null) null
        else parcelable as Parcelable
    }

    private fun setString(key: String, value: String) {
        extra[key] = value
    }

    private fun setBoolean(key: String, value: Boolean) {
        extra[key] = value
    }

    private fun setInt(key: String, value: Int) {
        extra[key] = value
    }

    private fun setFloat(key: String, value: Float) {
        extra[key] = value
    }

    private fun setLong(key: String, value: Long) {
        extra[key] = value
    }

    private fun setDouble(key: String, value: Double) {
        extra[key] = value
    }

    private fun setParcelable(key: String, value: Parcelable) {
        extra[key] = value
    }
    // --------------------------------------------------------

    override fun toString(): String {
        val str: StringBuilder = StringBuilder()
        extra.forEach { str.append("${it.key}=${it.value},") }
        return str.toString()
    }

    override fun hashCode(): Int {
        return extra.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ExtraData
        return extra == other.extra
    }
}