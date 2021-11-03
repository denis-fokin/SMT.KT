import org.junit.jupiter.api.Test

class BVArithTest {

    @Test
    fun testBVAndBVConst() {
        solver {
            // x = 10

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bv = bvVar("x", 32)
            // this is how we create constant bv
            val const = context.mkBV(10, 32)

            add(bv `=` const)
        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }

    @Test
    fun testTestTwoBVSum() {
        solver {
            // x + y = 9
            // y = 5

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bvX = bvVar("x", 32)
            val bvY = bvVar("y", 32)
            // this is how we create constant bv
            val const10 = context.mkBV(9, 32)
            val const5 = context.mkBV(5, 32)

            add(bvX + bvY `=` const10)
            add(bvY `=` const5)


        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }

    @Test
    fun testTestTwoBVMul() {
        solver {
            // x + y = 12
            // y = 4

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bvX = bvVar("x", 32)
            val bvY = bvVar("y", 32)
            // this is how we create constant bv
            val const10 = context.mkBV(12, 32)
            val const5 = context.mkBV(4, 32)

            add(bvX * bvY `=` const10)
            add(bvY `=` const5)


        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }
}