package com.kilco.tomato

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.kilco.tomato.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tomato: Tomato = ViewModelProvider(this)[Tomato::class.java]
        val binding: FragmentSettingsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.data = tomato
        binding.lifecycleOwner = this.activity

        binding.setButton1.setOnClickListener {
            if (!TextUtils.isEmpty(setMaxCustomTimeLength.text)) {
                tomato.maxCustomTimeLength.value = setMaxCustomTimeLength.text.toString().toInt()
            }
            tomato.save()
            Toast.makeText(this.activity, "设置成功!", Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        binding.keepScreenOnSwitch.setOnCheckedChangeListener { _, isChecked ->
            tomato.keepScreenOnTeller.value = isChecked
        }
        return binding.root
    }
}
