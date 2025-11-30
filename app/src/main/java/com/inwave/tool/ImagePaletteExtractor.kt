package com.inwave.tool

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ImagePaletteExtractor @Inject constructor(
    val context: Context
) {
    private val _bitmap: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap.asStateFlow()

    private val _palette: MutableStateFlow<Palette?> = MutableStateFlow(null)
    val palette: StateFlow<Palette?> = _palette.asStateFlow()

    private companion object {
        val fetchCache: MutableMap<String, Pair<Bitmap, Palette>> = mutableMapOf()
    }

    suspend fun fetchImageByUrl(imageUrl: String) {
        if (fetchCache.containsKey(imageUrl)) {
            fetchCache[imageUrl]?.let {
                _bitmap.value = it.first
                _palette.value = it.second
            }
        } else {
            _bitmap.value = ImageLoader(context).execute(
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()
            ).image?.toBitmap()

            tryExtractPaletteFromCurrentBitmap()

            _bitmap.value?.let { bitmap ->
                _palette.value?.let { palette ->
                    fetchCache[imageUrl] = (bitmap to palette)
                }
            }
        }
    }

    private fun tryExtractPaletteFromCurrentBitmap() {
        _bitmap.value?.let { extractPalette(it) }
    }

    private fun extractPalette(bitmap: Bitmap) {
        _palette.value = Palette.from(bitmap.copy(Bitmap.Config.ARGB_8888, false)).generate()
    }
}