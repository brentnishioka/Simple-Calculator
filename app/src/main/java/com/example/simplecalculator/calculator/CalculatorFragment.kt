package com.example.simplecalculator.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.simplecalculator.R
import com.example.simplecalculator.databinding.FragmentCalculatorBinding

/**
 * A simple [Fragment] subclass.
 * Use the [CalculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalculatorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentCalculatorBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_calculator, container, false)

        val viewModelFactory = CalculatorViewModelFactory()

        val calculatorViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CalculatorViewModel::class.java)

        binding.lifecycleOwner = this
        binding.calculatorViewModel = calculatorViewModel

        return binding.root
    }
}