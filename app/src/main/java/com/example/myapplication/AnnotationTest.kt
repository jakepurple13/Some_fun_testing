package com.example.myapplication

import java.lang.Exception

class AnnoTester {
    fun mained() {
        val validator = Validator()
        val item = Item(amount = 1.0f, name = "Bob")
        Loged.e("Is instance valid? ${validator.isValid(item)} with $item")
        try {
            val item1 = Item(amount = 1.0f, name = "John")
            Loged.e("Is instance valid? ${validator.isValid(item1)} with $item1")
        } catch (e: NameNotAllowedException) {
            Loged.e("Is instance valid? super false")
        }
        val item2 = Item(amount = 1.0f, name = "Maya")
        Loged.e("Is instance valid? ${validator.isValid(item2)} with $item2")
    }
}

@Target(AnnotationTarget.FIELD)
annotation class Positive

@Target(AnnotationTarget.FIELD)
annotation class AllowedNames(val names: Array<String>)

class Item(@Positive var amount: Float, @AllowedNames(["Alice", "Bob", "Maya"]) var name: String) {

    private val v = Validator()

    init {
        val f = v.isValid(this)
        if (!f) {
            this.amount = 1f
            this.name = "N/A"
        }
    }

    override fun toString(): String {
        return "$name at $amount"
    }
}

class Validator() {

    /**
     * Return true if every item's property annotated with @Positive is positive and if
     * every item's property annotated with @AllowedNames has a value specified in that annotation.
     */
    @Throws(NameNotAllowedException::class)
    fun isValid(item: Item): Boolean {
        val fields = item::class.java.declaredFields
        for (field in fields) {
            field.isAccessible = true
            for (annotation in field.annotations) {
                val value = field.get(item)
                if (field.isAnnotationPresent(Positive::class.java)) {
                    val amount = value as Float
                    if (amount < 0) {
                        return false
                    }
                }
                if (field.isAnnotationPresent(AllowedNames::class.java)) {
                    val allowedNames = field.getAnnotation(AllowedNames::class.java)?.names
                    val name = value as String
                    allowedNames?.let {
                        if (!it.contains(name)) {
                            return false
                            //throw NameNotAllowedException()
                        }
                    }
                }
            }
        }
        return true
    }
}

class NameNotAllowedException(message: String? = "That name is not allowed") : Exception(message)