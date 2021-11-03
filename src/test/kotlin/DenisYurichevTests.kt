import com.microsoft.z3.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DenisYurichevTests {
    @Test
    fun test2_2_1() {
        //  School-level system of equations
        solver {
            config {
                this["model"] = "true"
            }

            val x = s("x")
            val y = s("y")
            val z = s("z")

            add(3*x + 2*y - z `=` 1)
            add(2*x - 2*y + 4*z `=` -2)
            add(-x + fr(1,2)*y - z `=` 0)

        }.check {
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess{ model, context ->
            createRatAndAssert(context, model,"x", 1, 1)
            createRatAndAssert(context, model,"y", -2, 1)
            createRatAndAssert(context, model,"z", -2, 1)
        }
    }

    @Test
    fun test2_2_2() {
        //  Another school-level system of equations
        solver {
            config {
                this["model"] = "true"
            }

            val (circle, square, triangle) = s("circle", "square", "triangle")

            add(circle + circle `=` 10)
            add(circle * square + square `=` 12)
            add(circle * square - triangle * circle `=` circle)

        }.check {
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess{ model, context ->
            createRatAndAssert(context, model,"circle", 5, 1)
            createRatAndAssert(context, model,"triangle", 1, 1)
            createRatAndAssert(context, model,"square", 2, 1)
        }
    }

    @Test
    fun test3_3() {
        // Wood workshop, linear programming and Leonid Kantorovich
        optimizer {
            config {
                this["model"] = "true"
            }

            val workpiecesTotal = s("workpieces_total")
            val (cutA , cutB , cutC , cutD) = s("cut_A", "cut_B", "cut_C", "cut_D")
            val (outA , outB) = s("out_A", "out_B")

            add(workpiecesTotal `=` cutA + cutB + cutC + cutD)

            add(cutA GE i(0))
            add(cutB GE i(0))
            add(cutC GE i(0))
            add(cutD GE i(0))

            add(outA `=` 3 * cutA + 2 * cutB + cutC)
            add(outB `=` cutA + 6*cutB + 9*cutC + 13*cutD)

            add(outA `=` 800)
            add(outB `=` 400)

            minimize(workpiecesTotal)

            println()

        }.check {
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess{ model, context ->
            createIntAndAssert(context, model,"cut_A", 250)
            createIntAndAssert(context, model,"cut_B", 25)
            createIntAndAssert(context, model,"cut_C", 0)
            createIntAndAssert(context, model,"cut_D", 0)
            createIntAndAssert(context, model,"out_A", 800)
            createIntAndAssert(context, model,"out_B", 400)
            createIntAndAssert(context, model,"workpieces_total", 275)
        }
    }

    @Test
    fun test3_4() {
        // Puzzle with animals
        solver ("QF_BV") {
            config {
                this["model"] = "true"
            }

            val dogBV = bvVar("dog", 16)
            val catBV = bvVar("cat", 16)
            val rabbitBV = bvVar("rabbit", 16)

            val rabbitAndCat = context.mkBVAdd(rabbitBV, catBV)
            val dogAndRabbit = context.mkBVAdd(dogBV, rabbitBV)
            val dogAndCat = context.mkBVAdd(dogBV, catBV)

            add(rabbitAndCat `=` bvConst(context,10, 16))
            add(dogAndRabbit `=` bvConst(context,20, 16))
            add(dogAndCat `=` context.mkBV(24, 16))


        }.check {
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess { model, context ->
            createBvAndAssert(context, model,"cat", "7".toInt(radix = 16), 16)
            createBvAndAssert(context, model,"dog", "11".toInt(radix = 16), 16)
            createBvAndAssert(context, model,"rabbit", "3".toInt(radix = 16), 16)
        }
    }

    @Test
    fun test3_5 () {
        // Subset sum

        solver {

            config {
                this["model"] = "true"
            }

            val set = arrayOf(-7, -3, -2, 5, 8)

            val variables = generateSequence(0) { it + 1 }
                .take(set.size).map { bvVar("var_$it",32) }.toList()

            val rt = variables.zip(set).map {
                    (variable, setElement) ->
                val setElementAsBV = bvConst(context, setElement, 32)
                variable * setElementAsBV
            }.toList()

            val bvConst_0 = bvConst(context, 0, 32)
            val bvConst_1 = bvConst(context, 1, 32)

            variables.forEach {
                add((it `=` bvConst_0) OR (it `=` bvConst_1))
            }

            val sumOfRt = rt.reduce { acc, bitVecExpr ->
                context.mkBVAdd(acc, bitVecExpr)
            }

            val sumOfVariables = variables.reduce { acc, bitVecExpr ->
                context.mkBVAdd(acc, bitVecExpr)
            }

            add(sumOfRt `=` bvConst_0)
            add(sumOfVariables SGE bvConst_1)

        }.check{
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess { model, context ->
            createBvAndAssert(context, model,"var_1", "1".toInt(radix = 32), 32)
            createBvAndAssert(context, model,"var_2", "1".toInt(radix = 32), 32)
            createBvAndAssert(context, model,"var_3", "1".toInt(radix = 32), 32)
        }
    }

    @Test
    fun test3_6 () {
        // Art of problem-solving
        solver {
            config {
                this["model"] = "true"
            }

            val (x, y) = realVar("x", "y")

            val CONST_0 = intConst(0)

            add(x GT CONST_0)
            add(y GT CONST_0)

            add( x + y `=` intConst(4) * x * y)

            println()

        }.check {
            Assertions.assertEquals(it.toInt(), 1)
        }.onSuccess{ model, context ->
            createRatAndAssert(context, model,"x", 1, 1)
            createRatAndAssert(context, model,"y", 1, 3)
        }
    }

    @Test
    fun test3_7() {
        // Yet another explanation of modulo inverse using SMT-solvers
        solver {
            config {
                this["model"] = "true"
            }

            val BVS_32 = context.mkBitVecSort(32)

            val m = bvVar("m", 32)
            val divisor = context.mkConst(context.mkSymbol(3), BVS_32) as BitVecExpr
            val decimalValue = "1234567".toLong(radix = 32)
            val bv1234567 = context.mkBV(decimalValue, 32)
            val const = bv1234567 * divisor

            add( const*m `=` const/divisor)

        }.check {
            println(it)
        }.onSuccess{ model, context ->
            println(model)
        }
    }

}

fun createBvAndAssert (context: Context, model: Model, name:String, expectedValue:Int, bvSize:Int) {
    val value = context.mkConst(context.mkSymbol(name), context.mkBitVecSort(bvSize)) as BitVecExpr
    Assertions.assertEquals(model.eval(value, true), context.mkBV(expectedValue, bvSize))
}

fun createIntAndAssert (context: Context, model: Model, name:String, result:Int) {
    val value = context.mkConst(context.mkSymbol(name), context.intSort) as ArithExpr<*>
    Assertions.assertEquals(model.eval(value, true), context.mkInt(result))
}

fun createRatAndAssert (context: Context, model: Model, name:String, numerator:Int, denominator:Int ) {
    val value = context.mkConst(context.mkSymbol(name), context.realSort) as ArithExpr<*>
    Assertions.assertEquals((model.evaluate(value, true) as RatNum).numerator.int, numerator)
    Assertions.assertEquals((model.evaluate(value, true) as RatNum).denominator.int, denominator)
}