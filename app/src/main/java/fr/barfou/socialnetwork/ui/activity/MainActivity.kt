package fr.barfou.socialnetwork.ui.activity

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.navigation.findNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.utils.changeToolbarFont
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    enum class Mode { HOMEPAGE, FILTER }

    companion object {
        var mode: Mode = Mode.HOMEPAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolBar()
        //testFirebase()
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
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val manager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchItem = menu.findItem(R.id.search_item)
        val searchView = searchItem.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(this.componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                findNavController(R.id.main_fragment_container).navigate(R.id.action_home_fragment_to_filter_fragment)
                return true
            }
        })
        searchView.setOnCloseListener {
            onBackPressed()
            return@setOnCloseListener false
        }
        return true
    }

    private fun testFirebase() {
        val activitiesRef = Firebase.database.reference.child("Activities")
        var idTask = activitiesRef.push().key!!
        activitiesRef.child(idTask).setValue(true)
    }
}
