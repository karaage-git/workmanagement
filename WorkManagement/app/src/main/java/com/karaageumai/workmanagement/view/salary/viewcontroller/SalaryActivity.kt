package com.karaageumai.workmanagement.view.salary.viewcontroller

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
import com.karaageumai.workmanagement.view.salary.*
import com.karaageumai.workmanagement.view.salary.util.SalaryInfoHelper
import com.karaageumai.workmanagement.view.salary.util.SalaryInfoParcel
import com.karaageumai.workmanagement.view.salary.viewdata.SalaryInputViewTag
import com.karaageumai.workmanagement.view.salary.viewdata.sumview.BaseSalaryDataSumViewData
import com.karaageumai.workmanagement.view.salary.viewdata.sumview.SalarySumViewTag
import java.lang.IllegalArgumentException

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
    private lateinit var mCheckResult: CalendarUtil.Companion.CheckFormatResultCode

    // DBに登録するデータ
    lateinit var mSalaryInfo: SalaryInfo

    lateinit var mTabLayout: TabLayout

    private var mSumTextViewMap: MutableMap<SalarySumViewTag.Tag, TextView> = mutableMapOf()

    private lateinit var mSalaryInfoHelper: SalaryInfoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_salary)
        setSupportActionBar(toolbar)

        // intentからリザルトコードを取り出し、nullチェック、型チェックを行った上で変数に格納する
        mCheckResult =
                (intent.extras?.getSerializable(CheckTargetYearMonthActivity.KEY_CHECK_RESULT) ?: CalendarUtil.Companion.CheckFormatResultCode.ERROR).let { it ->
                    if(it is CalendarUtil.Companion.CheckFormatResultCode) {
                        it
                    } else {
                        CalendarUtil.Companion.CheckFormatResultCode.ERROR
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
            CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_NEW_ENTRY -> {
                // 新規データ
                Log.i("create new SalaryInfo")
                mSalaryInfo = SalaryInfo(0, year, month)
                mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, true)
            }

            CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_ALREADY_EXIST -> {
                // 既存データを取得
                Log.i("get SalaryInfo from DB")
                mModelFacade.selectSalaryInfo(year, month)?.let {
                    mSalaryInfo = it
                    mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, false)
                }
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

    override fun onResume() {
        super.onResume()
        displaySumView()

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

                    val salaryInfoParcelArrayList: Array<SalaryInfoParcel> =
                        mSalaryInfoHelper.getSalaryInfoParcelList(
                            listOf(SalaryInputViewTag.WorkingDayInputViewData,
                                SalaryInputViewTag.WorkingTimeInputViewData,
                                SalaryInputViewTag.OverTimeInputViewData
                            )
                        ).toTypedArray()

                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputFragment.newInstance(
                        salaryInfoParcelArrayList,
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
                    val salaryInfoParcelArrayList: Array<SalaryInfoParcel> =
                        mSalaryInfoHelper.getSalaryInfoParcelList(
                            listOf(
                                SalaryInputViewTag.BaseIncomeInputViewData,
                                SalaryInputViewTag.OverTimeIncomeInputViewData,
                                SalaryInputViewTag.OtherIncomeInputViewData
                            )
                        ).toTypedArray()
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputFragment.newInstance(
                        salaryInfoParcelArrayList,
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
                    val salaryInfoParcelArrayList: Array<SalaryInfoParcel> =
                        mSalaryInfoHelper.getSalaryInfoParcelList(
                            listOf(
                                SalaryInputViewTag.HealthInsuranceInputViewData,
                                SalaryInputViewTag.LongTermCareInsuranceFeeInputViewData,
                                SalaryInputViewTag.PensionInsuranceInputViewData,
                                SalaryInputViewTag.EmploymentInsuranceInputViewData,
                                SalaryInputViewTag.IncomeTaxInputViewData,
                                SalaryInputViewTag.ResidentTaxInputViewData,
                                SalaryInputViewTag.OtherDeductionInputViewData
                            )
                        ).toTypedArray()
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputFragment.newInstance(
                            salaryInfoParcelArrayList,
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
                    throw RuntimeException("$position is illegal page number")
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
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    // mSalaryInfo
    override fun update(aSalaryInfoObservable: SalaryInfoObservableFragment) {
        Log.i("update()")
        val receiveParcels = aSalaryInfoObservable.getSalaryInfoParcelList()

        mSalaryInfoHelper.updateSalaryInfo(receiveParcels)

        displaySumView()
    }

    private fun displaySumView() {
        // 合計値を更新
        mSumTextViewMap[SalarySumViewTag.Tag.WorkStatusSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumWorkTime().toString()
        }
        mSumTextViewMap[SalarySumViewTag.Tag.IncomeSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumIncome().toString()
        }
        mSumTextViewMap[SalarySumViewTag.Tag.DeductionSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumDeduction().toString()
        }
    }

    private fun saveData() {
        if(mSalaryInfoHelper.checkUserInputFinished()){
            mSalaryInfo.isComplete = true
            when (mCheckResult) {
                CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_NEW_ENTRY -> {
                    mModelFacade.insertSalaryInfo(mSalaryInfo)
                }

                CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_ALREADY_EXIST -> {
                    mModelFacade.updateSalaryInfo(mSalaryInfo)
                }
                else -> return
            }
        } else {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    when (mCheckResult) {
                        CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_NEW_ENTRY -> {
                            mModelFacade.insertSalaryInfo(mSalaryInfo)
                        }

                        CalendarUtil.Companion.CheckFormatResultCode.RESULT_OK_ALREADY_EXIST -> {
                            mModelFacade.updateSalaryInfo(mSalaryInfo)
                        }

                        else -> {}
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }
    }
}