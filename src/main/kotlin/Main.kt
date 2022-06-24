import dev.na2na.eval.Eval
fun main() {
    val evalSampleStr = "3+(4+4/(6*8))+(8+2-(6+6+(8*2)))"
    val evalSample = Eval.eval(evalSampleStr)

    println(evalSample)
}