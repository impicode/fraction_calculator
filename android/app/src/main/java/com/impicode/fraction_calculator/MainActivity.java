package com.impicode.fraction_calculator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;

import io.github.kexanie.library.MathView;

public class MainActivity extends AppCompatActivity {
    //TODO dodac scrolla
    //TODO dodać loga
    Button buttonCompute;
    ColorStateList defaultTextViewColors;
    ColorStateList defaultEditTextColors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCompute = findViewById(R.id.button_compute);

        buttonCompute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                computeAndShowResult();
            }
        });
        defaultTextViewColors = ((TextView)findViewById(R.id.text_result)).getTextColors();
        defaultEditTextColors = getEditTextControl(R.id.edit_right_denominator).getTextColors();

        getEditTextControl(R.id.edit_right_denominator).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    computeAndShowResult();
                    return true;
                }
                return false;
            }
        });
    }


    public static class Fraction {
        public BigInteger numerator;
        public BigInteger denominator;

        public Fraction(BigInteger n, BigInteger d) {
            numerator = n;
            denominator = d;
        }
    }


    private static BigInteger GCD(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return a;
        }
        return GCD(b, a.mod(b));
    }

    public static BigInteger LCM(BigInteger a, BigInteger b) {
        return a.multiply(b).divide(GCD(a, b));
    }

    private String getEditText(int id) {
        EditText editText = findViewById(id);
        String ret = editText.getText().toString();
        return ret.equals("")
            ? editText.getHint().toString()
            : ret;
    }

    private EditText getEditTextControl(int id) {
        return ((EditText)findViewById(id));
    }

    private void makeBold(int id) {
        ((EditText)findViewById(id)).setTextColor(getResources().getColor(R.color.color_red));
    }

    private void setAlertMessage(String message) {
        TextView result = findViewById(R.id.text_result);
        result.setVisibility(View.VISIBLE);
        result.setTextColor(getResources().getColor(R.color.color_red));
        result.setText(message);
    }

    private void computeAndShowResult() {
        hideKeyboard();
        cleanUIState();
        Fraction left = buildFraction(
                getEditText(R.id.edit_left_numerator), getEditText(R.id.edit_left_denominator));
        Fraction right = buildFraction(
                getEditText(R.id.edit_right_numerator), getEditText(R.id.edit_right_denominator));
        if (left.denominator.equals(BigInteger.ZERO) || right.denominator.equals(BigInteger.ZERO)) {
            if (left.denominator.equals(BigInteger.ZERO)) {
                makeBold(R.id.edit_left_denominator);
            }
            if (right.denominator.equals(BigInteger.ZERO)) {
                makeBold(R.id.edit_right_denominator);
            }
            setAlertMessage(getResources().getString(R.string.cannot_divide_by_zero));
            return;
        }

        String resultMsg = "";
        BigInteger commonDenominator = LCM(left.denominator, right.denominator);

        resultMsg += printFraction(left) + "+" + printFraction(right);
        resultMsg += "=";
        BigInteger leftFactor = commonDenominator.divide(left.denominator);
        BigInteger rightFactor = commonDenominator.divide(right.denominator);
        if (!leftFactor.equals(BigInteger.ONE) || !rightFactor.equals(BigInteger.ONE)) {
            resultMsg += printFactor(left, leftFactor) + "+" +
                    printFactor(right, rightFactor);
            left.numerator = left.numerator.multiply(leftFactor);
            right.numerator = right.numerator.multiply(rightFactor);
            left.denominator = commonDenominator;
            right.denominator = commonDenominator;
            resultMsg += "=";
            resultMsg += printFraction(left) + "+" + printFraction(right);
            resultMsg += " = ";
        }

        resultMsg += printFraction(left.numerator + " + " + right.numerator, commonDenominator + "");
        Fraction result = new Fraction(left.numerator.add(right.numerator), commonDenominator);
        resultMsg += "=";
        resultMsg += printFraction(result);
        BigInteger gcd = GCD(result.denominator, result.numerator);
        if (gcd.compareTo(BigInteger.ONE) > 0 && !result.numerator.equals(BigInteger.ZERO)) {
            resultMsg += " = " + printFraction(result.numerator + "\\div " + getColored(gcd),
                    result.denominator + "\\div " + getColored(gcd));
            result.numerator = result.numerator.divide(gcd);
            result.denominator = result.denominator.divide(gcd);
            resultMsg += " = " + printFraction(result);
        }

        if (result.numerator.equals(BigInteger.ZERO)) {
            resultMsg += " = 0";
        } else {
            if (result.denominator.compareTo(result.numerator) <= 0) {
                BigInteger big = result.numerator.divide(result.denominator);
                resultMsg += " = ";
                if (!result.denominator.equals(BigInteger.ONE)) {
                    resultMsg += printFraction(
                            big + "\\cdot " + getColored(result.denominator + "") + " + "
                                    + (result.numerator.subtract(result.denominator.multiply(big))),
                            getColored(result.denominator + ""));
                    resultMsg += " = ";
                }
                resultMsg += big;
                result.numerator = result.numerator.subtract(result.denominator.multiply(big));
                if (!result.numerator.equals(BigInteger.ZERO)) {
                    resultMsg += printFraction(result);
                }
            }
        }
        showResultInMathView(resultMsg);
    }

    private void showResultInMathView(String msg) {
        ((MathView)findViewById(R.id.text_result_mathview)).setEngine(MathView.Engine.MATHJAX);
        ((MathView)findViewById(R.id.text_result_mathview)).config(
                "MathJax.Hub.Config({\n"+
                        "  CommonHTML: { linebreaks: { automatic: true } },\n"+
                        "  \"HTML-CSS\": { linebreaks: { automatic: true } },\n"+
                        "         SVG: { linebreaks: { automatic: true } }\n"+
                        "});");
        ((MathView)findViewById(R.id.text_result_mathview)).setText("\\begin{equation}" + msg + "\\end{equation}");

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void cleanUIState() {
        TextView textViewResult = findViewById(R.id.text_result);
        textViewResult.setTextColor(defaultTextViewColors);
        textViewResult.setVisibility(View.INVISIBLE);
        getEditTextControl(R.id.edit_left_numerator).setTextColor(defaultEditTextColors);
        getEditTextControl(R.id.edit_left_denominator).setTextColor(defaultEditTextColors);
        getEditTextControl(R.id.edit_right_numerator).setTextColor(defaultEditTextColors);
        getEditTextControl(R.id.edit_right_denominator).setTextColor(defaultEditTextColors);
    }

    private static String printFactor(Fraction fraction, BigInteger factor) {
        return factor.equals(BigInteger.ONE)
            ? printFraction(fraction)
            : printFraction(
                fraction.numerator + " \\cdot " + getColored(factor),
                fraction.denominator + " \\cdot " + getColored(factor)
        );
    }

    private static String getColored(String s) {
        return "\\color{green}{" + s + "}";
    }


    private static String getColored(BigInteger s) {
        return getColored(s + "");
    }

    private static String printFraction(String left, String right) {
        return "\\frac{" + left + "}{" + right + "}";
    }

    private static String printFraction(BigInteger left, BigInteger right) {
        return printFraction(left.toString(), right.toString());
    }

    private static String printFraction(Fraction f) {
        return printFraction(f.numerator, f.denominator);
    }


    private Fraction buildFraction(String nominator, String denominator) {
        return new Fraction(new BigInteger(nominator), new BigInteger(denominator));
    }
}
