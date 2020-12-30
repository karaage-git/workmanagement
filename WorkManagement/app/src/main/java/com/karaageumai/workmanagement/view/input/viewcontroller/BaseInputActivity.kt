package com.karaageumai.workmanagement.view.input.viewcontroller

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.view.common.viewcontroller.*
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.SumViewResData
import com.karaageumai.workmanagement.view.input.viewdata.SumViewTag

/**
 * 情報入力を行うActivityのベース
 */
abstract class BaseInputActivity : AppCompatActivity(), InputInfoObserverInterface{
    // 表示するタブの種類と順番を管理するリスト（インデックスがタブのページに相当）
    private var mTabPageList:List<TabPage> = listOf()
    // タブ用のViewPager2
    private lateinit var mViewPager: ViewPager2
    // タブレイアウト
    private lateinit var mTabLayout: TabLayout
    // 子フラグメントを管理するマップ
    var mChildFragmentMap: MutableMap<Int, InputInfoObservableFragment> = mutableMapOf()
    // 合計を表示するViewを管理するマップ
    private var mSumViewMap: MutableMap<SumViewTag, TextView> = mutableMapOf()

    enum class TabPage {
        WorkStatus,
        Income,
        Deduction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        // 小クラスの初期化
        init()

        // ツールバー設定
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_salary)
        setSupportActionBar(toolbar)

        // 表示する合計値Viewのタグリスト
        val sumViewTagList: List<SumViewTag> = getSumViewTagList()
        // 合計値を出すためのViewを初期化
        val sumLinearLayout: LinearLayout = findViewById(R.id.ll_sum)
        for (tag in sumViewTagList) {
            val data = SumViewResData(tag)
            val view: View = layoutInflater.inflate(R.layout.layout_input_sum, sumLinearLayout, false)
            // 背景設定
            val root: LinearLayout = view.findViewById(R.id.ll_sum_root)
            root.setBackgroundResource(data.mBackgroundResId)

            // タイトル設定
            val title: TextView = view.findViewById(R.id.tv_sum_title)
            title.setText(data.mTitleResId)

            // 単位設定
            val unit: TextView = view.findViewById(R.id.tv_sum_unit)
            unit.setText(data.mUnitResId)

            // 値部分をマップに登録
            val value: TextView = view.findViewById(R.id.tv_sum_value)
            mSumViewMap[tag] = value

            // 作成したViewを追加
            sumLinearLayout.addView(view)
        }

        // 表示するタブの種類を特定するためのリスト
        mTabPageList = getTabPageList()
        // タブ関連の処理
        mViewPager = findViewById(R.id.view_pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mViewPager.adapter = pagerAdapter
        // タブレイアウトの読み込み
        mTabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            when(mTabPageList[position]) {
                TabPage.WorkStatus -> {
                    tab.text = getString(R.string.tab_title_workstatus)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_work_24)
                }
                TabPage.Income -> {
                    tab.text = getString(R.string.tab_title_income)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_attach_money_24)
                }
                TabPage.Deduction -> {
                    tab.text = getString(R.string.tab_title_deduction)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_money_off_24)
                }
            }
        }.attach()

        // タッチされないタブは中身がロードされず入力チェックが動かないため、最初に全タブロードする
        for (i in 0..mTabPageList.lastIndex ) mTabLayout.getTabAt(i)?.select()
        // 初期タブの選択
        mTabLayout.getTabAt(0)?.select()

    }

    override fun onResume() {
        super.onResume()
        // 合計値を最新化
        updateSumView()
    }

    // タブ生成処理
    private inner class ScreenSlidePagerAdapter(mActivity: AppCompatActivity) : FragmentStateAdapter(mActivity) {
        // タブアイテムの数取得
        override fun getItemCount(): Int {
            return mTabPageList.size
        }

        override fun createFragment(position: Int): Fragment {
            when(mTabPageList[position]) {
                TabPage.WorkStatus -> {
                    Log.i("create WorkStatusInputFragment()")
                    // 表示する項目を定義するArray
                    val inputInfoParcelArrayList: Array<InputInfoParcel> = getWorkStatusInputInfoParcelList().toTypedArray()

                    // フラグメント生成
                    val fragment: InputInfoObservableFragment = BaseInputFragment.newInstance(
                            inputInfoParcelArrayList,
                            R.color.work_status_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@BaseInputActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[mTabPageList.indexOf(TabPage.WorkStatus)] = fragment
                    return fragment
                }

                TabPage.Income -> {
                    Log.i("create IncomeInputFragment()")
                    val inputInfoParcelArrayList: Array<InputInfoParcel> = getIncomeInputInfoParcelList().toTypedArray()
                    // フラグメント生成
                    val fragment: InputInfoObservableFragment = BaseInputFragment.newInstance(
                            inputInfoParcelArrayList,
                            R.color.income_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@BaseInputActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[mTabPageList.indexOf(TabPage.Income)] = fragment
                    return fragment
                }

                TabPage.Deduction -> {
                    Log.i("create DeductionInputFragment()")
                    // 表示する項目を定義するArray
                    val inputInfoParcelArrayList: Array<InputInfoParcel> = getDeductionInputInfoParcelList().toTypedArray()
                    // フラグメント生成
                    val fragment: InputInfoObservableFragment = BaseInputFragment.newInstance(
                            inputInfoParcelArrayList,
                            R.color.deduction_basic
                    )
                    // SalaryInfoのオブザーバーをセット
                    fragment.addObserver(this@BaseInputActivity)
                    // マップにフラグメントを紐付け
                    mChildFragmentMap[mTabPageList.indexOf(TabPage.Deduction)] = fragment
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

    // ツールバー操作の定義
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.btn_save_data -> {
                saveData()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getSumViewMap(): Map<SumViewTag, TextView> {
        return mSumViewMap
    }

    /**
     * 子クラスの初期化
     */
    abstract fun init()

    /**
     * 新規or更新を表す定数を取得
     */
    @EntryMode
    abstract fun getEntryMode(): Int

    /**
     * 勤怠情報の入力Viewを取得
     */
    abstract fun getWorkStatusInputInfoParcelList(): List<InputInfoParcel>

    /**
     * 収入情報の入力Viewを取得
     */
    abstract fun getIncomeInputInfoParcelList(): List<InputInfoParcel>

    /**
     * 控除情報の入力Viewを取得
     */
    abstract fun getDeductionInputInfoParcelList(): List<InputInfoParcel>

    /**
     * 更新時の処理
     */
    abstract override fun update(aInputInfoObservable: InputInfoObservableFragment)

    /**
     * 合計Viewの更新
     */
    abstract fun updateSumView()

    /**
     * データの保存
     */
    abstract fun saveData()

    /**
     * 表示するタブの種類を取得
     */
    abstract fun getTabPageList(): List<TabPage>

    /**
     * 表示する合計Viewの種類を取得
     */
    abstract fun getSumViewTagList(): List<SumViewTag>
}