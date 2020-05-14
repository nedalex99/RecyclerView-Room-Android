package com.teme.addtorv.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun userDao(): UserDao

    companion object{
        private var mInstance : AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if(mInstance == null){
                mInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user"
                ).build()
            }
            return mInstance as AppDatabase
        }
    }

}