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

    @Test
    fun testTestThreeBvSum() {
        solver {
            // x + y + z= 12
            // y = 4
            // z = 1

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bvX = bvVar("x", 32)
            val bvY = bvVar("y", 32)
            val bvZ = bvVar("z", 32)
            // this is how we create constant bv
            val const12 = context.mkBV(12, 32)
            val const4 = context.mkBV(4, 32)
            val const1 = context.mkBV(1, 32)

            val listXYZ = listOf(bvX, bvY, bvZ)

            val threeSum = listXYZ.reduce { acc, bitVecExpr ->
                context.mkBVAdd(acc, bitVecExpr)
            }

            add(threeSum `=` const12)
            add(bvY `=` const4)
            add(bvZ `=` const1)


        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }

    @Test
    fun testTestThreeBvSumWithNegativeConsts() {
        solver {
            // x + y + z - 1= 12
            // y = 4
            // z = 1

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bvX = bvVar("x", 32)
            val bvY = bvVar("y", 32)
            val bvZ = bvVar("z", 32)
            // this is how we create constant bv
            val const12 = context.mkBV(12, 32)
            val const4 = context.mkBV(4, 32)
            val const1 = context.mkBV(1, 32)
            val constNeg1 = context.mkBV(-1, 32)

            val listXYZ = listOf(bvX, bvY, bvZ)

            val threeSum = listXYZ.reduce { acc, bitVecExpr ->
                context.mkBVAdd(acc, bitVecExpr)
            }

            add((threeSum + constNeg1) `=` const12)
            add(bvY `=` const4)
            add(bvZ `=` const1)


        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }

    @Test
    fun testTestThreeBvNegativeMultiply() {
        solver {
            // x + y * (-2) + z = 3
            // x = 4
            // y = 2

            val BVS_32 = context.mkBitVecSort(32)
            // this is how we create variable bv
            val bvX = bvVar("x", 32)
            val bvY = bvVar("y", 32)
            val bvZ = bvVar("z", 32)
            // this is how we create constant bv
            val const3 = context.mkBV(3, 32)
            val const4 = context.mkBV(4, 32)
            val const2 = context.mkBV(2, 32)
            val constNeg2 = context.mkBV(-2, 32)

            val listXYZ = listOf(bvX, bvY * constNeg2, bvZ)

            val threeSum = listXYZ.reduce { acc, bitVecExpr ->
                context.mkBVAdd(acc, bitVecExpr)
            }

            add((threeSum) `=` const3)
            add(bvX `=` const4)
            add(bvY `=` const2)

        }.check {
            println(it)
        }.onSuccess { model, context ->
            println(model)
        }
    }
}