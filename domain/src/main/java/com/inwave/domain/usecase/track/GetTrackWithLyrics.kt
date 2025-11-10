package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track

class GetTrackWithLyrics(
    private val trackUseCase: GetTrackUseCase,
    private val lyricsUseCase: GetTrackLyricsUseCase
) {
    suspend operator fun invoke(id: String): Result<Track> {
        val trackResult = trackUseCase(id).also { result ->
            result.onFailure {
                return@invoke result
            }
        }

        val lyricsResult = lyricsUseCase(id).also { result ->
            result.onFailure {
                return@invoke trackResult
            }
        }

        return Result.success(
            trackResult.getOrThrow().copy(lyrics = lyricsResult.getOrThrow())
        )
    }
}