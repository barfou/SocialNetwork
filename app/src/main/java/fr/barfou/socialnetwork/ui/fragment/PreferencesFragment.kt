package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_preferences.*

class PreferencesFragment: Fragment() {

    lateinit var user: User
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_preferences)
            this.setDisplayHomeAsUpEnabled(true)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.PREFERENCES
        }

        val listTheme = mutableListOf<String>()
        listTheme.add("SPORT")
        listTheme.add("CULTURE")
        val aa= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listTheme)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPromote!!.adapter = aa

        user = mainViewModel.currentUser!!

        swTrend.isChecked = user.boolTrend
        swLocation.isChecked = user.boolLocation
        spPromote.setSelection(user.promote.toInt())

        swTrend.setOnCheckedChangeListener { _, isChecked ->
            try {
                mainViewModel.updateBoolTrendUser(user.firebaseId, isChecked)
            } catch (e: Exception) {
                print(e.toString())
            }

        }

        swLocation.setOnCheckedChangeListener { _, isChecked ->
            try {
                mainViewModel.updateBoolLocationUser(user.firebaseId, isChecked)
            } catch (e: Exception) {
                print(e.toString())
            }
        }

        spPromote?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                try {
                    mainViewModel.updatePromoteUser(user.firebaseId, position.toString())
                } catch (e: Exception) {
                    print(e.toString())
                }
            }
        }
    }
}