package com.drugstracking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.drugstracking.models.DrugsListSettingsModel
import com.drugstracking.R
import com.drugstracking.entities.Drug
import com.drugstracking.models.DrugSettingModel
import kotlinx.android.synthetic.main.new_drug_settings.*

class NewDrugSettingsFragment : Fragment() {

    private val settingsModel: DrugsListSettingsModel by activityViewModels()
    private val drugSettingsModel: DrugSettingModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.new_drug_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (drugSettingsModel.drug.value != null) {
            val drug = drugSettingsModel.drug.value!!
            drugNameText.setText(drug.name)
            timeIntervalText.setText(drug.time)
            commentText.setText(drug.comment)
        }

        view.findViewById<Button>(R.id.applyNewDrugSettings).setOnClickListener {
            findNavController().navigate(R.id.action_newDrugSettingsFragment_to_drugsListSettingsFragment)
            addNewDrug()
        }
    }

    fun addNewDrug() {
        val drug = Drug(drugNameText.text.toString(), timeIntervalText.text.toString(), commentText.text.toString())
        if (drugSettingsModel.drug.value != null) {
            val oldDrug = drugSettingsModel.drug.value!!
            settingsModel.delete(oldDrug)
        }
        settingsModel.add(drug)
    }
}