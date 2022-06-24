package dev.na2na.eval

object Eval {
    private val num_list = ArrayList<Double>()
    private val ope_list = ArrayList<Char>()
    private var answer: Double = 0.0

    // リスト初期化
    private fun listInit() {
        num_list.clear()
        ope_list.clear()
    }

    // ()で囲まれた部分を一つのかたまりと見る。

    private fun bracketAnalysis(formula: String): Array<String> {
        val sequenceList = formula.toCharArray()
        // 1+2*(5-4)は 1 + 2 * 5-4に分離する
        var formulaArray: Array<String> = arrayOf("")
        var i = 0
        var j: Int
        while (i < sequenceList.size) {
            if (sequenceList[i] == '(') {
                var startBracket = 0
                var endBracket = 0
                j = i
                while (startBracket != endBracket || endBracket == 0) {
                    // "("を追加で見つけた回数をカウントする
                    if (sequenceList[j] == '(') {
                        startBracket++
                    }
                    if (sequenceList[j] == ')') {
                        endBracket++
                    }
                    j++
                }
                j--
                formulaArray = formulaArray.plus(formula.substring(i, j + 1))
                i = j + 1
            } else {
                formulaArray = formulaArray.plus(sequenceList[i].toString())
                i++
            }
        }
        return formulaArray
    }

    // 文字列で渡された式を、数値と演算子に分割する。
    private fun lexicalAnalysis(formula: String) {
        val sequenceList = formula.toCharArray()
        var setNumStr = ""
        for (token in sequenceList) {
            if ("+-*/".indexOf(token) > -1) {
                ope_list.add(token)
                num_list.add(setNumStr.toDouble())
                setNumStr = ""
            } // 0-9と小数点も考慮する
            else if ("0123456789.".indexOf(token) > -1) {
                // setNumStrに一文字追加する
                setNumStr += token
            }
        }
        // setNumStrの末尾が.の場合、0を追加する
        if (setNumStr.last() == '.') {
            setNumStr += "0"
        }
        num_list.add(setNumStr.toDouble())

        // System.out.println("num:" + num_list + " / ope:" + ope_list);
    }

    // 演算子の優先順位を考慮して計算する。
    private fun calculation(): Double {
        // 足し算、引き算、掛け算、割り算を行う際に使用する共通の変数
        var num1Num: Double
        var num2Num: Double
        var setNum: Double
        var num1Id: Int
        var num2Id: Int

        // 掛け算、割り算を行う
        var hitId: Int
        var hitCount = 0
        for (token in ope_list) {
            if (token == '*' || token == '/') {
                hitId = ope_list.indexOf(if (token == '*') '*' else '/')
                ope_list[hitId] = '@'
                num1Id = hitId - hitCount
                num2Id = hitId - hitCount + 1
                // "-"を含む数を検出したら、除去する
                if (num_list[num1Id] < 0) {
                    num_list[num1Id] = -num_list[num1Id]
                }
                if (num_list[num2Id] < 0) {
                    num_list[num2Id] = -num_list[num2Id]
                }

                num1Num = num_list[num1Id]
                num2Num = num_list[num2Id]
                setNum = if (token == '*') num1Num * num2Num else num1Num / num2Num
                num_list[num1Id] = setNum
                num_list.removeAt(num2Id)
                hitCount++
            }
        }
        // for文内では'*'と'/'削除できないため、for文内では'@'に置換しここでまとめて削除している。
        while (ope_list.remove('@')) { /* ループ内では何もしない */
        }

        // 足し算、割り算を行う
        for (token in ope_list) {
            num1Id = 0
            num2Id = 1
            num1Num = num_list[num1Id]
            num2Num = num_list[num2Id]
            setNum = if (token == '+') num1Num + num2Num else num1Num - num2Num
            num_list[num1Id] = setNum
            num_list.removeAt(num2Id)
        }

        // System.out.println("num:" + num_list + " / ope:" + ope_list);
        return num_list[0]
    }

    fun eval(formula: String): Double {
        listInit()
        // formulaに"("が含まれている場合、bracketAnalysis(formula)を実行する
        val formulas = bracketAnalysis(formula)
        // formulasの要素中に()がある場合は再帰的に計算する。
        // formulasは、["", "1", "+", "2", "*", "(6-4)"]のようになっている。
        // "(6-4)"を計算結果で置換する。
        for (f in formulas) {
            if (f.indexOf('(') > -1) {
                formulas[formulas.indexOf(f)] =
                        // 先頭と末尾の()を取り除く
                    eval(f.substring(1, f.length - 1)).toString()
            }
        }
        // formulasを文字列に戻す
        var revFormula = ""
        for (formula_ in formulas) {
            revFormula += formula_
        }
        listInit()
        // revFormulaの中で、-と他の演算子が連続していた場合、置換する。
        // +と-が連続していた場合
        while(revFormula.indexOf("+-") > -1 || revFormula.indexOf("-+") > -1) {
            revFormula = revFormula.replace("+-", "-")
            revFormula = revFormula.replace("-+", "-")
        }
        // *と/が連続していた場合
        while(revFormula.indexOf("*/") > -1 || revFormula.indexOf("/*") > -1) {
            revFormula = revFormula.replace("*/", "/")
            revFormula = revFormula.replace("/*", "/")
        }


        lexicalAnalysis(revFormula)
        answer = 0.0
        answer = calculation()
        // answerをうまく丸める
        answer = answer * 100000000.0.toInt().toDouble() / 100000000.0
        return this.answer
    }
}