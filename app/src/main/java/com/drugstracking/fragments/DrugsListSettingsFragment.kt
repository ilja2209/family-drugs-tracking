package com.drugstracking.fragments

import android.Manifest.permission.*
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.drugstracking.models.DrugsListModel
import com.drugstracking.models.DrugsListSettingsModel
import com.drugstracking.R
import com.drugstracking.models.DrugSettingModel
import kotlinx.android.synthetic.main.drugs_list_settings.*

class DrugsListSettingsFragment : Fragment() {

    private var privateView: View? = null
    private val settingsModel: DrugsListSettingsModel by activityViewModels()
    private val listModel: DrugsListModel by activityViewModels()
    private val drugSettingModel: DrugSettingModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.drugs_list_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        privateView = view
        view.findViewById<Button>(R.id.addNewDrug).setOnClickListener {
            drugSettingModel.drug.value = null
            findNavController().navigate(R.id.action_drugsListSettingsFragment_to_newDrugSettingsFragment)
        }

        view.findViewById<Button>(R.id.applySettings).setOnClickListener {
            listModel.add(settingsModel.person.value, settingsModel.drugs.value)
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE),
                1
            )
            listModel.save(requireContext().externalCacheDir)
            try {
                listModel.uploadSettings()
            } catch (e: Throwable) {
                // Add flag that there was not connection and synchronize changed settings with server in the future
            }
            findNavController().navigate(R.id.action_drugsListSettingsFragment_to_mainDrugsListFragment)
        }

        val drugs: List<String> =
            settingsModel.drugs.value.orEmpty().map { item -> item.toString() }
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter((privateView as View).context, android.R.layout.simple_list_item_1, drugs)
        new_drugs_list.adapter = arrayAdapter

        new_drugs_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            val drug = settingsModel.get(position)
            drugSettingModel.drug.value = drug
            findNavController().navigate(R.id.action_drugsListSettingsFragment_to_newDrugSettingsFragment)
        }

        new_drugs_list.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                val drug = settingsModel.get(position)
                viewDialog(view,
                    "Confirmation",
                    "Are you sure that you want to delete the drug ${drug.name} for time ${drug.time}?",
                    DialogInterface.OnClickListener { _, _ ->
                        settingsModel.delete(drug)
                        findNavController().navigate(R.id.action_drugsListSettingsFragment_self)
                    })
                true
            }
    }

    fun viewDialog(
        view: View,
        title: String,
        text: String,
        listener: DialogInterface.OnClickListener?
    ) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(text)
        alertDialogBuilder.setPositiveButton("Yes", listener)
        alertDialogBuilder.setNegativeButton("No", null)
        alertDialogBuilder.create().show()
    }
}