import com.microsoft.z3.*

fun solver(logic:String? = null, content: DslSolverBuilder.() -> Unit): DslSolverBuilder {
    val builder = DslSolverBuilder(logic)
    builder.apply(content)
    return builder
}

fun optimizer(content: DslOptimizerBuilder.() -> Unit) : DslOptimizerBuilder{
    val optimizer = DslOptimizerBuilder()
    optimizer.apply(content)
    return optimizer
}

class DslSolverConfigBuilder : HashMap<String, String>()

class DslSolverBuilder(logic: String?) {
    internal val context by lazy {
        Context(configBuilder)
    }
    internal val solver by lazy { if (logic == null) context.mkSolver() else context.mkSolver(logic) }
    private val configBuilder = DslSolverConfigBuilder()
    private var checkSuccessful = false

    fun config (content: DslSolverConfigBuilder.() -> Unit) {
        configBuilder.apply(content)
    }

    fun bvVar (name:String, size:Int) : BitVecExpr =
        context.mkConst(context.mkSymbol(name), context.mkBitVecSort(size)) as BitVecExpr

    fun bvConst(context: Context, value: Int, size:Int) = context.mkBV(value, size)

    fun check(cb:(Status) -> Unit): DslSolverBuilder {
        val status = solver.check()
        cb.invoke(status)
        checkSuccessful = status.toInt() == 1
        return this
    }

    fun onSuccess(cb: ((Model, Context) -> Unit)):DslSolverBuilder {
        if (checkSuccessful) {
            cb.invoke(solver.model, context)
        }
        return this
    }

    fun subtract(first: ArithExpr<*>, second: ArithExpr<*>): ArithExpr<ArithSort> {
        return context.mkSub(first, second)
    }

    fun e(first: Expr<*>, second:  Expr<*>): BoolExpr? {
        return context.mkEq(first, second)
    }

    infix fun ArithExpr<out ArithSort>.UGE(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkGe(this, second)
    }

    // >
    infix fun ArithExpr<out ArithSort>.GT(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkGt(this, second)
    }


    infix fun BitVecExpr.SGE(second: BitVecExpr): BoolExpr? {
        return context.mkBVSGE(this, second)
    }

    infix fun BitVecExpr.UGE(second: BitVecExpr): BoolExpr? {
        return context.mkBVUGE(this, second)
    }

    infix fun ArithExpr<*>.`=`(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkEq(this, second)
    }

    infix fun ArithExpr<*>.`=`(second: Int): BoolExpr? {
        return context.mkEq(this, context.mkInt(second))
    }

    infix fun BitVecExpr.`=`(second: BitVecExpr): BoolExpr? {
        return context.mkEq(this, second)
    }

    infix fun BoolExpr?.OR(second: BoolExpr?): BoolExpr? {
        return context.mkOr(this, second)
    }

    fun intConst(i: Int): ArithExpr<out ArithSort> {
        return context.mkInt(i)
    }

    fun add(vararg expr: BoolExpr?) {
        return solver.add(*expr)
    }

    fun fr(numerator:Int, denominator:Int): ArithExpr<*> {
        return context.mkReal(numerator, denominator)
    }

    fun realVar (name: String) : ArithExpr<out ArithSort> {
        return context.mkConst(context.mkSymbol(name), context.realSort) as ArithExpr<out ArithSort>
    }

    fun realVar (vararg names: String) : List<ArithExpr<out ArithSort>> {
        return names.
        map { n ->
            context.mkConst(context.mkSymbol(n), context.realSort) as ArithExpr<out ArithSort>
        }.toList()
    }

    fun intVar (name: String) : ArithExpr<out ArithSort> {
        return context.mkConst(context.mkSymbol(name), context.intSort) as ArithExpr<out ArithSort>
    }

    fun intVar (vararg names: String) : List<ArithExpr<out ArithSort>> {
        return names.
        map { n ->
            context.mkConst(context.mkSymbol(n), context.intSort) as ArithExpr<out ArithSort>
        }.toList()
    }


