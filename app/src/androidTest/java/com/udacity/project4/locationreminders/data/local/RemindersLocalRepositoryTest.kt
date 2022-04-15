package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt - Done

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    //private val testDispatcher = TestCoroutineDispatcher()
    //private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun initializeDatabaseAndRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun saveReminder() =
        mainCoroutineRule.runBlockingTest {
            // Given reminderData
            val reminderData = ReminderDTO(
                title = "Unit test the DAO",
                description = "test description",
                location = "test location",
                latitude = 53.58619662652105,
                longitude = 9.894304565290064,
                id = UUID.randomUUID().toString()
            )

            // When reminderData is saved to repository
            repository.saveReminder(reminderData)
            val result = repository.getReminders()

            result as Result.Success

            // Then repository data is not empty and has size
            assertThat(result.data, Matchers.notNullValue())
            assertThat(result.data, Matchers.hasSize(1))
        }



    @Test
    fun saveReminder_getReminderByIdSucceeds() = mainCoroutineRule.runBlockingTest{

            // Given reminderData
            val reminderData = ReminderDTO(
                title = "Unit test the DAO",
                description = "test description",
                location = "test location",
                latitude = 53.58619662652105,
                longitude = 9.894304565290064,
                id = UUID.randomUUID().toString()
            )

            // When reminderData is saved to repository returned by id
            repository.saveReminder(reminderData)
            val result = repository.getReminder(reminderData.id)

            result as Result.Success

            // Then
            assertThat(result.data, Matchers.notNullValue())
            assertThat(result.data.title, Matchers.`is`(reminderData.title))
            assertThat(result.data.description, Matchers.`is`(reminderData.description))
            assertThat(result.data.location, Matchers.`is`(reminderData.location))
            assertThat(result.data.latitude, Matchers.`is`(reminderData.latitude))
            assertThat(result.data.longitude, Matchers.`is`(reminderData.longitude))
            assertThat(result.data.id, Matchers.`is`(reminderData.id))

    }

    @Test
    fun getReminderByIdFails() = mainCoroutineRule.runBlockingTest{


            // Given reminderData
            val reminderData = ReminderDTO(
                title = "Unit test DAO",
                description = "test description",
                location = "test location",
                latitude = 53.58619662652105,
                longitude = 9.894304565290064,
                id = UUID.randomUUID().toString()
            )

            // When result has error
            val result = repository.getReminder(reminderData.id)
            result as Result.Error

            // Then
            assertThat(result.message, Matchers.`is`("Reminder not found!"))
            assertThat(result.statusCode, Matchers.nullValue())


    }

    @Test
    fun deleteRemindersSucceeds() = mainCoroutineRule.runBlockingTest{

            // Given reminderData
            val reminderData = ReminderDTO(
                title = "Unit test the DAO",
                description = "test description",
                location = "test location",
                latitude = 53.58619662652105,
                longitude = 9.894304565290064,
                id = UUID.randomUUID().toString()
            )

            // When all reminders are deleted
            repository.deleteAllReminders()

            // Then the repository data is empty
            val result = repository.getReminders()

            result as Result.Success
            assertThat(result.data, Matchers.empty())

    }
}
