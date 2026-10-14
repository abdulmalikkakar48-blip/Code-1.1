package com.maree.app

/** All local feature writes share the same lock as erasure of the user's data. */
internal object LocalFeatureLock {
    val lock = Any()
    // Access under lock; invalidates planning operations queued before a full reset.
    var erasureGeneration = 0L
}
