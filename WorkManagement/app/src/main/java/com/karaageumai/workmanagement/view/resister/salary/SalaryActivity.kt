package com.karaageumai.workmanagement.view.resister.salary

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.view.resister.salary.ressetter.*
import com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview.SalaryInputViewTag
import com.karaageumai.workmanagement.view.resister.salary.ressetter.sumview.BaseSalaryDataSumViewData
import com.karaageumai.workmanagement.view.resister.salary.ressetter.sumview.SalarySumViewTag
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

class SalaryActivity : AppCompatActivity(), SalaryInfoObserverInterface {

    companion object {
        // タブのページ数
        private const val NUM_PAGES = 3
        // 勤怠状況を入力するタブのページ数
        private const val PAGE_OF_WORK_STATUS = 0
        // 収入を入力するタブのページ数
        private const val PAGE_OF_INCOME = 1
        // 控除を入力するタブのページ数
        private const val PAGE_OF_DEDUCTION = 2
    }

    private val mModelFacade: ModelFacade = ModelFacade
    lateinit var mViewPager: ViewPager2
    // 子フラグメントを管理するマップ
    var mChildFragmentMap: MutableMap<Int, SalaryInfoObservableFragment> = mutableMapOf()

    // 新規 or 更新を判定する情報
    private lateinit var mCheckResult: CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE

    // DBに登録するデータ
    lateinit var mSalaryInfo: SalaryInfo

    lateinit var mTabLayout: TabLayout

    //Todo フラグがダブっているため見直しが必要
    // 新規作成かどうかを判定するフラグ
    private var mIsNewEntry: Boolean = true

