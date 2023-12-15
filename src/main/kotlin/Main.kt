package org.example

import kotlin.random.Random

data class Person(
    val name: String,
    val age: Int,
    val email: Email
)

data class Email(
    val email: String
)

fun main() {
    val random = Random.Default
    val people = Faker.fake<Person>()
        .specify(Person::name, String::name)
        .specify(Person::age, Int.generator(1, 100))

    println(people.generate(random).take(10).toList())
}
