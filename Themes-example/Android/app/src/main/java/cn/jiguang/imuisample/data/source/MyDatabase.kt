package cn.jiguang.imuisample.data.source

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import cn.jiguang.imuisample.data.MyMessage

@Database(entities = arrayOf(MyMessage::class), version = 3, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        private val sLock = Any()
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            if (null == INSTANCE) {
                synchronized(sLock) {
                    if (null == INSTANCE) {
                        INSTANCE = Room
                                .databaseBuilder<MyDatabase>(context.applicationContext, MyDatabase::class.java, "sample.db")
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}