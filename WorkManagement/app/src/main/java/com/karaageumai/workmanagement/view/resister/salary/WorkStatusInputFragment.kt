package com.karaageumai.workmanagement.view.resister.salary

import android.content.Context
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
import androidx.core.content.ContextCompat.getDrawable
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.NumberFormatUtil
import java.lang.NumberFormatException

private const val KEY_SALARY_INFO = "KEY_SALARY_INFO"
private const val KEY_IS_NEW_ENTRY = "KEY_IS_NEW_ENTRY"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkStatusInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkStatusInputFragment() : SalaryInfoObservableFragment() {

    private lateinit var mView: View
    private lateinit var mSalaryInfo: SalaryInfo
    private var mIsNewEntry: Boolean = true

    // 各View
    private lateinit var mSumInfoTextView: TextView
    private lateinit var mWorkingDayEditText: EditText
    private lateinit var mWorkingDayIcon: ImageView
    private lateinit var mWorkingDayErrorTextView: TextView
    private lateinit var mWorkingTimeEditText: EditText
    private lateinit var mWorkingTimeIcon: ImageView
    private lateinit var mWorkingTimeErrorTextView: TextView
    private lateinit var mOvertimeEditText: EditText
    private lateinit var mOvertimeIcon: ImageView
    private lateinit var mOvertimeErrorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Todo : 型チェックが必要
            mSalaryInfo = it.getSerializable(KEY_SALARY_INFO) as SalaryInfo
            mIsNewEntry = it.getBoolean(KEY_IS_NEW_ENTRY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_work_status_input, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    override fun onResume() {
        super.onResume()
        Log.i("onResume()")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param aSalaryInfo
         * @return A new instance of fragment WorkStatusInputFragment.
         */
        @JvmStatic
        fun newInstance(aSalaryInfo: SalaryInfo, aIsNewEntry: Boolean) =
            WorkStatusInputFragment().apply {
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
        Log.i("test3:" + mSalaryInfo.workingDay)
        updateDisplay()
    }

    fun initView() {
        // View紐付け
        mSumInfoTextView = mView.findViewById(R.id.tv_display_sum)
        mWorkingDayEditText = mView.findViewById(R.id.et_workingday)
        mWorkingDayIcon = mView.findViewById(R.id.iv_workingday_check_ic)
        mWorkingDayErrorTextView = mView.findViewById(R.id.tv_workingday_error)
        mWorkingTimeEditText = mView.findViewById(R.id.et_workingtime)
        mWorkingTimeIcon = mView.findViewById(R.id.iv_workingtime_check_ic)
        mWorkingTimeErrorTextView = mView.findViewById(R.id.tv_workingtime_error)
        mOvertimeEditText = mView.findViewById(R.id.et_overtime)
        mOvertimeIcon = mView.findViewById(R.id.iv_overtime_check_ic)
        mOvertimeErrorTextView = mView.findViewById(R.id.tv_overtime_error)

        // アイコンは消しておく
        mWorkingDayIcon.visibility = View.INVISIBLE
        mWorkingTimeIcon.visibility = View.INVISIBLE
        mOvertimeIcon.visibility = View.INVISIBLE

        // エラーメッセージを消しておく
        mWorkingDayErrorTextView.visibility = View.INVISIBLE
        mWorkingTimeErrorTextView.visibility = View.INVISIBLE
        mOvertimeErrorTextView.visibility = View.INVISIBLE

        if(!mIsNewEntry){
            updateDisplay()
        }

        // TextChangedListenerをセット
        mWorkingDayEditText.addTextChangedListener(MyTextWatcher(mWorkingDayEditText))
        mWorkingTimeEditText.addTextChangedListener(MyTextWatcher(mWorkingTimeEditText))
        mOvertimeEditText.addTextChangedListener(MyTextWatcher(mOvertimeEditText))

    }

    // アイコンとエラーメッセージの表示切り替え
    private fun showAndChangeIcon(
            aImageView: ImageView,
            aResId: Int,
            aErrorMessageTextView: TextView,
            isShowMessage: Boolean) {
        val context: Context? = context
        if(context != null) {
            aImageView.setImageDrawable(getDrawable(context, aResId))
            aImageView.visibility = View.VISIBLE
        }
        aErrorMessageTextView.visibility = if (isShowMessage) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    // 表示データの最新化
    private fun updateDisplay() {
        // 勤務日数
        mWorkingDayEditText.setText(mSalaryInfo.workingDay.toString())
        // 勤務時間
        mWorkingTimeEditText.setText(mSalaryInfo.workingTime.toString())
        // 残業
        mOvertimeEditText.setText(mSalaryInfo.overtime.toString())
        // 合計を更新
        updateSum()
    }

    private fun updateSum() {
        // 合計を更新
        val sum: Double = mSalaryInfo.workingTime + mSalaryInfo.overtime
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
            when(mEditText.id) {
                R.id.et_workingday -> {
                    val workingDay = s.let {
                        try {
                            // 0.5単位だったら有効
                            val temp: Double = it.toString().toDouble()
                            if(NumberFormatUtil.checkNumberFormat05(s.toString())) {
                                if(temp > 31.0) {
                                    // 1ヶ月の最大日数超えていたら無効
                                    showAndChangeIcon(mWorkingDayIcon, R.drawable.ic_baseline_error_24, mWorkingDayErrorTextView, true)
                                    0.0
                                } else {
                                    showAndChangeIcon(mWorkingDayIcon, R.drawable.ic_baseline_check_circle_24, mWorkingDayErrorTextView, false)
                                    temp
                                }
                            } else {
                                showAndChangeIcon(mWorkingDayIcon, R.drawable.ic_baseline_error_24, mWorkingDayErrorTextView, true)
                                0.0
                            }
                        } catch (e: NumberFormatException) {
                            showAndChangeIcon(mWorkingDayIcon, R.drawable.ic_baseline_error_24, mWorkingDayErrorTextView, true)
                            0.0
                        }
                    }
                    mSalaryInfo.workingDay = workingDay
                }

                R.id.et_workingtime -> {
                    val workingTime = s.let {
                        try {
                            // 0.1単位だったら有効
                            val temp: Double = it.toString().toDouble()
                            if(NumberFormatUtil.checkNumberFormat01(s.toString())) {
                                if(temp > 744.0) {
                                    // 1ヶ月の最大日数超えていたら無効
                                    showAndChangeIcon(mWorkingTimeIcon, R.drawable.ic_baseline_error_24, mWorkingTimeErrorTextView, true)
                                    0.0
                                } else {
                                    showAndChangeIcon(mWorkingTimeIcon, R.drawable.ic_baseline_check_circle_24, mWorkingTimeErrorTextView, false)
                                    temp
                                }
                            } else {
                                showAndChangeIcon(mWorkingTimeIcon, R.drawable.ic_baseline_error_24, mWorkingTimeErrorTextView, true)
                                0.0
                            }
                        } catch (e: NumberFormatException) {
                            showAndChangeIcon(mWorkingTimeIcon, R.drawable.ic_baseline_error_24, mWorkingTimeErrorTextView, true)
                            0.0
                        }
                    }
                    mSalaryInfo.workingTime = workingTime
                }

                R.id.et_overtime -> {
                    val overTime = s.let {
                        try {
                            // 0.1単位だったら有効
                            val temp: Double = it.toString().toDouble()
                            if(NumberFormatUtil.checkNumberFormat01(s.toString())) {
                                if(temp > 744.0) {
                                    // 1ヶ月の最大日数超えていたら無効
                                   showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                                    0.0
                                } else {
                                    showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_check_circle_24, mOvertimeErrorTextView, false)
                                    temp
                                }
                            } else {
                                showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                                0.0
                            }
                        } catch (e: NumberFormatException) {
                            showAndChangeIcon(mOvertimeIcon, R.drawable.ic_baseline_error_24, mOvertimeErrorTextView, true)
                            0.0
                        }
                    }
                    mSalaryInfo.overtime = overTime
                }
            }
            notifyObserver()
            updateSum()
        }



    }



}