    private var mSumTextViewMap: MutableMap<SalarySumViewTag.Tag, TextView> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_salary)
        setSupportActionBar(toolbar)

        // intentからリザルトコードを取り出し、nullチェック、型チェックを行った上で変数に格納する
        mCheckResult =
                (intent.extras?.getSerializable(CheckTargetYearMonthActivity.KEY_CHECK_RESULT) ?: CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.ERROR).let { it ->
                    if(it is CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE) {
                        it
                    } else {
                        CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.ERROR
                    }
                }

        // intentから年月を表す文字列を取得する
        val yearMonth: String = intent.extras?.getString(CheckTargetYearMonthActivity.KEY_YEAR_MONTH, "") ?: ""

        // 仮にデータが空 or エラーだった場合はトップメニューに遷移させる
        if (yearMonth.isEmpty()) {
            Log.i("can not get data. go to TopMenu.")
            // 通常はありえないルート
            // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
        }

        // 年月を分割
        val yearMonthPair: Pair<Int, Int> = try {
            CalendarUtil.splitYearMonth(yearMonth)
        } catch (e: IllegalArgumentException) {
            // 通常はありえないルート
            // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
            return
        }

        // 年
        val year: Int = yearMonthPair.first
        // 月
        val month: Int = yearMonthPair.second

        // データを作成
        when (mCheckResult) {
            CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY -> {
                // 新規データ
                Log.i("create new SalaryInfo")
                mSalaryInfo = SalaryInfo(0, year, month)
                mIsNewEntry = true
            }

            CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_ALREADY_EXIST -> {
                // 既存データを取得
                Log.i("get SalaryInfo from DB")
                mModelFacade.selectSalaryInfo(year, month)?.let {
                    mSalaryInfo = it
                }
                mIsNewEntry = false
            }

            else -> {
                // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
            }
        }

        // 合計値を出すためのViewを初期化
        val sumLinearLayout: LinearLayout = findViewById(R.id.ll_sum)
        for (target in SalarySumViewTag.tagMap) {
            val tag: SalarySumViewTag.Tag = target.key
            val data: BaseSalaryDataSumViewData = target.value
            val view: View = layoutInflater.inflate(R.layout.layout_input_sum, sumLinearLayout, false)
            // 背景設定
            val root: LinearLayout = view.findViewById(R.id.ll_sum_root)
            root.setBackgroundResource(data.getBackgroundResId())

            // タイトル設定
            val title: TextView = view.findViewById(R.id.tv_sum_title)
            title.setText(data.getTitleResId())

            // 単位設定
            val unit: TextView = view.findViewById(R.id.tv_sum_unit)
            unit.setText(data.getUnitResId())

            // 値部分をマップに登録
            val value: TextView = view.findViewById(R.id.tv_sum_value)
            mSumTextViewMap[tag] = value

            // 作成したViewを追加
            sumLinearLayout.addView(view)
        }


        // タブ関連の処理
        mViewPager = findViewById(R.id.view_pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mViewPager.adapter = pagerAdapter

        // タブレイアウトの読み込み
        mTabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            when(position) {
                PAGE_OF_WORK_STATUS -> {
                    tab.text = getString(R.string.tab_title_workstatus)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_work_24)
                }
                PAGE_OF_INCOME -> {
                    tab.text = getString(R.string.tab_title_income)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_attach_money_24)
                }
                PAGE_OF_DEDUCTION -> {
                    tab.text = getString(R.string.tab_title_deduction)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_money_off_24)
                }
            }
        }.attach()

        // タッチされないタブは中身がロードされず入力チェックが動かないため、最初に全タブロードする
        // onCreate()で触るのは危険な感じがするが、今のところ問題無し。
        for (i in 0..NUM_PAGES ) mTabLayout.getTabAt(i)?.select()
        // 初期タブの選択
        mTabLayout.getTabAt(0)?.select()

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.i("currentFocus:" + currentFocus.toString())
        return super.dispatchTouchEvent(ev)
    }

    // タブ生成処理
    private inner class ScreenSlidePagerAdapter(mActivity: AppCompatActivity) : FragmentStateAdapter(mActivity) {
        // タブアイテムの数取得
        override fun getItemCount(): Int {
            return NUM_PAGES
        }

        override fun createFragment(position: Int): Fragment {
            when(position) {
                PAGE_OF_WORK_STATUS -> {
                    Log.i("create WorkStatusInputFragment()")
                    // 表示する項目を定義するArray
                    val salaryInfoParcelArrayList: ArrayList<SalaryInfoParcel> = arrayListOf(
                        SalaryInfoParcel(SalaryInputViewTag.WorkingDayInputViewData, mSalaryInfo.workingDay.toString()),
                        SalaryInfoParcel(SalaryInputViewTag.WorkingTimeInputViewData, mSalaryInfo.workingTime.toString()),
                        SalaryInfoParcel(SalaryInputViewTag.OverTimeInputViewData, mSalaryInfo.overtime.toString())
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        salaryInfoParcelArrayList,
                        mIsNewEntry,
                        R.color.work_status_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_WORK_STATUS] = fragment
                    return fragment
                }

                PAGE_OF_INCOME -> {
                    Log.i("create IncomeInputFragment()")
                    val salaryInfoParcelArrayList: ArrayList<SalaryInfoParcel> = arrayListOf(
                        SalaryInfoParcel(SalaryInputViewTag.BaseIncomeInputViewData, mSalaryInfo.salary.toString()),
                        SalaryInfoParcel(SalaryInputViewTag.OverTimeIncomeInputViewData, mSalaryInfo.overtimeSalary.toString()),
                        SalaryInfoParcel(SalaryInputViewTag.OtherIncomeInputViewData, mSalaryInfo.otherIncome.toString())
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        salaryInfoParcelArrayList,
                        mIsNewEntry,
                        R.color.income_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_INCOME] = fragment
                    return fragment
                }

                PAGE_OF_DEDUCTION -> {
                    Log.i("create DeductionInputFragment()")
                    // 表示する項目を定義するArray
                    val salaryInfoParcelArrayList: ArrayList<SalaryInfoParcel> = arrayListOf(
                        SalaryInfoParcel(SalaryInputViewTag.HealthInsuranceInputViewData, mSalaryInfo.healthInsuranceFee.toString()),
                        SalaryInfoParcel(SalaryInputViewTag.PensionDataInputViewData, mSalaryInfo.pensionFee.toString())
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        salaryInfoParcelArrayList,
                        mIsNewEntry,
                        R.color.deduction_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_DEDUCTION] = fragment
                    return fragment
                }

                else -> {
                    // 通常はありえない
                    return SalaryInfoInputBaseFragment.newInstance(
                        arrayListOf(),
                        mIsNewEntry,
                        R.color.work_status_basic
                    )
                }
            }
        }
    }

    // ツールバーのレイアウト反映
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("onCreateOptionsMenu()")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.btn_save_data -> {
                saveData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    // mSalaryInfo
    override fun update(aSalaryInfoObservable: SalaryInfoObservableFragment) {
        Log.i("update()")
        val receiveParcels = aSalaryInfoObservable.getSalaryInfoParcelList()

        refreshSalaryInfo(receiveParcels)

        // 合計値を更新
        mSumTextViewMap[SalarySumViewTag.Tag.WorkStatusSumViewData]?.let {
            val sum = mSalaryInfo.workingTime + mSalaryInfo.overtime
            it.text = sum.toString()
        }
        mSumTextViewMap[SalarySumViewTag.Tag.IncomeSumViewData]?.let {
            val sum = mSalaryInfo.salary + mSalaryInfo.overtimeSalary + mSalaryInfo.otherIncome
            it.text = sum.toString()
        }
        mSumTextViewMap[SalarySumViewTag.Tag.DeductionSumViewData]?.let {
            val sum = mSalaryInfo.healthInsuranceFee + mSalaryInfo.pensionFee
            it.text = sum.toString()
        }
    }

    private fun saveData() {
        val notEnteredItemList: MutableList<SalaryInputViewTag> = mutableListOf()
        for(element in mChildFragmentMap) {
            val fragment = element.value
            notEnteredItemList.addAll(fragment.getNotEnteredInputItemList())
        }

        if(notEnteredItemList.isEmpty()){
            mSalaryInfo.isComplete = true
            when (mCheckResult) {
                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY -> {
                    mModelFacade.insertSalaryInfo(mSalaryInfo)
                }

                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_ALREADY_EXIST -> {
                    mModelFacade.updateSalaryInfo(mSalaryInfo)
                }

                else -> return
            }
        } else {
            Log.i("this data is not complete. app can not save this data.")
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.dialog_title)

            val message: StringBuilder = StringBuilder()
            for (tag in notEnteredItemList) {
                if(message.isNotEmpty()) {
                    message.append("\n")
                }

            }
            alertDialog.setMessage(message)
            alertDialog.setPositiveButton(R.string.ok, DialogInterface.OnClickListener{ dialog, _ ->
                dialog.dismiss()
            })
            alertDialog.show()
            return
        }
    }

    private fun refreshSalaryInfo(aList: List<SalaryInfoParcel>) {
        for (parcel in aList){
            when(parcel.mTag){
                SalaryInputViewTag.WorkingDayInputViewData -> {
                    mSalaryInfo.workingDay = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.WorkingTimeInputViewData -> {
                    mSalaryInfo.workingTime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.OverTimeInputViewData -> {
                    mSalaryInfo.overtime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.BaseIncomeInputViewData -> {
                    mSalaryInfo.salary = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OverTimeIncomeInputViewData -> {
                    mSalaryInfo.overtimeSalary = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OtherIncomeInputViewData -> {
                    mSalaryInfo.otherIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.HealthInsuranceInputViewData -> {
                    mSalaryInfo.healthInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.PensionDataInputViewData -> {
                    mSalaryInfo.pensionFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

            }
        }
    }

}