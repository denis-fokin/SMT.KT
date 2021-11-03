import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context
import kotlin.jvm.JvmStatic

object SimpleEquation {
    // 2*x = y
    // y - x = 3
    @JvmStatic
    fun main(args: Array<String>) {
        val cfg = HashMap<String, String>()
        cfg["model"] = "true"
        val ctx = Context(cfg)

        val x = ctx.mkConst(ctx.mkSymbol("x"), ctx.realSort) as ArithExpr<*>
        val y = ctx.mkConst(ctx.mkSymbol("y"), ctx.realSort) as ArithExpr<*>

        val solver = ctx.mkSolver()
        val `2*x` = ctx.mkMul( ctx.mkInt(2),x)

        solver.add(ctx.mkEq(`2*x`, y))

        val `y - x` = ctx.mkSub(y, x)
        solver.add(ctx.mkEq(`y - x`, ctx.mkInt(3)))

        println(solver.check())
        print(solver.model)
    }
}