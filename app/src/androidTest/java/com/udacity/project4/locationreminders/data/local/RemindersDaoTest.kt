package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt - Done

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    @Before
    fun initializeDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        dao = database.reminderDao()
    }

    @After
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun insertReminderIntoDatabase() = runBlockingTest {

        // Given reminderData
        val reminderData = ReminderDTO(
            title = "Unit test the DAO",
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064,
            id = UUID.randomUUID().toString()
        )

        // When reminderData is inserted into Database
        dao.saveReminder(reminderData)

        // Then database has size and contains the given reminderData
        assertThat(dao.getReminders(), Matchers.hasSize(1))
        assertThat(dao.getReminders(), Matchers.contains(reminderData))


    }

    @Test
    fun insertReminderIntoDatabase_getReminderById() = runBlockingTest {

        // Given reminderData
        val reminderData = ReminderDTO(
            title = "Unit test the DAO",
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064,
            id = UUID.randomUUID().toString()
        )
        dao.saveReminder(reminderData)

        // When getting reminder by id
        val loadedReminder = dao.getReminderById(reminderData.id)

        // Then loadedReminder has correct data
        assertThat(loadedReminder as ReminderDTO, notNullValue())
        assertThat(loadedReminder.id, Matchers.`is`(reminderData.id))
        assertThat(loadedReminder.description, Matchers.`is`(reminderData.description))
        assertThat(loadedReminder.location, Matchers.`is`(reminderData.location))
        assertThat(loadedReminder.longitude, Matchers.`is`(reminderData.longitude))
        assertThat(loadedReminder.id, Matchers.`is`(reminderData.id))
    }

    @Test
    fun deleteAllRemindersFromDatabase() = runBlockingTest {

        // Given reminderData is inserted into Database
        val reminderData = ReminderDTO(
            title = "Unit test the DAO",
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064,
            id = UUID.randomUUID().toString()
        )
        dao.saveReminder(reminderData)
        assertThat(dao.getReminders(), Matchers.hasSize(1))

        // When all reminders are deleted
        dao.deleteAllReminders()

        // Then database is empty
        assertThat(dao.getReminders(), Matchers.empty())
    }



}