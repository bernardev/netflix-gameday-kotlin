package com.example.netflix

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Filme::class, Perfil::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filmeDAO(): FilmeDAO

    abstract fun perfilDAO(): PerfilDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val tempInstance = INSTANCE

            if (tempInstance != null) {
                // o banco ja existe
                return tempInstance
            } else {
                // cria o banco uma unica vez
                synchronized(this) {

                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "netflix_database"
                    ).build()

                    INSTANCE = instance
                    return instance

                }
            }
        }

    }

}
