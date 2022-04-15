package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.content.pm.ApplicationInfoBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    val instantTaskExecRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun initializeTest() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(fakeDataSource)
    }

    @Test
    fun saveReminderDataItem_showLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        // Given ReminderDateItem
        val reminderDataItem = ReminderDataItem(
            title = "unit test",
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064,
            id = UUID.randomUUID().toString()
        )

        // When given ReminderDataItem is saved
        saveReminderViewModel.saveReminder(reminderDataItem)

        // Then
        assertThat(saveReminderViewModel.showLoading.value, Matchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.value, Matchers.`is`(false))

    }

    @Test
    fun validateEnteredData_showErrorSnackbar(){

        // Given invalid ReminderDateItem with Title = null
        val reminderDataItem = ReminderDataItem(
            title = null,
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064,
            id = UUID.randomUUID().toString()
        )

        // When trying to save invalid ReminderDataItem
        mainCoroutineRule.runBlockingTest {
            saveReminderViewModel.validateEnteredData(reminderDataItem)
        }

        // Then
        assertThat(saveReminderViewModel.showSnackBarInt.value, Matchers.`is`(R.string.err_enter_title))
    }

}