package com.karaageumai.workmanagement.view.resister.salary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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
import com.karaageumai.workmanagement.view.resister.salary.ressetter.HealthInsuranceFeeViewData
import com.karaageumai.workmanagement.view.resister.salary.ressetter.PensionDataInputViewData
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
    private lateinit var mCheckResult: CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE

    // DBに登録するデータ
    lateinit var mSalaryInfo: SalaryInfo
    // 新規作成かどうかを判定するフラグ
    private var mIsNewEntry: Boolean = true

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


        // タブ関連の処理
        mViewPager = findViewById(R.id.view_pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mViewPager.adapter = pagerAdapter

        // タブレイアウトの読み込み
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, mViewPager) { tab, position ->
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
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = WorkStatusInputFragment.newInstance(mSalaryInfo, mIsNewEntry)
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_WORK_STATUS] = fragment
                    return fragment
                }

                PAGE_OF_INCOME -> {
                    Log.i("create IncomeInputFragment()")
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = IncomeInputFragment.newInstance(mSalaryInfo, mIsNewEntry)
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_INCOME] = fragment
                    return fragment
                }

                PAGE_OF_DEDUCTION -> {
                    Log.i("create DeductionInputFragment()")
                    // 表示する項目を定義するArray
                    val inputViewArray = arrayOf(HealthInsuranceFeeViewData, PensionDataInputViewData)
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(mSalaryInfo, mIsNewEntry, inputViewArray, R.layout.fragment_deduction_input)
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_DEDUCTION] = fragment
                    return fragment
                }

                else -> {
                    return WorkStatusInputFragment()
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
        // SalaryInfoを更新
        Log.i("mSalaryInfo is update")
        mSalaryInfo = aSalaryInfoObservable.getSalaryInfo()

        // Todo : ここで入力完了フラグのチェックを行う
        updateIsComplete()

        // 他のタブに変更を反映させる
        for ((_, value) in mChildFragmentMap){
            if(aSalaryInfoObservable != value) {
                value.refreshSalaryInfo(mSalaryInfo)
            }
        }
    }

    private fun saveData() {
        if(!mSalaryInfo.isComplete) {
            Log.i("this data is not complete. app can not save this data.")
            return
        }
        when (mCheckResult) {
            CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY -> {
                mModelFacade.insertSalaryInfo(mSalaryInfo)
            }

            CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_ALREADY_EXIST -> {
                mModelFacade.updateSalaryInfo(mSalaryInfo)
            }

            else -> return
        }
    }

    private fun updateIsComplete() {

        // Todo：入力状態に応じて、mSalaryInfo.isCompleteを更新する


    }
}