package org.kodein.di

import org.kodein.di.bindings.UnboundedScope
import org.kodein.di.test.FixMethodOrder
import org.kodein.di.test.IPerson
import org.kodein.di.test.MethodSorters
import org.kodein.di.test.Person
import org.kodein.type.TypeToken
import org.kodein.type.erased
import org.kodein.type.erasedComp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ErasedJvmTests_03_Description {

    object TestScope : UnboundedScope()

    // Only the JVM supports precise description
    @Test
    fun test_00_BindingsDescription() {

        val di = DI {
            bind<IPerson>() with provider { Person() }
            bind<IPerson>(tag = "thread-scopedSingleton") with scopedSingleton(ref = threadLocal) { Person("ts") }
            bind<IPerson>(tag = "scopedSingleton") with scopedSingleton { Person("s") }
            bind<IPerson>(tag = "factory") with factory { name: String -> Person(name) }
            bind<IPerson>(tag = "instance") with instance(Person("i"))
            bind<String>(tag = "scoped") with scoped(TestScope).scopedSingleton { "" }
            constant(tag = "answer") with 42
        }

        val lines = di.container.tree.bindings.description().trim().lineSequence().map(String::trim).toList()
        assertEquals(7, lines.size)
        assertTrue("bind<IPerson>() with provider { Person }" in lines)
        assertTrue("bind<IPerson>(tag = \"thread-scopedSingleton\") with scopedSingleton(ref = threadLocal) { Person }" in lines)
        assertTrue("bind<IPerson>(tag = \"scopedSingleton\") with scopedSingleton { Person }" in lines)
        assertTrue("bind<IPerson>(tag = \"factory\") with factory { String -> Person }" in lines)
        assertTrue("bind<IPerson>(tag = \"instance\") with instance ( Person )" in lines)
        assertTrue("bind<String>(tag = \"scoped\") with scoped(ErasedJvmTests_03_Description.TestScope).scopedSingleton { String }" in lines)
        assertTrue("bind<Int>(tag = \"answer\") with instance ( Int )" in lines)
    }

    // Only the JVM supports precise description
    @Test
    fun test_01_BindingsFullDescription() {

        val di = DI {
            bind<IPerson>() with provider { Person() }
            bind<IPerson>(tag = "thread-scopedSingleton") with scopedSingleton(ref = threadLocal) { Person("ts") }
            bind<IPerson>(tag = "scopedSingleton") with scopedSingleton { Person("s") }
            bind<IPerson>(tag = "factory") with factory { name: String -> Person(name) }
            bind<IPerson>(tag = "instance") with instance(Person("i"))
            bind<String>(tag = "scoped") with scoped(TestScope).scopedSingleton { "" }
            constant(tag = "answer") with 42
        }

        val lines = di.container.tree.bindings.fullDescription().trim().lineSequence().map(String::trim).toList()
        assertEquals(7, lines.size)
        assertTrue("bind<org.kodein.di.test.IPerson>() with provider { org.kodein.di.test.Person }" in lines)
        assertTrue("bind<org.kodein.di.test.IPerson>(tag = \"thread-scopedSingleton\") with scopedSingleton(ref = org.kodein.di.threadLocal) { org.kodein.di.test.Person }" in lines)
        assertTrue("bind<org.kodein.di.test.IPerson>(tag = \"scopedSingleton\") with scopedSingleton { org.kodein.di.test.Person }" in lines)
        assertTrue("bind<org.kodein.di.test.IPerson>(tag = \"factory\") with factory { kotlin.String -> org.kodein.di.test.Person }" in lines)
        assertTrue("bind<org.kodein.di.test.IPerson>(tag = \"instance\") with instance ( org.kodein.di.test.Person )" in lines)
        assertTrue("bind<kotlin.String>(tag = \"scoped\") with scoped(org.kodein.di.ErasedJvmTests_03_Description.TestScope).scopedSingleton { kotlin.String }" in lines)
        assertTrue("bind<kotlin.Int>(tag = \"answer\") with instance ( kotlin.Int )" in lines)
    }

    // Only the JVM supports precise description
    @Test fun test_02_RegisteredBindings() {
        val kodein = DI {
            bind<IPerson>() with provider { Person() }
            bind<IPerson>(tag = "thread-scopedSingleton") with scopedSingleton(ref = threadLocal) { Person("ts") }
            bind<IPerson>(tag = "scopedSingleton") with scopedSingleton { Person("s") }
            bind<IPerson>(tag = "factory") with factory { name: String -> Person(name) }
            bind<IPerson>(tag = "instance") with instance(Person("i"))
            constant(tag = "answer") with 42
        }

        assertEquals(6, kodein.container.tree.bindings.size)
        assertEquals("provider", kodein.container.tree.bindings[DI.Key(TypeToken.Any, TypeToken.Unit, org.kodein.type.generic<IPerson>(), null)]!!.first().binding.factoryName())
        assertEquals("scopedSingleton(ref = threadLocal)", kodein.container.tree.bindings[DI.Key(TypeToken.Any, TypeToken.Unit, org.kodein.type.generic<IPerson>(), "thread-scopedSingleton")]!!.first().binding.factoryName())
        assertEquals("scopedSingleton", kodein.container.tree.bindings[DI.Key(TypeToken.Any, TypeToken.Unit, org.kodein.type.generic<IPerson>(), "scopedSingleton")]!!.first().binding.factoryName())
        assertEquals("factory", kodein.container.tree.bindings[DI.Key(TypeToken.Any, org.kodein.type.generic<String>(), org.kodein.type.generic<IPerson>(), "factory")]!!.first().binding.factoryName())
        assertEquals("instance", kodein.container.tree.bindings[DI.Key(TypeToken.Any, TypeToken.Unit, org.kodein.type.generic<IPerson>(), "instance")]!!.first().binding.factoryName())
        assertEquals("instance", kodein.container.tree.bindings[DI.Key(TypeToken.Any, TypeToken.Unit, org.kodein.type.generic<Int>(), "answer")]!!.first().binding.factoryName())
    }

    open class A
    class B : A()
    @Suppress("unused")
    class G<out T : A>

    // Only the JVM supports precise description
    @Test fun test_03_SimpleDispString() {

        assertEquals("Int", org.kodein.type.generic<Int>().simpleDispString())

        assertEquals("Array<Char>", org.kodein.type.generic<Array<Char>>().simpleDispString())

        assertEquals("List<*>", org.kodein.type.generic<List<*>>().simpleDispString())
        assertEquals("List<String>", erasedComp(List::class, erased<String>()).simpleDispString())

        assertEquals("Map<String, Any>", erasedComp(Map::class, erased<String>(), erased<Any>()).simpleDispString())
        assertEquals("Map<String, String>", erasedComp(Map::class, erased<String>(), erased<String>()).simpleDispString())

        assertEquals("ErasedJvmTests_03_Description.G<*>", org.kodein.type.generic<G<*>>().simpleDispString())
//        assertEquals("ErasedJvmTests_03_Description.G<ErasedJvmTests_03_Description.A>", erasedComp1<G<A>, A>().simpleDispString())
//        assertEquals("ErasedJvmTests_03_Description.G<ErasedJvmTests_03_Description.B>", erasedComp1<G<B>, B>().simpleDispString())
    }

    // Only the JVM supports precise description
    @Test fun test_04_FullDispString() {

        assertEquals("kotlin.Int", org.kodein.type.generic<Int>().qualifiedDispString())

        assertEquals("kotlin.Array<kotlin.Char>", org.kodein.type.generic<Array<Char>>().qualifiedDispString())

        assertEquals("kotlin.collections.List<*>", org.kodein.type.generic<List<*>>().qualifiedDispString())
//        assertEquals("kotlin.collections.List<kotlin.String>", erasedComp1<List<String>, String>().fullDispString())

//        assertEquals("kotlin.collections.Map<kotlin.String, kotlin.Any>", erasedComp2<Map<String, *>, String, Any>().fullDispString())
//        assertEquals("kotlin.collections.Map<kotlin.String, kotlin.String>", erasedComp2<Map<String, String>, String, String>().fullDispString())

        assertEquals("org.kodein.di.ErasedJvmTests_03_Description.G<*>", org.kodein.type.generic<G<*>>().qualifiedDispString())
//        assertEquals("org.kodein.di.erased.ErasedJvmTests_03_Description.G<org.kodein.di.erased.ErasedJvmTests_03_Description.A>", erasedComp1<ErasedJvmTests_03_Description.G<ErasedJvmTests_03_Description.A>, ErasedJvmTests_03_Description.A>().fullDispString())
//        assertEquals("org.kodein.di.erased.ErasedJvmTests_03_Description.G<org.kodein.di.erased.ErasedJvmTests_03_Description.B>", erasedComp1<ErasedJvmTests_03_Description.G<ErasedJvmTests_03_Description.B>, ErasedJvmTests_03_Description.B>().fullDispString())
    }

    // Only the JVM supports precise description
    @Test fun test_05_simpleKeyFullDescription() {
        val key = DI.Key(
                contextType = org.kodein.type.generic<Any>(),
                argType = org.kodein.type.generic<Unit>(),
                type = org.kodein.type.generic<String>(),
                tag = null
        )

        assertEquals("bind<kotlin.String>()", key.bindFullDescription)
        assertEquals("bind<kotlin.String>() with ? { ? }", key.fullDescription)
    }

    // Only the JVM supports precise description
    @Test fun test_06_complexKeyFullDescription() {
        val key = DI.Key(
                contextType = org.kodein.type.generic<String>(),
                argType = erasedComp(Pair::class, org.kodein.type.generic<String>(), org.kodein.type.generic<String>()),
                type = org.kodein.type.generic<IntRange>(),
                tag = "tag"
        )

        assertEquals("bind<kotlin.ranges.IntRange>(tag = \"tag\")", key.bindFullDescription)
        assertEquals("bind<kotlin.ranges.IntRange>(tag = \"tag\") with ?<kotlin.String>().? { kotlin.Pair<kotlin.String, kotlin.String> -> ? }", key.fullDescription)
    }

}
