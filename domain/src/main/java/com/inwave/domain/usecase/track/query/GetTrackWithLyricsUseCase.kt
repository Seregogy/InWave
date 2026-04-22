package com.inwave.domain.usecase.track.query

import com.inwave.domain.entity.Track
import java.lang.IllegalStateException

class GetTrackWithLyricsUseCase(
    private val trackUseCase: GetTrackUseCase,
    private val lyricsUseCase: GetTrackLyricsUseCase
) {
    suspend operator fun invoke(id: String): Result<Track> {
        val trackResult = trackUseCase(id).getOrNull()
            ?: return Result.failure(IllegalStateException("Track not found"))

        val lyricsResult = lyricsUseCase(id).getOrNull()
            ?: return Result.failure(IllegalStateException("Lyrics not found"))

        return Result.success(
            trackResult.copy(lyrics = lyricsResult)
        )
    }
}