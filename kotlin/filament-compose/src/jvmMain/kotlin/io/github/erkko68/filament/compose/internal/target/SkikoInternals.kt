package io.github.erkko68.filament.compose.internal.target

import java.awt.Container
import java.awt.Window
import org.jetbrains.skiko.SkiaLayer

// ponytail: skiko exposes neither its redrawer nor its DirectContext; drop once Compose does.

/** The redrawer skiko draws [window] with (`SkiaLayer.redrawerManager.redrawer`), or null before it exists. */
internal fun skikoRedrawer(window: Window?): Any? {
    val layer = window?.findSkiaLayer() ?: return null
    return layer.field("redrawerManager")!!.field("redrawer")
}

internal fun Container.findSkiaLayer(): SkiaLayer? {
    if (this is SkiaLayer) return this
    for (child in components) (child as? Container)?.findSkiaLayer()?.let { return it }
    return null
}

internal fun Any.field(name: String): Any? {
    var cls: Class<*>? = javaClass
    while (cls != null) {
        val f = cls.declaredFields.firstOrNull { it.name == name }
        if (f != null) {
            f.isAccessible = true
            return f.get(this)
        }
        cls = cls.superclass
    }
    throw NoSuchFieldException("${javaClass.name}.$name — skiko internals changed")
}
