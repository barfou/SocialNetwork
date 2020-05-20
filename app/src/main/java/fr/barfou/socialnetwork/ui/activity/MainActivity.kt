package fr.barfou.socialnetwork.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.fragment.FilterFragment
import fr.barfou.socialnetwork.ui.fragment.ProfilFragment
import fr.barfou.socialnetwork.ui.listener.OnFilterChangeListener
import fr.barfou.socialnetwork.ui.listener.OnLocationResult
import fr.barfou.socialnetwork.ui.listener.OnSearchValueChangeListener
import fr.barfou.socialnetwork.ui.utils.changeToolbarFont
import fr.barfou.socialnetwork.ui.utils.getDistanceFromLatLongInM
import fr.barfou.socialnetwork.ui.utils.hideKeyboard
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    //Location
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    lateinit var mainViewModel: MainViewModel

    enum class Mode { HOMEPAGE, FILTER, PROFILE, DETAILS, MODIFY_PROFILE, CREATE_MEETING, PREFERENCES, DISPLAY_LIST }

    lateinit var searchItem: MenuItem
    lateinit var sortItem: MenuItem
    lateinit var profileItem: MenuItem

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.main_fragment_container).navigateUp()
    }

    private fun initToolBar() {
        setSupportActionBar(tool_bar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        tool_bar.setNavigationOnClickListener { onNavigateUp() }
        tool_bar.changeToolbarFont()
        tool_bar.setNavigationOnClickListener {
            closeSearch()
            findNavController(R.id.main_fragment_container).popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_main_activity, menu)

        val manager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchItem = menu.findItem(R.id.search_item)
        sortItem = menu.findItem(R.id.sort_item)
        profileItem = menu.findItem(R.id.profil_item)
        val searchView = searchItem.actionView as SearchView
        searchView.setIconifiedByDefault(true)

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
            Mode.HOMEPAGE, Mode.FILTER -> {
                showSearch()
                showProfileIcon()
            }
            Mode.DETAILS, Mode.CREATE_MEETING -> {
                hideSearch()
                showProfileIcon()
            }
            Mode.PROFILE, Mode.MODIFY_PROFILE, Mode.PREFERENCES, Mode.DISPLAY_LIST -> {
                hideSearch()
                hideProfileIcon()
            }
        }
    }

    private fun hideProfileIcon() {
        try {
            profileItem.isVisible = false
            profileItem.isEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProfileIcon() {
        try {
            profileItem.isVisible = true
            profileItem.isEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
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
        var selectedPosition = -1
        if (mode == Mode.FILTER) {
            listFilter.add(resources.getText(R.string.none).toString())
            try {
                val fragment = getForegroundFragment() as FilterFragment
                selectedPosition = when (fragment.filterMode) {
                    FilterFragment.FilterMode.BY_DATE -> 0
                    FilterFragment.FilterMode.BY_PROXIMITY -> 1
                    FilterFragment.FilterMode.NONE -> 2
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var builder = AlertDialog.Builder(this, R.style.MyAlertDialogTheme)

        builder.setTitle(R.string.sort_by)

        builder.setSingleChoiceItems(listFilter.toTypedArray(), selectedPosition) { _, position ->
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
            else -> {
            }
        }
    }

    //Location
    @SuppressLint("MissingPermission")
    fun getLastLocation(onLocationResult: OnLocationResult) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        try {
                            onLocationResult(location)
                        } catch (e: Exception) {
                            println(e.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation { result ->
                    result.run {
                        notifyResult(result)
                    }
                }
            }
        }
    }

    private fun notifyResult(location: Location) {
        getForegroundFragment()?.run {
            try {
                (this as OnLocationResult).invoke(location)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun closeSearch() {
        val searchView = searchItem.actionView as SearchView
        searchView.setQuery("", false)
        searchView.isIconified = true
    }
}
