package com.example.guitartuner.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import com.example.guitartuner.data.SettingsManager
import com.example.guitartuner.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    viewModel {
        SettingsViewModel(settingsManager = get())
    }

    single {
        SettingsManager(storage = get(), scope = get())
    }

    single {
        Satchel.with(
            storer = FileSatchelStorer(
                file = File(get<Application>().filesDir, "settings.storage")
            )
        )
    }


    single<CoroutineScope> {
        ProcessLifecycleOwner.get().lifecycleScope
    }
}
