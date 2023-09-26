package com.fajar.mysharedpreferences.activity

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.transition.Transition.MatchOrder
import com.fajar.mysharedpreferences.R
import com.fajar.mysharedpreferences.UserPreference
import com.fajar.mysharedpreferences.databinding.ActivityMainBinding
import com.fajar.mysharedpreferences.model.UserModel

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserPreference: UserPreference

    private var isPreferenceEmpty = false
    private lateinit var userModel: UserModel

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null && result.resultCode == FormUserPreferenceActivity.RESULT_CODE) {
            userModel =
                result.data?.getParcelableExtra(
                    FormUserPreferenceActivity.EXTRA_RESULT,
                    userModel::class.java
                ) as UserModel
            populateView(userModel)
            checkForm(userModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My User Preference"

        mUserPreference = UserPreference(this)

        showExistingPreference()


        binding.btnSave.setOnClickListener(this)
    }

    private fun showExistingPreference() {
        userModel = mUserPreference.getUser()
        populateView(userModel)
        checkForm(userModel)
    }

    private fun checkForm(userModel: UserModel) {
        when {
            userModel.name.toString().isNotEmpty() -> {
                binding.btnSave.text = getString(R.string.change)
                isPreferenceEmpty = false
            }

            else -> {
                binding.btnSave.text = getString(R.string.save)
                isPreferenceEmpty = true
            }

        }
    }

    private fun populateView(userModel: UserModel) {
        binding.tvName.text =
            if (userModel.name.toString().isEmpty()) "Tidak ada" else userModel.name
        binding.tvAge.text =
            if (userModel.age.toString().isEmpty()) "Tidak ada" else userModel.age.toString()
        binding.tvIsLoveMu.text = if (userModel.isLove) "Ya" else "Tidak"
        binding.tvEmail.text =
            if (userModel.email.toString().isEmpty()) "Tidak ada" else userModel.email
        binding.tvPhone.text =
            if (userModel.phoneNumber.toString().isEmpty()) "Tidak ada" else userModel.phoneNumber

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(view: View) {
        if (view.id == R.id.btn_save) {
            val intent = Intent(this@MainActivity, FormUserPreferenceActivity::class.java)
            when {
                isPreferenceEmpty -> {
                    intent.putExtra(
                        FormUserPreferenceActivity.EXTRA_TYPE_FORM,
                        FormUserPreferenceActivity.TYPE_ADD
                    )
                    intent.putExtra("USER", userModel)
                }

                else -> {
                    intent.putExtra(
                        FormUserPreferenceActivity.EXTRA_TYPE_FORM,
                        FormUserPreferenceActivity.TYPE_EDIT
                    )
                    intent.putExtra("USER", userModel)
                }
            }
            resultLauncher.launch(intent)
        }
    }
}