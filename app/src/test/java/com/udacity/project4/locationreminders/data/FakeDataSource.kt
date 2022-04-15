package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (private val reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError){
             return Result.Error("Error occured, reminders not found")
        } else {
            return Result.Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Error occured, reminders not found")
        } else {
            val reminder = reminders.find {it.id == id}

            if (reminder == null) {
                return Result.Error("Error occured, Reminder not found")
            } else{
                return Result.Success(reminder)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}