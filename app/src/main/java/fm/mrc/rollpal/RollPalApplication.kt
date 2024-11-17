package fm.mrc.rollpal

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class RollPalApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        try {
            WorkManager.initialize(this, workManagerConfiguration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
} 