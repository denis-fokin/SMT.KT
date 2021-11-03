import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context


fun main() {
    // These examples need model generation turned on.
    val cfg = HashMap<String, String>()
    cfg["model"] = "true"
    val ctx = Context(cfg)

    val x = ctx.mkConst(ctx.mkSymbol("x"), ctx.realSort) as ArithExpr<*>
    val y = ctx.mkConst(ctx.mkSymbol("y"), ctx.realSort) as ArithExpr<*>
    val z = ctx.mkConst(ctx.mkSymbol("z"), ctx.realSort) as ArithExpr<*>

    val solver = ctx.mkSolver()

    val `3*x` = ctx.mkMul(ctx.mkInt(3),x)
    val `2*y` = ctx.mkMul(ctx.mkInt(2), y)
    val `3*x + 2*y` = ctx.mkAdd(`3*x`, `2*y`);
    val `3*x + 2*y - z` = ctx.mkSub(`3*x + 2*y`, z)

    solver.add(ctx.mkEq(`3*x + 2*y - z`, ctx.mkInt(1)))

    val `2*x` = ctx.mkMul(ctx.mkInt(2),x)
    val `2*x - 2*y` = ctx.mkSub(`2*x`, `2*y`);
    val `4*z` = ctx.mkMul(ctx.mkInt(4), z)
    val `2*x - 2*y + 4*z` = ctx.mkAdd(`2*x - 2*y`, `4*z`)

    solver.add(ctx.mkEq(`2*x - 2*y + 4*z`, ctx.mkReal(-2)))

    val `-x` = ctx.mkUnaryMinus(x)
    val `0,5*y` = ctx.mkMul(ctx.mkReal(1,2),y)
    val `-x + 0,5*y` = ctx.mkAdd(`-x`, `0,5*y`)
    val `-x + 0,5*y - z` = ctx.mkSub(`-x + 0,5*y`, z)

    solver.add(ctx.mkEq(`-x + 0,5*y - z`, ctx.mkInt(0)))

    println(solver.check())
    print(solver.model)
}