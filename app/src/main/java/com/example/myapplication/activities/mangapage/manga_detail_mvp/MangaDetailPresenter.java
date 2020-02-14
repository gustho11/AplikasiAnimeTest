package com.example.myapplication.activities.mangapage.manga_detail_mvp;

import android.util.Log;

import com.example.myapplication.networks.ApiEndPointService;
import com.example.myapplication.networks.RetrofitConfig;
import com.zhkrb.cloudflare_scrape_android.Cloudflare;

import org.jsoup.internal.StringUtil;

import java.net.HttpCookie;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MangaDetailPresenter {
    private MangaDetailInterface mangaDetailInterface;

    public MangaDetailPresenter(MangaDetailInterface mangaDetailInterface) {
        this.mangaDetailInterface = mangaDetailInterface;
    }

    public void getDetailMangaData(String detailPageURL) {
        Cloudflare cf = new Cloudflare(detailPageURL);
        cf.setUser_agent("Mozilla/5.0");
        cf.getCookies(new Cloudflare.cfCallback() {
            @Override
            public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
                Log.e("getNewURL?", String.valueOf(hasNewUrl));
                if (hasNewUrl) {
                    passToRetrofit(newUrl);
                    Log.e("NEWURL", newUrl);
                } else {
                    passToRetrofit(detailPageURL);
                }
            }

            @Override
            public void onFail() {
                mangaDetailInterface.onGetDetailDataFailed();
            }
        });
    }

    private void passToRetrofit(String newUrl) {

        String URLAfterCut = newUrl.substring(22);
        ApiEndPointService apiEndPointService = RetrofitConfig.getInitMangaRetrofit();
        apiEndPointService.getDiscoverMangaData(URLAfterCut)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String detailHTMLResult) {
                        mangaDetailInterface.onGetDetailDataSuccess(detailHTMLResult);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mangaDetailInterface.onGetDetailDataFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
