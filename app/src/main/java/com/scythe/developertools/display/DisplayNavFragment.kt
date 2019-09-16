package com.scythe.developertools.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.scythe.developertools.R
import kotlinx.android.synthetic.main.fragment_display.*
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.ActivityNavigatorExtras

class DisplayNavFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screen_on_card.setOnClickListener {
            activity?.let { parent ->
                val extras = ActivityNavigatorExtras(
                        ActivityOptionsCompat.makeSceneTransitionAnimation(parent,
                                card_header,
                                card_header.transitionName))
                it.findNavController()
                        .navigate(R.id.action_displayNavFragment_to_displayToolsActivity,
                                null,
                                null,
                                extras)
            }
        }
    }
}