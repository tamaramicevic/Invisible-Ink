package com.invisibleink.note

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.architecture.ViewProvider
import com.invisibleink.injection.InvisibleInkApplication
import javax.inject.Inject

class NoteFragment : Fragment(), ViewProvider {

    @Inject
    lateinit var presenter: NotePresenter
    private lateinit var viewDelegate: NoteViewDelegate

    override fun <T : View> findViewById(id: Int): T = findViewById(id)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_note, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as InvisibleInkApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDelegate = NoteViewDelegate(this)
        presenter.attach(viewDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }
}
