package com.karaageumai.workmanagement.view.resister.salary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.salary.SalaryInfo

private const val KEY_SALARY_INFO = "KEY_SALARY_INFO"

/**
 * A simple [Fragment] subclass.
 * Use the [IncomeInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomeInputFragment : SalaryInfoObservableFragment() {

    private lateinit var mView: View
    private lateinit var mSalaryInfo: SalaryInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Todo : 型チェックが必要
            mSalaryInfo = it.getSerializable(KEY_SALARY_INFO) as SalaryInfo

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income_input, container, false)
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
        fun newInstance(aSalaryInfo: SalaryInfo) =
            IncomeInputFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_SALARY_INFO, aSalaryInfo)
                }
            }
    }

    override fun getSalaryInfo(): SalaryInfo {
        TODO("Not yet implemented")
    }


    override fun refreshSalaryInfo(aSalaryInfo: SalaryInfo) {
        mSalaryInfo = aSalaryInfo
        Log.i("test4:" + mSalaryInfo.workingDay)
    }

}