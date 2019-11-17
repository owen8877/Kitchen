package tech.xdrd.kitchen

import android.app.Application
import io.realm.Realm


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Data.init()
    }

    override fun onTerminate() {
        super.onTerminate()
        Data.close()
    }
}