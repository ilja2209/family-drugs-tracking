package com.drugstracking.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drugstracking.entities.Drug

class DrugSettingModel: ViewModel() {
    val drug = MutableLiveData<Drug>()
}