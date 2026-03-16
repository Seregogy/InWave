package com.inwave.backend.data.repository

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun <T, R>T.catchingTransaction(
    db: Database,
    transaction: context(Transaction) T.() -> R
): Result<R> {
    return runCatching {
        transaction(db) {
            transaction()
        }
    }
}

suspend fun <T, R>T.suspendCatchingTransaction(
    db: Database,
    transaction: context(Transaction) T.() -> R
): Result<R> {
    return runCatching {
        suspendTransaction(db) {
            transaction()
        }
    }
}