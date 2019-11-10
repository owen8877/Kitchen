package tech.xdrd.kitchen.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import tech.xdrd.kitchen.Data
import tech.xdrd.kitchen.model.Ingredient.Unit
import tech.xdrd.kitchen.model.StorageIngredient

class StorageViewModel : ViewModel() {
    private val _storageList = Data.MRCollection(Data.fetchStorageList())
    val storageList: LiveData<List<StorageIngredient>>
        get() = _storageList.innerState

    class IngredientModel(val mode: IngredientDialog.Mode) {
        private var ref = StorageIngredient()
        var name = ""
        var quantity = 0.0
        var unit = -1

        private val _valid = MutableLiveData<Boolean>(false)
        val valid: LiveData<Boolean> = _valid

        private fun isInputValid(): Boolean {
            return name.isNotBlank() && quantity >= 0.0 && unit >= 0
        }

        private fun toIngredientItem(): StorageIngredient {
            return StorageIngredient(name, quantity, Unit.values()[unit])
        }

        fun adapt(item: StorageIngredient) {
            ref = item
            name = item.name
            quantity = item.quantity
            unit = item.unit.ordinal
            _valid.apply { value = true }
        }

        fun add() {
            Data.execute(Realm.Transaction { realm -> run { realm.insert(toIngredientItem()) } })
        }

        fun delete() {
            Data.execute(Realm.Transaction { ref.deleteFromRealm() })
        }

        fun refresh() {
            _valid.apply { value = isInputValid() }
        }

        fun update() {
            Data.execute(Realm.Transaction {
                run {
                    ref.name = name
                    ref.quantity = quantity
                    ref.unit = Unit.values()[unit]
                }
            })
        }
    }
}