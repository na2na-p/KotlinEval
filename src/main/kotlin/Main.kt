import dev.na2na.eval.Eval
fun main() {
    val evalSampleStr = "5*(-1-2)"
    val evalSample = Eval.eval(evalSampleStr)

    println(evalSample)
}