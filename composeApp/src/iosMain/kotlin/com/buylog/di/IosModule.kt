package com.buylog.di

import com.buylog.data.db.AppDatabase
import com.buylog.data.db.createAppDatabase
import org.koin.dsl.module

val iosModule = module {
    includes(sharedModule)
    single<AppDatabase> { createAppDatabase() }
}
