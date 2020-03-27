package com.invisibleink.injection

import android.app.Application
import com.invisibleink.explore.ar.ArExploreFragment
import com.invisibleink.explore.map.MapExploreFragment
import com.invisibleink.image.ImageFragment
import com.invisibleink.injection.modules.NetworkModule
import com.invisibleink.note.NoteFragment
import dagger.Component

@Component(modules = [NetworkModule::class])
interface ApplicationComponent {

    // Field injection for activities/fragments
    fun inject(obj: NoteFragment)
    fun inject(obj: ArExploreFragment)
    fun inject(obj: MapExploreFragment)
    fun inject(obj: ImageFragment)
}

class InvisibleInkApplication : Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}