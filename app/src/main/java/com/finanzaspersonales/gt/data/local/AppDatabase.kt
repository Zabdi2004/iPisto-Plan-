package com.finanzaspersonales.gt.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.finanzaspersonales.gt.data.local.dao.*
import com.finanzaspersonales.gt.data.local.entity.*

@Database(
    entities = [
        User::class,
        Ingreso::class,
        GastoFijo::class,
        GastoVariable::class,
        Deuda::class,
        MetaAhorro::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userTypeDao(): UserTypeDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun gastoFijoDao(): GastoFijoDao
    abstract fun gastoVariableDao(): GastoVariableDao
    abstract fun deudaDao(): DeudaDao
    abstract fun metaAhorroDao(): MetaAhorroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "finanzas_personales_db"
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Ingreso ADD COLUMN fechaMillis INTEGER")
                db.execSQL("ALTER TABLE GastoFijo ADD COLUMN fechaMillis INTEGER")
                db.execSQL("ALTER TABLE GastoVariable ADD COLUMN fechaMillis INTEGER")
                db.execSQL("ALTER TABLE Deuda ADD COLUMN fechaMillis INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2).build()
                    .also { INSTANCE = it }
            }
        }
    }
}
