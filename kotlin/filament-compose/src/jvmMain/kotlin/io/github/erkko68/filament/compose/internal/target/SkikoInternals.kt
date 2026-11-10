package io.github.erkko68.filament.compose.internal.target

import java.awt.Container
import java.awt.Window
import org.jetbrains.skiko.SkiaLayer

// ponytail: skiko exposes neither its renderer nor its DirectContext; drop once Compose does.
// Reflects skiko 0.156 internals (Compose 1.13): the redrawers of 0.150 became renderers behind a FrameDriver.

/** The renderer skiko draws [layer] with (`frameDriverManager.current.producer.renderer`), or null before it exists. */
internal fun skikoRenderer(layer: SkiaLayer): Any? =
    layer.field("frameDriverManager")!!.field("current")?.field("producer")!!.field("renderer")

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
