package com.scythe.developertools.memory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.scythe.developertools.R
import kotlinx.android.synthetic.main.fragment_memory.*

class MemoryNavFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_memory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test_btn.setOnClickListener {
            it.findNavController().navigate(R.id.action_memoryNavFragment_to_memoryToolsActivity)
        }
    }
}