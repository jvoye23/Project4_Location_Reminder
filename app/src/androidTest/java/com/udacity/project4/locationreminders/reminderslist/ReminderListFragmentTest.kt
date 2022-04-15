package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.MainCoroutineRule
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.EspressoIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repository: ReminderDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()



    @Before
    fun setup() = runBlocking{
        stopKoin()

        val appModule = module {
            viewModel {
                RemindersListViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get () as ReminderDataSource
                )
            }

            single<ReminderDataSource> {RemindersLocalRepository(get())}
            single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext())}
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(appModule))
        }
        repository = GlobalContext.get().koin.get()
        repository.deleteAllReminders()

    }

    @Test
    fun clickFabButton_navigateToSaveReminderFragment() {
        runBlockingTest {
            // Given this Fragment Scenario
            val scenario = launchFragmentInContainer<ReminderListFragment>(
                Bundle.EMPTY,
                R.style.AppTheme
            )
            val navController = mock(NavController::class.java)

            scenario.onFragment{
                Navigation.setViewNavController(it.view!!, navController)
            }

            // When clicking the FAB Button

            onView(withId(R.id.addReminderFAB)).perform(click())

            // Then navigate
            verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
            Thread.sleep(2000)
        }


    }

    @Test
    fun showRemindersOnScreen()  {
        runBlocking {

            // Given reminderData
            val reminderData = ReminderDTO(
                title = "Unit test the DAO",
                description = "test description",
                location = "test location",
                latitude = 53.58619662652105,
                longitude = 9.894304565290064,
                id = UUID.randomUUID().toString()
            )

            // When reminder is saved
            repository.saveReminder(reminderData)

            launchFragmentInContainer<ReminderListFragment>(
                Bundle.EMPTY,
                R.style.AppTheme
            )

            // Then
            onView(withText(reminderData.title)).check(ViewAssertions.matches(isDisplayed()))
            onView(withText(reminderData.description)).check(ViewAssertions.matches(isDisplayed()))
            onView(withText(reminderData.location)).check(ViewAssertions.matches(isDisplayed()))
        }

        Thread.sleep(2000)

    }
}


