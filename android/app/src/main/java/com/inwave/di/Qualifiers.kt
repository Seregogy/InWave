package com.inwave.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteLegacyRepo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRepo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRepo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockRepo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaletteCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LyricsCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ArtistCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TrackCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserRemoteRepo