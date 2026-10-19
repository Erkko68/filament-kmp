package io.github.erkko68.filament.filamat.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.filamat.MaterialPackage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

/** The material compiler plus an engine ([Engine.Backend.NOOP] unless [createEngine] says otherwise) to load what it compiles. Materials from [load] are destroyed in [tearDown]. */
open class FilamatTestFixture {
    protected lateinit var engine: Engine
    private val materials = mutableListOf<Material>()

    @BeforeTest
    fun setUp() {
        MaterialBuilder.init()
        Filament.init()
        engine = createEngine()
    }

    protected open fun createEngine(): Engine = Engine.create(Engine.Backend.NOOP)!!

    @AfterTest
    fun tearDown() {
        if (::engine.isInitialized) {
            materials.forEach(engine::destroy)
            engine.flushAndWait()
            Engine.destroy(engine)
        }
        MaterialBuilder.shutdown()
    }

    /** Compiles this builder, failing the test if it doesn't. */
    protected fun MaterialBuilder.compile(): MaterialPackage = build().also { assertTrue(it.isValid, "material did not compile") }

    /**
     * Compiles for every platform, as apps do (the default, desktop, has no shaders web can load), and loads it into
     * a Filament [Material] built with [configure].
     */
    protected fun MaterialBuilder.load(configure: Material.Builder.() -> Material.Builder = { this }): Material =
        checkNotNull(Material.Builder().payload(platform(MaterialBuilder.Platform.ALL).compile().data).configure().build(engine)) {
            "Filament rejected the package"
        }.also { materials += it }
}
