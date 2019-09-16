package com.scythe.developertools.memory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.findNavController
import com.scythe.developertools.R
import kotlinx.android.synthetic.main.fragment_memory.*

class MemoryNavFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_memory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        zombie_apps_card.setOnClickListener {
            activity?.let { parent ->
                val extras = ActivityNavigatorExtras(
                        ActivityOptionsCompat.makeSceneTransitionAnimation(parent,
                                card_header,
                                card_header.transitionName))
                it.findNavController()
                        .navigate(R.id.action_memoryNavFragment_to_memoryToolsActivity,
                                null,
                                null,
                                extras)
            }
        }
    }
}