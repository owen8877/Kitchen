package tech.xdrd.kitchen.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import tech.xdrd.kitchen.Data
import tech.xdrd.kitchen.model.Ingredient.Unit
import tech.xdrd.kitchen.model.StorageIngredient
import java.text.SimpleDateFormat
import java.util.*

class StorageViewModel : ViewModel() {
    private val _storageList = Data.MRCollection(Data.fetchStorageList())
    val storageList: LiveData<List<StorageIngredient>>
        get() = _storageList.innerState

    class IngredientModel(val mode: IngredientDialog.Mode) {
        private lateinit var ref: StorageIngredient
        var name = ""
        var quantity = 0.0
        var unit = -1

        private val _valid = MutableLiveData<Boolean>(false)
        val valid: LiveData<Boolean> = _valid

        private fun isInputValid() = name.isNotBlank() && quantity >= 0.0 && unit >= 0

        private fun toStorageIngredient() = StorageIngredient(name, quantity, Unit.values()[unit])

        fun adapt(item: StorageIngredient) {
            ref = item
            name = item.name
            quantity = item.quantity
            unit = item.unit.ordinal

            _valid.apply { value = true }
        }

        fun add() = Data.execute(Realm.Transaction { realm ->
            val storageIngredient = toStorageIngredient()
            realm.insert(storageIngredient)
            storageIngredient.addRecord(Date(), observation = true)
        })

        fun delete() = Data.execute(Realm.Transaction {
            ref.records.deleteAllFromRealm()
            ref.deleteFromRealm()
        })

        fun getHistory() = ref.records.map { record ->
            val FORMAT = SimpleDateFormat("EEE, MMM dd")
            "(${if (record.observation) "Obs" else "Pre"}) ${FORMAT.format(record.date)} - ${record.quantity}"
        }.reduce { acc, s -> acc + '\n' + s }

        fun refresh() = _valid.apply { value = isInputValid() }

        fun update() = Data.execute(Realm.Transaction {
            ref.name = name
            ref.quantity = quantity
            ref.unit = Unit.values()[unit]
            ref.addRecord(Date(), observation = true)
        })
    }
}