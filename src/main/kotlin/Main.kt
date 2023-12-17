package org.example

import faker.fake
import faker.generator
import faker.name
import kotlin.random.Random

data class Person(
    val name: String,
    val age: Int,
    val email: Email,
    val numbers: List<Int>,
    val strings: List<String>,
    val map: Map<String, Int>,
)

data class Email(
    val email: String
)

fun main() {
    val random = Random.Default
    val people = fake<Person>()
        .set(Person::name, String::name)
        .set(Person::age, Int.generator(1, 100))
        .set(
            Person::email, fake<Email>()
                .set(Email::email, "a@b.com")
        )

    println(people.generate(random).take(10).toList())
}
