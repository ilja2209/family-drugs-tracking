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
import com.drugstracking.entities.Person
import kotlinx.android.synthetic.main.new_person.*

class NewPersonFragment : Fragment() {

    private val settingsModel: DrugsListSettingsModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.new_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (settingsModel.person.value == null) {
            personName.setText("")
        } else {
            personName.setText(settingsModel.person.value.toString())
        }
        view.findViewById<Button>(R.id.next).setOnClickListener {
            settingsModel.person.value =
                Person(personName.text.toString())
            findNavController().navigate(R.id.action_newPersonFragment_to_drugsListSettingsFragment)
        }
    }
}