package com.inwave.domain.entity

data class User(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isAuthenticated: Boolean
) {

}