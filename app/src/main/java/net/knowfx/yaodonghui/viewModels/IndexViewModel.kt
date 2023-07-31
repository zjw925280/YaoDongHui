package net.knowfx.yaodonghui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import net.knowfx.yaodonghui.base.BaseResponse
import net.knowfx.yaodonghui.entities.Result
import net.knowfx.yaodonghui.ext.hasDataKey
import net.knowfx.yaodonghui.ext.readData
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.http.APIs
import net.knowfx.yaodonghui.http.HttpClientManager
import net.knowfx.yaodonghui.ui.activity.FunctionAllActivity.Companion.KEY_LIST
import net.knowfx.yaodonghui.ui.fragment.FragmentIndexVpList
import net.knowfx.yaodonghui.utils.NetConfig
import net.knowfx.yaodonghui.utils.SingleSourceLiveData
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class IndexViewModel(app: Application) : AndroidViewModel(app) {
    private val service = HttpClientManager.instance.client.createService(Service::class.java)
    val functions = SingleSourceLiveData<Any>()
    val banner = SingleSourceLiveData<Any>()
    val hisTop = SingleSourceLiveData<Any>()
    val hisBottom = SingleSourceLiveData<Any>()
    val pagerListNewest = SingleSourceLiveData<Any>()
    val pagerListOrigin = SingleSourceLiveData<Any>()
    val pagerListDealer = SingleSourceLiveData<Any>()
    val functionAllList = SingleSourceLiveData<Any>()
    val unreadResult = SingleSourceLiveData<Any>()

    fun getFunctions() {
        functions.setSource(service.getFunctions())
    }

    fun getBanner() {
        banner.setSource(service.getBanner())
    }

    fun getHisTop() {
        hisTop.setSource(service.getHistoryTop())
    }

    fun getHisTBottom() {
        hisBottom.setSource(service.getHistoryBottom())
    }

    fun getPagerList(classId: String, pageNum: Int, pageSize: Int) {
        when (classId) {
            FragmentIndexVpList.CLASS_ID_NEWEST -> pagerListNewest.setSource(
                service.getPagerList(
                    classId,
                    pageNum,
                    pageSize
                )
            )

            FragmentIndexVpList.CLASS_ID_ORIGINAL -> pagerListOrigin.setSource(
                service.getPagerList(
                    classId,
                    pageNum,
                    pageSize
                )
            )

            FragmentIndexVpList.CLASS_ID_BROKER -> pagerListDealer.setSource(
                service.getPagerList(
                    classId,
                    pageNum,
                    pageSize
                )
            )

            else -> {}
        }
    }

    fun getUnread(){
        unreadResult.setSource(service.getUnread())
    }

    fun getAllFunctions() {
        hasDataKey(KEY_LIST).trueLet {
            val string = readData(KEY_LIST, "")
            string.isNotEmpty().trueLet {
                val result = Result<String>()
                result.data = string
                result.msg = ""
                result.code = NetConfig.REQUEST_SUCCESS_CODE
                functionAllList.postValue(result)
                return
            }
        }
        functionAllList.setSource(service.getAllFunction())
    }

    interface Service {
        @POST(APIs.URL_GET_INDEX_FUNCTION)
        fun getFunctions(): LiveData<Any>

        @POST(APIs.URL_GET_INDEX_BANNER)
        fun getBanner(): LiveData<Any>

        @POST(APIs.URL_GET_INDEX_HIS_TOP)
        fun getHistoryTop(): LiveData<Any>

        @POST(APIs.URL_GET_INDEX_HIS_BOTTOM)
        fun getHistoryBottom(): LiveData<Any>

        @POST(APIs.URL_GET_INDEX_PAGER_LIST)
        fun getPagerList(
            @Query("classId") classId: String,
            @Query("pageNum") pageNum: Int,
            @Query("pageSize") pageSize: Int
        ): LiveData<Any>

        @POST(APIs.URL_GET_FUNCTION_ALL)
        fun getAllFunction(): LiveData<Any>
        @GET(APIs.URL_GET_UNREAD)
        fun getUnread(): LiveData<Any>
    }
}