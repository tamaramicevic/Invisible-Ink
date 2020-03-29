package com.invisibleink.image

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

class ImageFragment : Fragment(), ViewProvider {

    override fun <T : View> findViewById(id: Int): T = findViewOrThrow(id)

    @Inject
    lateinit var presenter: ImagePresenter
    private lateinit var viewDelegate: ImageViewDelegate
    private lateinit var imageURL: String

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_show_image, container, false)

        val bundle = arguments
        if (bundle != null) {
            imageURL = bundle.getString("IMAGE-URL").toString()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = ImageViewDelegate(this)
        viewDelegate.imageURL = this.imageURL
        viewDelegate.context = context
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }
}