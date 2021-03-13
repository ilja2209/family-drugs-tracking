package com.drugstracking.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drugstracking.entities.Drug
import com.drugstracking.entities.Person

class DrugsListSettingsModel: ViewModel() {
    val drugs = MutableLiveData<List<Drug>>()
    val person = MutableLiveData<Person>()

    fun init(person: Person, drugs: List<Drug>) {
        this.person.value = person
        this.drugs.value = drugs
    }

    fun add(drug: Drug) {
        if (person.value == null) {
            return
        }
        val drugList = ArrayList<Drug>(drugs.value.orEmpty())
        drugList.add(drug)
        drugs.value = drugList
    }

    fun replace(oldDrug: Drug, changedDrug: Drug) {
        if (person.value == null) {
            return
        }
        val drugList = drugs.value
            .orEmpty()
            .minus(oldDrug)
            .plus(changedDrug)
        drugs.value = drugList
    }

    fun delete(drug: Drug) {
        drugs.value = drugs.value.orEmpty().minus(drug)
    }

    fun get(position: Int): Drug {
        return drugs.value.orEmpty().get(position)
    }

    fun clear() {
        drugs.value = emptyList()
        person.value = null
    }
}