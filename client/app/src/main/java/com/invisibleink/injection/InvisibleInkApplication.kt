package com.invisibleink.injection

import android.app.Application
import com.invisibleink.explore.ExploreFragment
import com.invisibleink.note.NoteFragment
import dagger.Component

@Component
interface ApplicationComponent {

    // Field injection for activities/fragments
    fun inject(obj: ExploreFragment)
    fun inject(obj: NoteFragment)
}

class InvisibleInkApplication : Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}