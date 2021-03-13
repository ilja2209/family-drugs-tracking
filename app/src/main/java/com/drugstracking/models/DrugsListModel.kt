package com.drugstracking.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drugstracking.entities.Drug
import com.drugstracking.entities.Person
import com.drugstracking.entities.SerializablePerson
import com.drugstracking.helpers.DrugsServerHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.RuntimeException

class DrugsListModel : ViewModel() {
    private val fileName = "settings.json"
    val personDrugs = MutableLiveData<Map<Person, List<Drug>>>()
    val drugsServerHelper = DrugsServerHelper("192.168.88.71", "8080")

    fun add(person: Person?, drugs: List<Drug>?) {
        val pDrugs = HashMap(this.personDrugs.value.orEmpty())
        if (pDrugs.containsKey(person)) {
            pDrugs.remove(person)
        }
        pDrugs[person] = drugs
        this.personDrugs.value = pDrugs
    }

    fun clear() {
        personDrugs.value = null
    }

    fun save(dir: File?) {
        if (personDrugs.value == null) {
            return
        }

        val f = File(dir, fileName)
        if (f.exists() && !f.delete()) {
            throw RuntimeException("Can't remove file")
        }

        if (!f.createNewFile()) {
            throw RuntimeException("Cant create a new file")
        }

        val persons =
            personDrugs.value!!.entries.map { e -> SerializablePerson(e.key.name, e.value) }
        val gson = Gson()
        f.writeText(gson.toJson(persons))
    }

    fun load(dir: File?) {
        if (dir == null || !dir.exists()) {
            return
        }

        val f = File(dir, fileName)
        if (!f.exists()) {
            return
        }

        val json = f.readLines().joinToString(separator = "")
        val gson = Gson()
        val type = object : TypeToken<List<SerializablePerson>>() {}.type
        val persons = gson.fromJson<List<SerializablePerson>>(json, type)
        val map: HashMap<Person, List<Drug>> = HashMap()
        persons.forEach { map[Person(it.personName)] = it.drugs.orEmpty() }
        personDrugs.value = map
    }

    fun remove(dir: File?) {
        if (dir == null || !dir.exists()) {
            return
        }

        val f = File(dir, fileName)
        if (f.exists()) {
            f.delete()
            personDrugs.value = null
        }
    }

    fun downloadSettings() {
        runBlocking {
            val people = drugsServerHelper.loadSettings()
            val map: HashMap<Person, List<Drug>> = HashMap()
            people.forEach { map[Person(it.personName)] = it.drugs.orEmpty() }
            personDrugs.value = map
        }
    }

    fun uploadSettings() {
        if (personDrugs.value == null) {
            return
        }
        val people =
            personDrugs.value!!.entries.map { e -> SerializablePerson(e.key.name, e.value) }
        runBlocking {
            drugsServerHelper.saveSettings(people)
        }
    }
}