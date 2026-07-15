package com.inwave.backend.di

import com.inwave.backend.db.migration.migration1
import com.inwave.backend.db.migration.migration2
import com.inwave.backend.db.migration.migration3
import com.inwave.backend.db.table.ArtistGenreTable
import com.inwave.backend.db.table.ArtistLegacyTableId
import com.inwave.backend.db.table.ArtistReleaseTable
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ArtistTrackTable
import com.inwave.backend.db.table.ReleaseAdditionalDataTable
import com.inwave.backend.db.table.ReleaseGenreTable
import com.inwave.backend.db.table.ReleaseLegacyTableId
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.ReleaseTrackTable
import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackGenreTable
import com.inwave.backend.db.table.TrackLegacyTableId
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import com.inwave.backend.db.table.UserTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.koin.dsl.module

val databaseModule = module {
    single<Database> {
        get<Env>().let { env ->
            Database.connect(
                env.databaseUrl.also { println(it) },
                env.databaseDriver.also { println(it) },
                env.databaseUser.also { println(it) },
                env.databasePassword.also { println(it) }
            ).also {
                if (env.isDatabaseInitRequired) {
                    DbInitializer(it).apply {
                        configureDb()
                        showMigrations()
                    }
                }
            }
        }
    }
}

open class DbInitializer(
    protected val db: Database
) {
    open fun configureDb() {
        transaction(db) {
            SchemaUtils.create(UserTable)

            SchemaUtils.create(
                TrackTable, TrackMetadataTable, TrackStatisticsTable, TrackLyricsTable,
                TrackAdditionalDataTable, ReleaseTrackTable, TrackGenreTable
            )

            SchemaUtils.create(
                ReleaseTable, ReleaseStatisticsTable, ReleaseAdditionalDataTable, ReleaseGenreTable
            )

            SchemaUtils.create(
                ArtistTable, ArtistStatisticsTable, ArtistGenreTable, ArtistReleaseTable,
                ArtistTrackTable
            )
        }
    }

    open fun showMigrations() {
        transaction(db) {
            val tables = arrayOf(
                TrackTable, TrackMetadataTable, TrackStatisticsTable, TrackLyricsTable,
                TrackAdditionalDataTable, ReleaseTrackTable, TrackGenreTable, ReleaseTable,
                ReleaseStatisticsTable, ReleaseAdditionalDataTable, ReleaseGenreTable, ArtistTable,
                ArtistStatisticsTable, ArtistGenreTable, ArtistReleaseTable, ArtistTrackTable
            )

            MigrationUtils.statementsRequiredForDatabaseMigration(*tables).forEach { println(it) }
        }
    }
}

class DbInitializerMigration1(
    db: Database
): DbInitializer(db) {
    override fun configureDb() {
        super.configureDb()

        transaction(db) {
            SchemaUtils.create(
                TrackLegacyTableId, ReleaseLegacyTableId, ArtistLegacyTableId
            )
        }
    }

    override fun showMigrations() {
        val dbPath = "C:/Users/delhi/Desktop/kotlin/ktor-test-backend/src/files/database-main.db"
        val oldDb = Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")

        migration1(oldDb, db)

        super.showMigrations()

        transaction(db) {
            MigrationUtils.statementsRequiredForDatabaseMigration(
                TrackLegacyTableId, ReleaseLegacyTableId, ArtistLegacyTableId
            ).forEach { println(it) }
        }
    }
}

class DbInitializerMigration2(
    db: Database
): DbInitializer(db) {
    override fun showMigrations() {
        migration2(db)
        super.showMigrations()
    }
}

class DbInitializerMigration3(
    db: Database
): DbInitializer(db) {
    override fun showMigrations() {
        val dbPath = "C:/Users/delhi/Desktop/kotlin/ktor-test-backend/src/files/database-main.db"
        val oldDb = Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")

        migration3(oldDb, db)
        super.showMigrations()
    }
}