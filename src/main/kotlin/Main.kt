package org.example

import kotlin.random.Random
import kotlin.reflect.full.primaryConstructor

data class Person(
    val name: String,
    val age: Int
)

val allowedCharacters = ('A'..'Z') + ('a'..'z') + ('0'..'9')

fun String.Companion.generate(random: Random): String {
    val length = random.nextInt(0, 50)
    return (1..length)
        .map { allowedCharacters.random(random) }
        .joinToString("")
}

fun Boolean.Companion.generate(random: Random): Boolean =
    listOf(true, false).random(random)

inline fun <reified T : Any> generate(random: Random): T {
    val constructor = T::class.primaryConstructor!!
    val constructorParams = constructor.parameters.map {
        when (it.type.classifier) {
            Byte::class -> random.nextBytes(1)[0]
            Short::class -> random.nextInt(Short.MAX_VALUE + 1)
            String::class -> String.generate(random)
            Char::class -> allowedCharacters.random(random)
            Boolean::class -> Boolean.generate(random)
            Int::class -> random.nextInt(0, 1000)
            else -> throw RuntimeException("Unknown type $it")
        }
    }

    return constructor.call(*constructorParams.toTypedArray())
}

//TIP Press <shortcut raw="SHIFT"/> twice to open the Search Everywhere dialog and type <b>show whitespaces</b>,
// then press <shortcut raw="ENTER"/>. You can now see whitespace characters in your code.
fun main() {
    println(generate<Person>(Random))
}