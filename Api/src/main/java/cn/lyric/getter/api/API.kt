package cn.lyric.getter.api

import android.media.MediaMetadata
import cn.lyric.getter.api.data.ExtraData

class API {
    /**
     * Has enable
     * 是否启用
     * @return 是否启用，普通环境下为 false，被 Hook 后为 true
     */
    val hasEnable = false

    /**
     *
     * 发送歌词（全参数）
     *
     * @param lyric [String] 歌词
     * @param extra [HashMap] 额外参数
     */
    fun sendLyric(lyric: String, extra: ExtraData = ExtraData()) {}

    /**
     * 发送元数据
     *
     * @param mediaMetadata [MediaMetadata] 元数据
     */
    fun sendMediaData(mediaMetadata: MediaMetadata) {}

    /**
     * 清除歌词
     *
     */
    fun clearLyric() {}

    companion object {
        /**
         * Api Version
         */
        const val API_VERSION = BuildConfig.API_VERSION
    }
}
