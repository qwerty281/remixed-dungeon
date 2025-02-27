package com.nyrds.pixeldungeon.support;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.nyrds.platform.EventCollector;
import com.nyrds.platform.app.RemixedDungeonApp;
import com.nyrds.platform.game.Game;
import com.watabou.pixeldungeon.utils.GLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.var;

//This is flavor specific class
public class AdsUtils {

    static Map<AdsUtilsCommon.IBannerProvider, Integer> bannerFails = new ConcurrentHashMap<>();
    static Map<AdsUtilsCommon.IInterstitialProvider, Integer> interstitialFails = new ConcurrentHashMap<>();
    static Map<AdsUtilsCommon.IRewardVideoProvider, Integer> rewardVideoFails = new ConcurrentHashMap<>();

    static InitializationStatus initializationStatus;

    static {
        MobileAds.initialize(RemixedDungeonApp.getContext(), initializationStatus -> {
            AdsUtils.initializationStatus = initializationStatus;
            var status = initializationStatus.getAdapterStatusMap();

            GLog.debug("admob status: %s", status.toString());
        });

        bannerFails.put(new AdMobBannerProvider(),-2);
        interstitialFails.put(new AdMobInterstitialProvider(), -2);


        if(!RemixedDungeonApp.checkOwnSignature()) {
            bannerFails.put(new AAdsComboProvider(), 0);
            interstitialFails.put(new AAdsComboProvider(), 0);
        }

        if(AppodealAdapter.usable()) {
            AppodealAdapter.init();
            bannerFails.put(AppodealBannerProvider.getInstance(), -1);
            interstitialFails.put(new AppodealInterstitialProvider(), -1);
        }
    }

    public static void initRewardVideo() {

        if(!rewardVideoFails.isEmpty()) {
            return;
        }

        if(AppodealAdapter.usable()) {
            rewardVideoFails.put(new AppodealRewardVideoProvider(), -1);
        }
        rewardVideoFails.put(new GoogleRewardVideoAds(), -2000);
    }

    static void removeBannerView(int index, View adview) {
        if (adview instanceof BannerView) {
            Appodeal.hide(Game.instance(), Appodeal.BANNER);
        }
        if (adview instanceof AdView) {
            ((AdView) adview).destroy();
        }

        Game.instance().getLayout().removeViewAt(index);
    }

    public static void removeEasyModeBanner() {
        Game.runOnMainThread(() -> {
            int index = AdsUtils.bannerIndex();
            if (index >= 0) {

                View adview = Game.instance().getLayout().getChildAt(index);
                AdsUtils.removeBannerView(index, adview);

            }
        });
    }

    static void updateBanner(final View view) {
        Game.runOnMainThread(() -> {

            int index = AdsUtils.bannerIndex();
            final LinearLayout layout = Game.instance().getLayout();

            if (index >= 0) {

                View adview = layout.getChildAt(index);
                if(adview == view) {
                    return;
                }

                AdsUtils.removeBannerView(index, adview);

            }

            try {
                layout.addView(view, 0);
            } catch (IllegalStateException e) {
                EventCollector.logException(e);
            }
        });
    }


    static int bannerIndex() {
        final LinearLayout layout = Game.instance().getLayout();

        int childs = layout.getChildCount();
        for (int i = 0; i < childs; ++i) {
            View view = layout.getChildAt(i);
            if (view instanceof AdView || view instanceof WebView || view instanceof BannerView) {
                return i;
            }
        }
        return -1;
    }

}
