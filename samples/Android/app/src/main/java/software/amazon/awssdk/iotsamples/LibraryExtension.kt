package software.amazon.awssdk.iotsamples

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log

class LibraryExtension : Application() {
    override fun onCreate() {
        super.onCreate()

        val applicationInfo = applicationInfo

        Log.e("software.amazon.awssdk.iotsamples", "DEBUG LOG STEVE LibraryExtension")
    }
}