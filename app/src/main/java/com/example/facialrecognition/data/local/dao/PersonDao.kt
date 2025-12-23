package com.example.facialrecognition.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facialrecognition.data.local.entity.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeople(): Flow<List<Person>>

    @Query("SELECT * FROM people")
    suspend fun getAllPeopleList(): List<Person>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Long): Person?

    @Query("SELECT COUNT(*) FROM people")
    fun getPeopleCount(): Flow<Int>
}
