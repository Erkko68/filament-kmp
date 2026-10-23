package buildlogic.apigen.c

/** The c stage's result: the files it generates, by path under the C dir, and each C function the C++ API calls for. */
class CApi(val files: Map<String, String>, val bindings: List<Binding>)

/** C function [c], for the C++ [cpp]: how it's bound, or why it isn't ([reason]). */
class Binding(val c: String, val cpp: String, val status: Status, val reason: String? = null) {
    enum class Status { GENERATED, MANUAL, TODO, SKIPPED }
}