    operator fun Int.times(second: ArithExpr<out ArithSort>) = context.mkMul(context.mkInt(this), second)
    operator fun ArithExpr<*>.times(second: ArithExpr<out ArithSort>) = context.mkMul(this, second)

    operator fun Expr<BitVecSort>.times(second: Expr<BitVecSort>) = context.mkBVMul(this, second)
    operator fun Expr<BitVecSort>.div(second: Expr<BitVecSort>) = context.mkBVMul(this, second)
    operator fun Expr<BitVecSort>.plus(second: Expr<BitVecSort>) = context.mkBVAdd(this, second)

    operator fun ArithExpr<*>.minus(second: ArithExpr<out ArithSort>) = context.mkSub(this, second)
    operator fun ArithExpr<*>.plus(second: ArithExpr<out ArithSort>) = context.mkAdd(this, second)
    operator fun ArithExpr<*>.unaryMinus() = context.mkUnaryMinus(this)
}

class DslOptimizerBuilder () {
    internal val context by lazy {
        Context(configBuilder)
    }
    internal val solver by lazy { context.mkOptimize() }
    private val configBuilder = DslSolverConfigBuilder()
    private var checkSuccessful = false

    fun config (content: DslSolverConfigBuilder.() -> Unit) {
        configBuilder.apply(content)
    }

    fun s (string:String) : ArithExpr<out ArithSort> {
        return context.mkConst(context.mkSymbol(string), context.intSort) as ArithExpr<*>
    }

    fun s (vararg strings:String) : List<ArithExpr<out ArithSort>> {
        return strings.
        map { s ->  context.mkConst(context.mkSymbol(s), context.intSort) as ArithExpr<out ArithSort>}.
        toList()
    }

    fun check(cb:(Status) -> Unit): DslOptimizerBuilder {
        val status = solver.Check()
        cb.invoke(status)
        checkSuccessful = status.toInt() == 1
        return this
    }

    fun onSuccess(cb: ((Model, Context) -> Unit)):DslOptimizerBuilder {
        if (checkSuccessful) {
            cb.invoke(solver.model, context)
        }
        return this
    }

    fun subtract(first: ArithExpr<*>, second: ArithExpr<*>): ArithExpr<ArithSort> {
        return context.mkSub(first, second)
    }

    fun e(first: Expr<*>, second:  Expr<*>): BoolExpr? {
        return context.mkEq(first, second)
    }

    infix fun ArithExpr<*>.`=`(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkEq(this, second)
    }

    // >=
    infix fun ArithExpr<out ArithSort>.GE(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkGe(this, second)
    }

    // >=
    infix fun ArithExpr<out ArithSort>.GT(second: ArithExpr<out ArithSort>): BoolExpr? {
        return context.mkGt(this, second)
    }

    infix fun ArithExpr<*>.`=`(second: Int): BoolExpr? {
        return context.mkEq(this, context.mkInt(second))
    }

    fun i(i: Int): ArithExpr<out ArithSort> {
        return context.mkInt(i)
    }

    fun add(vararg expr: BoolExpr?) {
        return solver.Add(*expr)
    }

    fun minimize(expr: ArithExpr<out ArithSort>) : Optimize.Handle<out ArithSort>{
        return solver.MkMinimize(expr)
    }

    operator fun Int.times(second: ArithExpr<out ArithSort>) = context.mkMul(context.mkInt(this), second)
    operator fun ArithExpr<*>.times(second: ArithExpr<out ArithSort>) = context.mkMul(this, second)
    operator fun ArithExpr<*>.minus(second: ArithExpr<out ArithSort>) = context.mkSub(this, second)
    operator fun ArithExpr<*>.plus(second: ArithExpr<out ArithSort>) = context.mkAdd(this, second)
    operator fun ArithExpr<*>.unaryMinus() = context.mkUnaryMinus(this)
}


