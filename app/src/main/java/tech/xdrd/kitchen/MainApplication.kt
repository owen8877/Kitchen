package tech.xdrd.kitchen

import android.app.Application
import io.realm.Realm


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Data.init()

//        if (BuildConfig.DEBUG)
//            Stetho.initialize(
//                Stetho.newInitializerBuilder(this)
//                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                    .build()
//            )
    }

    override fun onTerminate() {
        super.onTerminate()
        Data.close()
    }
}