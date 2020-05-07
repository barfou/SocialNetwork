package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.UserAdapter
import kotlinx.android.synthetic.main.details_fragment.*

class FragmentFilter : Fragment() {

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        /*userAdapter = UserAdapter()
        recycler_view.apply {
            adapter = userAdapter
            if (itemDecorationCount == 0) addItemDecoration(UserAdapter.OffsetDecoration())
        }
        loadAdapter()*/
    }

    private fun loadAdapter() {
        //
    }
}