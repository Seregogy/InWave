package com.inwave.tool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.error
import coil3.toBitmap
import com.inwave.R
import com.inwave.domain.cache.CacheRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ImagePaletteExtractor @Inject constructor(
    val context: Context,
    val cache: CacheRepository<String, Pair<Bitmap, Palette>>
) {
    private val _bitmap: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap.asStateFlow()

    private val _palette: MutableStateFlow<Palette?> = MutableStateFlow(null)
    val palette: StateFlow<Palette?> = _palette.asStateFlow()

    suspend fun fetchImageByUrl(imageUrl: String) {
        ImageLoader(context).execute(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .error(R.drawable.image_item_placeholder)
                .listener(
                    onSuccess = { _, result ->
                        Log.d("ImagePaletteExtractor", "AFTER LOAD")
                        _bitmap.value = result.image.toBitmap()
                    },
                    onError = { _, result ->
                        Log.e("ImagePaletteExtractor", result.throwable.message.toString())
                        _bitmap.value = BitmapFactory.decodeResource(context.resources, R.drawable.image_item_placeholder)
                    }
                )
                .build()
        )

        tryExtractPaletteFromCurrentBitmap()
    }

    private fun tryExtractPaletteFromCurrentBitmap() {
        _bitmap.value?.let { extractPalette(it) }
    }

    private fun extractPalette(bitmap: Bitmap) {
        _palette.value = Palette.from(bitmap.copy(Bitmap.Config.ARGB_8888, false)).generate()
    }
}