package com.example.devicemonitor


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun uiFlowTest() {

        Log.d("UiFlow", "【测试】准备显示页面")

        composeTestRule.setContent {
            UiFlowTestScreen()
        }

        Log.d("UiFlow", "【测试】准备点击按钮")

        composeTestRule
            .onNodeWithText("数字 +1")
            .performClick()

        Log.d("UiFlow", "【测试】按钮点击完成")

        composeTestRule
            .onNodeWithText("当前数字：1")
            .assertExists()

        Log.d("UiFlow", "【测试】验证成功：数字已经变成1")
    }
}

@Composable
fun UiFlowTestScreen() {

    Log.d("UiFlow", "① UiFlowTestScreen 执行")

    var count by remember {
        Log.d("UiFlow", "② remember 初始化 count")
        mutableStateOf(0)
    }

    Log.d("UiFlow", "③ 当前 count=$count")

    Column {

        Text("当前数字：$count")

        Button(
            onClick = {
                Log.d("UiFlow", "④ onClick 开始")

                count++

                Log.d("UiFlow", "⑤ count 修改为 $count")
            }
        ) {
            Text("数字 +1")
        }
    }

    Log.d("UiFlow", "⑥ UiFlowTestScreen 本次执行结束")
}