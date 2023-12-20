package org.example

import kofix.fixture
import kofix.generator
import kofix.name
import kotlin.random.Random

data class Person(
    val name: String,
    val age: Int,
    val email: Email,
    val numbers: List<Int>,
    val strings: List<String>,
    val map: Map<String, Int>,
    val set: Set<Email>
)

data class Email(
    val email: String
)

fun main() {
    val random = Random.Default
    val people = fixture<Person>()
        .set(Person::name, String::name)
        .set(Person::age, Int.generator(1, 100))
        .set(
            Person::email, fixture<Email>()
                .set(Email::email, "a@b.com")
        )

    println(people.generate(random).take(10).toList().joinToString("\n"))
}
