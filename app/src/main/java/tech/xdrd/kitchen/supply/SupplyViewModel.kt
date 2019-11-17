package tech.xdrd.kitchen.supply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import tech.xdrd.kitchen.Data
import tech.xdrd.kitchen.model.Ingredient
import tech.xdrd.kitchen.model.SupplyIngredient
import java.util.*

class SupplyViewModel : ViewModel() {
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
