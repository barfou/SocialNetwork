package fr.barfou.socialnetwork.ui.activity

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.fragment.FilterFragment
import fr.barfou.socialnetwork.ui.fragment.ProfilFragment
import fr.barfou.socialnetwork.ui.listener.OnFilterChangeListener
import fr.barfou.socialnetwork.ui.listener.OnSearchValueChangeListener
import fr.barfou.socialnetwork.ui.utils.changeToolbarFont
import fr.barfou.socialnetwork.ui.utils.getDistanceFromLatLongInM
import fr.barfou.socialnetwork.ui.utils.hideKeyboard
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel

    enum class Mode { HOMEPAGE, FILTER, PROFILE, DETAILS, MODIFY_PROFILE, CREATE_MEETING, PREFERENCES }

    lateinit var searchItem: MenuItem
    lateinit var sortItem: MenuItem

    var mode: Mode by Delegates.observable(Mode.HOMEPAGE) { _, _, new ->
        notifyFragmentChange(new)
    }

    companion object {
        var userId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this, MainViewModel).get()
        setContentView(R.layout.activity_main)
        initToolBar()
        userId = intent.getStringExtra("userId") ?: ""
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.main_fragment_container).navigateUp()
    }

    private fun initToolBar() {
        setSupportActionBar(tool_bar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        tool_bar.setNavigationOnClickListener { onNavigateUp() }
        tool_bar.changeToolbarFont()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_main_activity, menu)

        val manager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchItem = menu.findItem(R.id.search_item)
        sortItem = menu.findItem(R.id.sort_item)
        val searchView = searchItem.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(this.componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                notifyFilterFragmentIfNeeded(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                main_root_layout.hideKeyboard()
                if (mode == Mode.HOMEPAGE) {
                    navigateToFilterFragment(query = query)
                }
                return true
            }
        })
        searchView.setOnCloseListener {
            /*if (mode == Mode.FILTER) {
                onBackPressed()
                mode = Mode.HOMEPAGE
            }*/
            return@setOnCloseListener false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /**
             * Handling toolbar submenu item click here
             */
            R.id.profil_item -> {
                findNavController(R.id.main_fragment_container).navigate(
                        R.id.action_to_profil_fragment,
                        bundleOf(ProfilFragment.USER_ID_KEY to userId)
                )
            }
            R.id.sort_item -> showDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToFilterFragment(filter: FilterFragment.FilterMode = FilterFragment.FilterMode.NONE, query: String = "") {
        findNavController(R.id.main_fragment_container).navigate(
                R.id.action_to_filter_fragment,
                bundleOf(
                        FilterFragment.SEARCH_VALUE_KEY to query,
                        FilterFragment.FILTER_VALUE_KEY to filter.toString()
                )
        )
        mode = Mode.FILTER
    }

    private fun getForegroundFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    private fun notifyFilterFragmentIfNeeded(newText: String) {
        if (mode == Mode.FILTER) {
            getForegroundFragment()?.run {
                try {
                    (this as OnSearchValueChangeListener).onSearchValueChange(newText)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun notifyFragmentChange(newValue: Mode) {
        when (newValue) {
            Mode.HOMEPAGE, Mode.FILTER -> showSearch()
            Mode.PROFILE, Mode.DETAILS, Mode.MODIFY_PROFILE, Mode.CREATE_MEETING, Mode.PREFERENCES -> hideSearch()
        }
    }

    private fun hideSearch() {
        try {
            searchItem.isVisible = false
            searchItem.isEnabled = false
            sortItem.isVisible = false
            sortItem.isEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSearch() {
        try {
            searchItem.isVisible = true
            searchItem.isEnabled = true
            sortItem.isVisible = true
            sortItem.isEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDialog() {

        lateinit var dialog: AlertDialog
        val listFilter = mutableListOf(resources.getString(R.string.date_event), resources.getString(R.string.proximity))
        if (mode == Mode.FILTER)
            listFilter.add(resources.getText(R.string.none).toString())

        var builder = AlertDialog.Builder(this, R.style.MyAlertDialogTheme)

        builder.setTitle(R.string.sort_by)

        builder.setSingleChoiceItems(listFilter.toTypedArray(), -1) { _, position ->
            when (position) {
                0 -> sortWith(FilterFragment.FilterMode.BY_DATE)
                1 -> sortWith(FilterFragment.FilterMode.BY_PROXIMITY)
                2 -> sortWith(FilterFragment.FilterMode.NONE)
            }
            dialog.cancel()
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun sortWith(filter: FilterFragment.FilterMode) {
        when (mode) {
            Mode.HOMEPAGE -> navigateToFilterFragment(filter = filter)
            Mode.FILTER -> {
                getForegroundFragment()?.run {
                    try {
                        (this as OnFilterChangeListener).onFilterChange(filter)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {}
        }
    }
}
