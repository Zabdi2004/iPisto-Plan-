package com.finanzaspersonales.gt.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                    .also { INSTANCE = it }
            }
        }
    }
}
