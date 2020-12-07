package com.karaageumai.workmanagement.view.resister.salary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.transition.MaterialSharedAxis
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.resister.InputCheckIconChanger
import java.lang.NumberFormatException

private const val KEY_SALARY_INFO = "KEY_SALARY_INFO"
private const val KEY_IS_NEW_ENTRY = "KEY_IS_NEW_ENTRY"
private const val MAX_INCOME = 1000000000

/**
 * A simple [Fragment] subclass.
 * Use the [IncomeInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomeInputFragment : SalaryInfoObservableFragment(), InputCheckIconChanger {

    private lateinit var mView: View
    private lateinit var mSalaryInfo: SalaryInfo
    private var mIsNewEntry: Boolean = true

    // 各View
    private lateinit var mSumInfoTextView: TextView
    private lateinit var mBaseEditText: EditText
    private lateinit var mBaseIcon: ImageView
    private lateinit var mBaseErrorTextView: TextView
    private lateinit var mOvertimeEditText: EditText
    private lateinit var mOvertimeIcon: ImageView
    private lateinit var mOvertimeErrorTextView: TextView
    private lateinit var mOtherEditText: EditText
    private lateinit var mOtherIcon: ImageView
    private lateinit var mOtherErrorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSalaryInfo = it.getSerializable(KEY_SALARY_INFO) as SalaryInfo
            mIsNewEntry = it.getBoolean(KEY_IS_NEW_ENTRY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_income_input, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    override fun onResume() {
        super.onResume()


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param aSalaryInfo
         * @return A new instance of fragment IncomeInputFragment.
         */
        @JvmStatic
        fun newInstance(aSalaryInfo: SalaryInfo, aIsNewEntry: Boolean) =
            IncomeInputFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_SALARY_INFO, aSalaryInfo)
                    putBoolean(KEY_IS_NEW_ENTRY, aIsNewEntry)
                }
            }
    }

    override fun getSalaryInfo(): SalaryInfo {
        return mSalaryInfo
    }


    override fun refreshSalaryInfo(aSalaryInfo: SalaryInfo) {
        mSalaryInfo = aSalaryInfo
    }

    fun initView() {
        // View紐付け
        mSumInfoTextView = mView.findViewById(R.id.tv_display_sum)
        mBaseEditText = mView.findViewById(R.id.et_base)
        mBaseIcon = mView.findViewById(R.id.iv_base_check_ic)
        mBaseErrorTextView = mView.findViewById(R.id.tv_base_error)
        mOvertimeEditText = mView.findViewById(R.id.et_overtime)
        mOvertimeIcon = mView.findViewById(R.id.iv_overtime_check_ic)
        mOvertimeErrorTextView = mView.findViewById(R.id.tv_overtime_error)
        mOtherEditText = mView.findViewById(R.id.et_other)
        mOtherIcon = mView.findViewById(R.id.iv_other_check_ic)
        mOtherErrorTextView = mView.findViewById(R.id.tv_other_error)

        // アイコンは消しておく
        mBaseIcon.visibility = View.INVISIBLE
        mOvertimeIcon.visibility = View.INVISIBLE
        mOtherIcon.visibility = View.INVISIBLE

        // エラーメッセージを消しておく
        mBaseErrorTextView.visibility = View.INVISIBLE
        mOvertimeErrorTextView.visibility = View.INVISIBLE
        mOtherErrorTextView.visibility = View.INVISIBLE

        if(!mIsNewEntry){
            updateDisplay()
        }

        // TextChangedListenerをセット
        mBaseEditText.addTextChangedListener(MyTextWatcher(mBaseEditText))
        mOvertimeEditText.addTextChangedListener(MyTextWatcher(mOvertimeEditText))
        mOtherEditText.addTextChangedListener(MyTextWatcher(mOtherEditText))

    }


    // 表示データの最新化
    private fun updateDisplay() {
        // 基本給
        mBaseEditText.setText(mSalaryInfo.salary.toString())
        // 残業手当
        mOvertimeEditText.setText(mSalaryInfo.overtimeSalary.toString())
        // その他
        mOtherEditText.setText(mSalaryInfo.otherIncome.toString())
        // 合計を更新
        updateSum()
    }

    private fun updateSum() {
        // 合計を更新
        val sum: Int = mSalaryInfo.salary + mSalaryInfo.overtimeSalary + mSalaryInfo.otherIncome
        mSumInfoTextView.setText(sum.toString())
    }


    inner class MyTextWatcher(aEditText: EditText) : TextWatcher {

        private var mEditText: EditText = aEditText

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 何もしない
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 何もしない
        }

        override fun afterTextChanged(s: Editable?) {
            when (mEditText.id) {
                R.id.et_base -> {
                    val baseIncome = s.let {
                        try {
                            val temp: Int = it.toString().toInt()
                            if (NumberFormatUtil.checkNaturalNumberFormat(s.toString())) {
                                if (temp > MAX_INCOME) {
                                    // 大きな値が入ってきたら無効
                                    showAndChangeIcon(mBaseIcon, R.drawable.ic_baseline_error_24, mBaseErrorTextView, true)
                                    0
                                } else {
                                    showAndChangeIcon(mBaseIcon, R.drawable.ic_baseline_check_circle_24, mBaseErrorTextView, false)
                                    temp
                                }
                            } else {
                                // 整数でなければ無効
                                showAndChangeIcon(mBaseIcon, R.drawable.ic_baseline_error_24, mBaseErrorTextView, true)
                                0
                            }
                        } catch (e: NumberFormatException) {
                            // 数値でなければ無効
                            showAndChangeIcon(mBaseIcon, R.drawable.ic_baseline_error_24, mBaseErrorTextView, true)
                            0
                        }
                    }
                    mSalaryInfo.salary = baseIncome
                }

                R.id.et_overtime -> {
                    val overtimeSalary = s.let {
                        if (s?.length == 0) {
                            showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_check_circle_24, mOtherErrorTextView, false)
                            0
                        } else {
                            try {
                                val temp: Int = it.toString().toInt()
                                if (NumberFormatUtil.checkNaturalNumberFormat(s.toString())) {
                                    if (temp > MAX_INCOME) {
                                        // 大きな値が入ってきたら無効
                                        showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                                        0
                                    } else {
                                        showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_check_circle_24, mOvertimeErrorTextView, false)
                                        temp
                                    }
                                } else {
                                    // 整数でなければ無効
                                    showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                                    0
                                }
                            } catch (e: NumberFormatException) {
                                // 数値でなければ無効
                                showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                                0
                            }
                        }
                    }
                    mSalaryInfo.overtimeSalary = overtimeSalary
                }

                R.id.et_other -> {
                    val other = s.let {
                        if (s?.length == 0) {
                            showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_check_circle_24, mOtherErrorTextView, false)
                            0
                        } else {
                            try {
                                val temp: Int = it.toString().toInt()
                                if (NumberFormatUtil.checkNaturalNumberFormat(s.toString())) {
                                    if (temp > MAX_INCOME) {
                                        // 大きな値が入ってきたら無効
                                        showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_error_24, mOtherErrorTextView, true)
                                        0
                                    } else {
                                        showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_check_circle_24, mOtherErrorTextView, false)
                                        temp
                                    }
                                } else {
                                    // 整数でなければ無効
                                    showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_error_24, mOtherErrorTextView, true)
                                    0
                                }
                            } catch (e: NumberFormatException) {
                                // 数値でなければ無効
                                showAndChangeIcon(mOtherIcon, R.drawable.ic_baseline_error_24, mOtherErrorTextView, true)
                                0
                            }
                        }
                    }
                    mSalaryInfo.otherIncome = other
                }
            }
            notifyObserver()
            updateSum()
        }
    }
}