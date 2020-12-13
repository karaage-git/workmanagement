package com.karaageumai.workmanagement.view.resister.salary

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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

    lateinit var mTabLayout: TabLayout

    //Todo フラグがダブっているため見直しが必要
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
                    val inputViewArray: Array<SalaryInputViewTag.Tag> = arrayOf(
                        SalaryInputViewTag.Tag.WorkingDayInputViewData,
                        SalaryInputViewTag.Tag.WorkingTimeInputViewData,
                        SalaryInputViewTag.Tag.OverTimeInputViewData
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        mSalaryInfo,
                        mIsNewEntry,
                        inputViewArray,
                        R.drawable.layout_frame_border_work,
                        R.string.layoutitem_workstatus_top,
                        R.string.layoutitem_workstatus_top_unit
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_WORK_STATUS] = fragment
                    return fragment
                }

                PAGE_OF_INCOME -> {
                    Log.i("create IncomeInputFragment()")
                    val inputViewArray: Array<SalaryInputViewTag.Tag> = arrayOf(
                        SalaryInputViewTag.Tag.BaseIncomeInputViewData,
                        SalaryInputViewTag.Tag.OverTimeIncomeInputViewData,
                        SalaryInputViewTag.Tag.OtherIncomeInputViewData
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        mSalaryInfo,
                        mIsNewEntry,
                        inputViewArray,
                        R.drawable.layout_frame_border_income,
                        R.string.layoutitem_income_top,
                        R.string.layoutitem_income_top_unit
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
                    val inputViewArray: Array<SalaryInputViewTag.Tag> = arrayOf(
                        SalaryInputViewTag.Tag.HealthInsuranceInputViewData,
                        SalaryInputViewTag.Tag.PensionDataInputViewData
                    )
                    // フラグメント生成
                    val fragment: SalaryInfoObservableFragment = SalaryInfoInputBaseFragment.newInstance(
                        mSalaryInfo,
                        mIsNewEntry,
                        inputViewArray,
                        R.drawable.layout_frame_border_deduction,
                        R.string.layoutitem_deduction_top,
                        R.string.layoutitem_deduction_top_unit
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@SalaryActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[PAGE_OF_DEDUCTION] = fragment
                    return fragment
                }

                else -> {
                    return SalaryInfoInputBaseFragment.newInstance(
                        mSalaryInfo,
                        mIsNewEntry,
                        arrayOf(),
                        R.drawable.layout_frame_border_deduction,
                        R.string.layoutitem_deduction_top,
                        R.string.layoutitem_deduction_top_unit
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
        // SalaryInfoを更新
        Log.i("mSalaryInfo is update")
        mSalaryInfo = aSalaryInfoObservable.getSalaryInfo()

        // 他のタブに変更を反映させる
        for ((_, value) in mChildFragmentMap){
            if(aSalaryInfoObservable != value) {
                value.refreshSalaryInfo(mSalaryInfo)
            }
        }
    }

    private fun saveData() {

        val notEnteredItemList: MutableList<SalaryInputViewTag.Tag> = mutableListOf()

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
                SalaryInputViewTag.tagDataMap[tag]?.let {
                    message.append("・")
                    message.append(getString(it.getTitleResId()))
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
}