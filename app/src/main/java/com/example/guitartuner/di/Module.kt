package com.example.guitartuner.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import com.example.guitartuner.data.db.AppDatabase
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.data.tuner.PermissionManagerImpl
import com.example.guitartuner.data.tuner.PitchGenerationRepositoryImpl
import com.example.guitartuner.data.tuner.PitchRepositoryImpl
import com.example.guitartuner.data.tuner.TunerRepositoryImpl
import com.example.guitartuner.data.tuner.TuningSetsRepositoryImpl
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.domain.repository.tuner.PitchGenerationRepository
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import com.example.guitartuner.domain.repository.tuner.TunerRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.ui.MainActivity
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.tuner.TunerViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {

    viewModel {
        SettingsViewModel(
            settingsManager = get(), tuningsRepository = get()
        )
    }

    scope<MainActivity> {
        viewModel {
            TunerViewModel(
                tunerRepository = get(),
                permissionManager = get(),
                pitchGenerationRepository = get(),
                tuningSetsRepository = get(),
                settingsManager = get()
            )
        }

        scoped<TunerRepository> {
            TunerRepositoryImpl(
                settingsManager = get(),
                permissionManager = get(),
                pitchRepository = get(),
            )
        }

        scoped<PermissionManager> {
            PermissionManagerImpl(
                activity = getSource<MainActivity>()!!
            )
        }
    }

    single<PitchGenerationRepository> {
        PitchGenerationRepositoryImpl(
            tuningSetsRepository = get(),
        )
    }

    single<TuningSetsRepository> {
        TuningSetsRepositoryImpl(get(), get(), get(), get())
    }

    single<SettingsManager> {
        SettingsManager(storage = get(), scope = get())
    }

    single {
        Satchel.with(
            storer = FileSatchelStorer(
                file = File(get<Application>().filesDir, "settings.storage")
            )
        )
    }

    single<PitchRepository> {
        PitchRepositoryImpl(
            coroutineScope = get(),
            settingsManager = get(),
            database = get()
        )
    }

    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        ).fallbackToDestructiveMigration()
            //.setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    single<CoroutineScope> {
        ProcessLifecycleOwner.get().lifecycleScope
    }
}
