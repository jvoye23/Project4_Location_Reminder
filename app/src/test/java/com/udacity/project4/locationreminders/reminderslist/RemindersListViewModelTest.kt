package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.Q])
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initializeTest() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun emptyList_showNoData() = runBlockingTest {

        // Given empty FakeDataSource
        fakeDataSource.deleteAllReminders()

        // When reminders are loaded
        remindersListViewModel.loadReminders()

        // Then No Data is shown
        assertThat(remindersListViewModel.showNoData.value, CoreMatchers.`is`(true))
    }

    @Test
    fun addNewReminder_showLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        // Given new reminder
        val newReminder = ReminderDTO(
            title = "unit test",
            description = "test description",
            location = "test location",
            latitude = 53.58619662652105,
            longitude = 9.894304565290064
        )

        // When saving new reminder
        fakeDataSource.saveReminder(newReminder)
        remindersListViewModel.loadReminders()

        // Then
        assertThat(remindersListViewModel.showLoading.value, CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun error_showSnackbar() = runBlockingTest {

        // Given an error
        fakeDataSource.setShouldReturnError(true)

        // When loading reminders
        remindersListViewModel.loadReminders()

        // Then
        assertThat(remindersListViewModel.showSnackBar.value.toString(), CoreMatchers.`is`("Error occured, reminders not found"))
    }
}