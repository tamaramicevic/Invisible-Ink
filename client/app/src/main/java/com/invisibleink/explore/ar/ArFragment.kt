package com.invisibleink.explore.ar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.extensions.findViewOrThrow
import com.invisibleink.injection.InvisibleInkApplication
import javax.inject.Inject

class ArExploreFragment : Fragment(), ViewProvider {

    @Inject
    lateinit var presenter: ArExplorePresenter
    private lateinit var viewDelegate: ArExploreViewDelegate

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar_explore, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = ArExploreViewDelegate(this)
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }
}
