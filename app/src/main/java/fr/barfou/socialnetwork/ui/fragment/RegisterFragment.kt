package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import kotlinx.android.synthetic.main.fragment_login.btnLogin

class RegisterFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_register_fragment_to_login_fragment
            )
        }
    }
}