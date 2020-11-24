package org.kodein.di

import org.kodein.di.test.FixMethodOrder
import org.kodein.di.test.MethodSorters
import org.kodein.di.test.Person
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GenericJvmTests_15_OnReady {

    @Test
    fun test_00_OnReadyCallback() {
        var passed = false
        DI {
            constant(tag = "name") with "Salomon"
            bind<Person>() with scopedSingleton { Person(instance(tag = "name")) }
            onReady {
                assertEquals("Salomon", instance<Person>().name)
                passed = true
            }
        }
        assertTrue(passed)
    }

}
