package tech.xdrd.kitchen.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmList
import tech.xdrd.kitchen.Data
import tech.xdrd.kitchen.Util
import tech.xdrd.kitchen.model.Dish
import tech.xdrd.kitchen.model.Plan
import tech.xdrd.kitchen.model.Plan.Type.values
import java.util.*

class PlanViewModel : ViewModel() {
    private var _planList = Data.MRCollection(Data.fetchPlanList())
    val planCollectionList: LiveData<List<Plan>>
        get() = _planList.innerState

    class PlanModel(var mode: PlanDialog.Mode) {
        private var ref = Plan()
        var date: Date = Util.getToday()
        var type: Int = -1
        var content: String = ""

        private val _valid = MutableLiveData<Boolean>(false)
        val valid: LiveData<Boolean> = _valid

        fun adapt(item: Plan) {
            ref = item
            date = item.date
            type = item.type.ordinal
            content = item.getDescription()

            _valid.apply { value = true }
        }

        private fun toPlanItem(): Plan {
            val list = content.split('\n')
                .map(String::trim)
                .filter(String::isNotEmpty)
                .map(::Dish)
            val intermediate = RealmList<Dish>()
            intermediate.addAll(list)
            return Plan(date, values()[type], intermediate)
        }

        fun add() {
            Data.execute(Realm.Transaction { realm ->
                run {
                    realm.insertOrUpdate(toPlanItem())
                }
            })
        }

        fun delete() {
            Data.execute(Realm.Transaction { ref.deleteFromRealm() })
        }

        fun refresh() {
            _valid.apply { value = isInputValid() }
        }

        private fun isInputValid(): Boolean {
            return type in values().indices && content.isNotBlank()
        }

        fun update() {
            Data.execute(Realm.Transaction {
                run {
                    val item = toPlanItem()
                    ref.date = item.date
                    ref.type = item.type
                    ref.dishes.removeAll { true }
                    ref.dishes.addAll(item.dishes)
                }
            })
        }
    }
}
