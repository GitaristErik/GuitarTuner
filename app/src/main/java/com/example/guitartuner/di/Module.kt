package com.example.guitartuner.di

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.data.tuner.PermissionManager
import com.example.guitartuner.ui.MainActivity
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.tuner.TunerViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    viewModel {
        SettingsViewModel(settingsManager = get())
    }

    scope<MainActivity> {

        viewModel {
            TunerViewModel(permissionManager = get())
        }

        scoped {
            PermissionManager(
                activity = getSource<MainActivity>().let {
                    Log.e("MainActivity", "activity = $it")
                    it!!
                }
            )
        }
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
