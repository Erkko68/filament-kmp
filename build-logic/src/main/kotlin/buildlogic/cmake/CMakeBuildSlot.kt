package buildlogic.cmake

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.registerIfAbsent

/** Held by every CMake build: each already runs a job per core, so two at once oversubscribe the machine. */
abstract class CMakeBuildSlot : BuildService<BuildServiceParameters.None>

fun Project.cmakeBuildSlot(): Provider<CMakeBuildSlot> =
    gradle.sharedServices.registerIfAbsent("cmakeBuildSlot", CMakeBuildSlot::class) { maxParallelUsages.set(1) }
