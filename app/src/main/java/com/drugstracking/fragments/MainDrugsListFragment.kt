package com.drugstracking.fragments

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.drugstracking.R
import com.drugstracking.Tracker
import com.drugstracking.entities.Drug
import com.drugstracking.entities.Person
import com.drugstracking.models.DrugsListModel
import com.drugstracking.models.DrugsListSettingsModel
import kotlinx.android.synthetic.main.main_drugs_list.*
import kotlin.system.exitProcess


class MainDrugsListFragment : Fragment() {

    private val listModel: DrugsListModel by activityViewModels()
    private val listSettingsModel: DrugsListSettingsModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_drugs_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clearListViews()
        try {
            listModel.downloadSettings()
        } catch (e: Throwable) {
            listModel.load(requireContext().externalCacheDir)
        }
        listModel.personDrugs.value.orEmpty().entries.forEachIndexed { i, e ->
            addListView(
                view,
                e.key,
                e.value,
                i
            )
        }
        Tracker(listModel.personDrugs.value.orEmpty(), context).start()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_new_person_action -> {
            findNavController().navigate(R.id.action_mainDrugsListFragment_to_newPersonFragment)
            listSettingsModel.clear()
            true
        }
        R.id.remove_settings_file -> {
            listModel.remove(requireContext().externalCacheDir)
            listSettingsModel.clear()
            true
        }
        R.id.upload_settings -> {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(
                Manifest.permission.INTERNET
            ), 1)
            listModel.uploadSettings()
            true
        }
        R.id.download_settings -> {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            ), 1)
            listModel.downloadSettings()
            listModel.save(requireContext().externalCacheDir)
            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
            true
        }
        R.id.close_app -> {
            exitProcess(0)
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun clearListViews() {
        drugsGridLayout.removeAllViews()
    }

    private fun addListView(view: View, person: Person?, drugs: List<Drug>?, index: Int) {
        if (person == null) {
            return
        }
        val listView = ListView(context)
        val items = ArrayList<String>()
        items.add(person.name)
        items.addAll(drugs.orEmpty().map { i -> i.toString() })
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        val height = drugsGridLayout.measuredHeight / 3.0
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            650, //FIXME: TRY TO USE WEIGHT! Now it is not working correctly!
            1.0f
        )
        listView.layoutParams = param

        listView.setBackgroundColor(Color.parseColor("#F5F5DA"))

        val params = listView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = 15
        listView.layoutParams = ViewGroup.MarginLayoutParams(params)

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, _, _ ->
            listSettingsModel.init(person, drugs.orEmpty())
            findNavController().navigate(R.id.action_mainDrugsListFragment_to_drugsListSettingsFragment)
            true
        }
        drugsGridLayout.addView(listView)
    }
}