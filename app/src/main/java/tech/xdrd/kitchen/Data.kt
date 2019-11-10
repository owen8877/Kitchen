package tech.xdrd.kitchen

import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import tech.xdrd.kitchen.model.Plan
import tech.xdrd.kitchen.model.StorageIngredient


object Data {
    private val DATABASE_FILE = "database.realm"
    private val SCHEMA_VERSION: Long = 1

    private lateinit var realm: Realm

    fun init() {
        val config = RealmConfiguration.Builder()
            .name(DATABASE_FILE)
            .schemaVersion(SCHEMA_VERSION)
//            .migration { r, old, new -> {
//                // See https://realm.io/docs/java/latest#local-migrations for detailed migration instructions.
//                when {
//                    old <= 1 -> {}
//                }
//            }}
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(config)
    }

    fun close() {
        realm.close()
    }

    fun execute(t: Realm.Transaction) {
        realm.executeTransaction(t)
    }

    fun fetchStorageList(): RealmResults<StorageIngredient> {
        return realm.where(StorageIngredient::class.java).findAll()
    }

    fun fetchPlanList(): RealmResults<Plan> {
        return realm.where(Plan::class.java).findAll()
    }

    class MRCollection<T>(private val source: RealmResults<T>) {
        val innerState = MutableLiveData<List<T>>().apply { value = source }

        init {
            source.addChangeListener { new -> innerState.apply { value = new } }
        }
    }
}
