package com.invisibleink.architecture

/**
 * Top-level type representing a component capable of navigation to a [Destination].
 */
interface Router<TypeOfDestination : Destination> {
    fun routeTo(destination: TypeOfDestination)
}
