package com.kjy.coroutine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kjy.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 클릭리스너를 추가하고 CoroutineScope 추가. 컨텍스트는 Main으로 입력해서 UI 관련 요소들을 다룰 수 있도록 구성.
        // 따라서 버튼 클릭시 가장 먼저 코루틴이 실행.
        binding.run {
            buttonDownload.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    // progress의 visibiltiy가 visible로 바꿀 수 있도록 하고
                    // 화면에 플레인텍스트에 입력된 값을 가져와서 url 변수에 저장함.
                    progress.visibility = View.VISIBLE
                    val url = editUrl.text.toString()
                    // loadImage() 함수를 호출하면서 url을 함께 전달하는데, 이 부분은 백그라운드 처리를 담당하는 IO
                    // 컨텍스트에서 진행되야 하기 때문에 withContext() 문을 사용해서 컨텍스트를 IO로 전환함.
                    // 그리고 결과값을 bitmap에 저장.

                    // loadImage() 함수에서 비트맵이 생성되고 bitmap 변수에 저장되기 전까지는 다음 줄이 실행되지 않고 멈춰 있습니다.
                    val bitmap = withContext(Dispatchers.IO) {
                        loadImage(url)
                    }
                    // 이미지뷰에 bitmap을 입력하고 VISIBLE 상태의 프로그래스바는 다시 GONE으로 바꿔서 화면에서 보이지 않게 함.
                    imagePreView.setImageBitmap(bitmap)
                    progress.visibility = View.GONE
                }

            }

        }
    }
}

// 탑레벨에 loadImage() 함수를 작성하고 suspend 키워드를 사용해서 코루틴으로 만들어줌. URL 객체를 만들고 URL이 가지고 있는
// openStream을 Bitmap 이미지로 저장한 후 반환하는 간단한 함수.
suspend fun loadImage(imageUrl: String): Bitmap {
    // .net(java) import
    val url = URL(imageUrl)
    val stream = url.openStream()
    return BitmapFactory.decodeStream(stream)

}