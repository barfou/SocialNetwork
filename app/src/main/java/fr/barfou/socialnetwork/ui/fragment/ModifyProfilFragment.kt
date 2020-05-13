package fr.barfou.socialnetwork.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.adapter.TrophyAdapter
import kotlinx.android.synthetic.main.fragment_modify_profil.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.*


class ModifyProfilFragment: Fragment() {

    val c = getInstance()
    var year : Int? = null
    var month : Int? = null
    var day : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_modify_profil)
            this.setDisplayHomeAsUpEnabled(false)
        }



        dtpBirthday.setOnClickListener {
            if(etBirthday.text.toString() == "") {
                day = c.get(DAY_OF_MONTH)
                month = c.get(MONTH)
                year = c.get(YEAR)
            } else {
                val tblDate : List<String> = etBirthday.text.toString().split("/")

                day = tblDate[0].toInt()
                month = tblDate[1].toInt()
                year = tblDate[2].toInt()
            }

            val dpd = DatePickerDialog(
                this.requireContext(),

                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                etBirthday.setText(dayOfMonth.toString() + "/"  + monthOfYear.toString() + "/" + year.toString())

            }, year!!, month!!, day!!)

            dpd.show()
        }
    }
}