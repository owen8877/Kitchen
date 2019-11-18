package tech.xdrd.kitchen.supply

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmList
import tech.xdrd.kitchen.Data
import tech.xdrd.kitchen.model.Ingredient
import tech.xdrd.kitchen.model.SupplyIngredient
import tech.xdrd.kitchen.model.SupplyIngredientSnapshot
import tech.xdrd.kitchen.model.SupplySnapshot
import java.util.*

class SupplyViewModel : ViewModel() {
    fun addSupplyIngredientToStorage() {
        // Create snapshot
        val list = Data.fetchSupplyList()
        val copiedList = list.map { SupplyIngredientSnapshot(it) }
        val snapshot = SupplySnapshot(
            Date(),
            RealmList<SupplyIngredientSnapshot>().also { it.addAll(copiedList) })
        Data.execute(Realm.Transaction { realm -> realm.insert(snapshot) })

        // Create bought list
        val boughtList = list.where().equalTo("bought", true).findAll()

        // Add/Update bought list to storage
        val storageMap = Data.fetchStorageList().map { it.name to it }.toMap()
        Data.execute(Realm.Transaction { realm ->
            for (ingredient in boughtList) {
                val storageIngredient = storageMap[ingredient.name]
                if (storageIngredient == null) {
                    // Name of the ingredient doesn't exists, should first convert the
                    // supplyIngredient to storageIngredient
                    val newStorageIngredient = ingredient.toStorageIngredient()
                    newStorageIngredient.addRecord(Date(), true)
                    realm.insert(newStorageIngredient)
                } else {
                    // Name of the ingredient exists, update the quantity
                    storageIngredient.quantity += ingredient.quantity
                    storageIngredient.addRecord(Date(), true)
                }
            }
        })

        // Delete bought and one-time items
        Data.execute(Realm.Transaction {
            boughtList.where().equalTo("recurrent", false).findAll().forEach {
                Log.d("delete", it.toString())
                it.deleteFromRealm()
            }
        })

        // Revert recurrent items to unbought
        Data.execute(Realm.Transaction {
            boughtList.where().equalTo("recurrent", true).findAll()
                .forEach { it.bought = !it.bought }
        })
    }

    private val _supplyList = Data.MRCollection(Data.fetchSupplyList())
    val supplyList: LiveData<List<SupplyIngredient>>
        get() = _supplyList.innerState

    class SupplyIngredientModel(val mode: SupplyIngredientDialog.Mode) {
        private lateinit var ref: SupplyIngredient
        var name = ""
        var quantity = 0.0
        var unit = -1
        var recurrent = false

        private val _valid = MutableLiveData<Boolean>(false)
        val valid: LiveData<Boolean> = _valid

        private fun isInputValid(): Boolean {
            return name.isNotBlank() && quantity >= 0.0 && unit >= 0
        }

        private fun toSupplyIngredient(): SupplyIngredient {
            return SupplyIngredient(
                name,
                quantity,
                Ingredient.Unit.values()[unit],
                recurrent,
                Date()
            );
        }

        fun adapt(item: SupplyIngredient) {
            ref = item
            name = item.name
            quantity = item.quantity
            unit = item.unit.ordinal
            recurrent = item.recurrent
            _valid.apply { value = true }
        }

        fun add() {
            Data.execute(Realm.Transaction { realm ->
                run {
                    val storageIngredient = toSupplyIngredient()
                    realm.insert(storageIngredient)
                }
            })
        }

        fun delete() {
            Data.execute(Realm.Transaction {
                ref.deleteFromRealm()
            })
        }

        fun refresh() {
            _valid.apply { value = isInputValid() }
        }

        fun update() {
            Data.execute(Realm.Transaction {
                ref.name = name
                ref.quantity = quantity
                ref.unit = Ingredient.Unit.values()[unit]
                ref.recurrent = recurrent
            })
        }
    }
}
