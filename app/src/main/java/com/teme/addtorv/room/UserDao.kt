package com.teme.addtorv.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.teme.addtorv.room.User

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)

    @Query("DELETE from user")
    fun deleteAll()

    @Query("DELETE FROM user WHERE first_name like :firstName AND last_name like :lastName")
    fun deleteUser(firstName: String, lastName: String)

}