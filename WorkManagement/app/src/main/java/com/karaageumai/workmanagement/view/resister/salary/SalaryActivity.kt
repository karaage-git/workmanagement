package com.karaageumai.workmanagement.view.resister.salary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.karaageumai.workmanagement.R

class SalaryActivity : AppCompatActivity() {

    companion object {
        private const val NUM_PAGES = 3
        private const val PAGE_OF_WORK_STATUS = 0
        private const val PAGE_OF_INCOME = 1
        private const val PAGE_OF_DEDUCTION = 2
    }

    lateinit var mViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        mViewPager = findViewById(R.id.view_pager)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mViewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, mViewPager) { tab, position ->
            when(position) {
                PAGE_OF_WORK_STATUS -> {
                    tab.text = getString(R.string.tab_work)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_work_24)
                }
                PAGE_OF_INCOME -> {
                    tab.text = getString(R.string.tab_income)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_attach_money_24)
                }
                PAGE_OF_DEDUCTION -> {
                    tab.text = getString(R.string.tab_deduction)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_money_off_24)
                }
            }

        }.attach()


    }





    private inner class ScreenSlidePagerAdapter(mActivity: AppCompatActivity) : FragmentStateAdapter(mActivity) {
        override fun getItemCount(): Int {
            return NUM_PAGES
        }

        override fun createFragment(position: Int): Fragment {

            when(position) {
                PAGE_OF_WORK_STATUS -> {
                    return WorkStatusInputFragment()
                }

                PAGE_OF_INCOME -> {
                    return IncomeInputFragment()
                }

                PAGE_OF_DEDUCTION -> {
                    return DeductionInputFragment()
                }

                else -> {
                    return WorkStatusInputFragment()
                }
            }

        }

    }
}