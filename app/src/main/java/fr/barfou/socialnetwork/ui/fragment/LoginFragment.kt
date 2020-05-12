package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            loginViewModel = ViewModelProvider(this, LoginViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        btnLogin.setOnClickListener {
            if (!etLogin.text.isNullOrBlank() && !etPassword.text.isNullOrBlank()) {
                auth.signInWithEmailAndPassword(etLogin.text.toString(), etPassword.text.toString())
                        .addOnCompleteListener(this.requireActivity()) { task ->
                            if (task.isSuccessful) {

                            } else {
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
            else {
                Toast.makeText(requireContext(), "Saisie incorrecte.", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(
                R.id.action_login_fragment_to_register_fragment
            )
        }
    }
}