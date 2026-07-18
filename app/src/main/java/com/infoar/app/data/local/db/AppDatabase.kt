package com.infoar.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.infoar.app.data.local.dao.HistoryDao
import com.infoar.app.data.local.dao.PlaceDao
import com.infoar.app.data.local.entity.HistoryEntity
import com.infoar.app.data.local.entity.PlaceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [HistoryEntity::class, PlaceEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "infoar_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.placeDao())
                    }
                }
            }

            suspend fun populateDatabase(placeDao: PlaceDao) {
                val initialPlaces = listOf(
                    PlaceEntity(
                        name = "Monumento UTP",
                        description = "Un monumento emblemático en la entrada de la universidad.",
                        qrCode = "UTP_MON_01",
                        latitude = 9.020,
                        longitude = -79.530,
                        category = "Cultura",
                        imageUrl = "",
                        model3dAsset = "monumento.glb"
                    ),
                    PlaceEntity(
                        name = "Biblioteca Central",
                        description = "Espacio de estudio con miles de recursos bibliográficos.",
                        qrCode = "UTP_BIB_02",
                        latitude = 9.021,
                        longitude = -79.531,
                        category = "Educación",
                        imageUrl = "",
                        model3dAsset = "libro.glb"
                    ),
                    PlaceEntity(
                        name = "Laboratorio de IA",
                        description = "Equipamiento de última generación para investigación.",
                        qrCode = "UTP_LAB_03",
                        latitude = 9.022,
                        longitude = -79.532,
                        category = "Tecnología",
                        imageUrl = "",
                        model3dAsset = "robot.glb"
                    ),
                    PlaceEntity(
                        name = "Estatua Histórica",
                        description = "Representación del fundador de la institución.",
                        qrCode = "UTP_EST_04",
                        latitude = 9.023,
                        longitude = -79.533,
                        category = "Historia",
                        imageUrl = "",
                        model3dAsset = "estatua.glb"
                    ),
                    PlaceEntity(
                        name = "Edificio 3",
                        description = "Sede de la Facultad de Ingeniería de Sistemas Computacionales.",
                        qrCode = "UTP_ED3_05",
                        latitude = 9.024,
                        longitude = -79.534,
                        category = "Académico",
                        imageUrl = "",
                        model3dAsset = "edificio.glb"
                    )
                )
                placeDao.insertAll(initialPlaces)
            }
        }
    }
}
