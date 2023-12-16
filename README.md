# Faker

A Kotlin library to create instances of classes filled with random values. Think [Autofixture](https://github.com/AutoFixture/AutoFixture), but for the JVM.

Use it for whatever, but the whole reason for the existence of this library is to minimize the arrange/setup phase of your unit tests.

## Usage

If you don’t care about the values, simply call the `fake` function

```kotlin
import faker.fake

data class Person(val name: String, val age: UInt)

val tenPeople = fake<Person>().take(10).toList()
```

If you want to restrict values to a more sensible value in your domain, you can override them with a custom value generator. Let’s restrict `age` to between 0 and 120

```kotlin
import faker.fake
import faker.generator

data class Person(val name: String, val age: UInt)

val tenPeople = fake<Person>()
    .set(Person::age, Int::generator(0, 120))
    .take(10)
    .toList()
```