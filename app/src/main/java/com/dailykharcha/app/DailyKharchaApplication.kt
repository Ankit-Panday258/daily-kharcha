package com.dailykharcha.app

import android.app.Application
import com.dailykharcha.app.data.local.ExpenseDatabase
import com.dailykharcha.app.data.repository.ExpenseRepository
import com.dailykharcha.app.data.repository.ExpenseRepositoryImpl

/**
 * Application class initializing offline database and repository dependencies.
 */
class DailyKharchaApplication : Application() {

    lateinit var database: ExpenseDatabase
        private set

    lateinit var expenseRepository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = ExpenseDatabase.getDatabase(this)
        expenseRepository = ExpenseRepositoryImpl(database.expenseDao())
    }
}
