package com.bitdubai.reference_wallet.fan_wallet.app_connection;

import android.content.Context;

import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;
import com.bitdubai.fermat_android_api.engine.FooterViewPainter;
import com.bitdubai.fermat_android_api.engine.HeaderViewPainter;
import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Developers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_tky_api.layer.wallet_module.interfaces.FanWalletModule;
import com.bitdubai.reference_wallet.fan_wallet.common.header.FanWalletHeaderPainter;
import com.bitdubai.reference_wallet.fan_wallet.fragmentFactory.FanWalletFragmentFactory;
import com.bitdubai.reference_wallet.fan_wallet.session.FanWalletSessionReferenceApp;

/**
 * Created by Miguel Payarez on 28/03/16.
 */
public class FanWalletFermatAppConnection extends AppConnections<ReferenceAppFermatSession<FanWalletModule>> {

    public FanWalletFermatAppConnection(Context activity) {
        super(activity);
    }

    @Override
    public FermatFragmentFactory getFragmentFactory() {
        return new FanWalletFragmentFactory();
    }

    @Override
    public PluginVersionReference[] getPluginVersionReference() {
        return new PluginVersionReference[]{ new PluginVersionReference(
                Platforms.TOKENLY,
                Layers.WALLET_MODULE,
                Plugins.TOKENLY_FAN_WALLET_MODULE,
                Developers.BITDUBAI,
                new Version()
        )};
    }

    @Override
    public AbstractReferenceAppFermatSession getSession() { return new FanWalletSessionReferenceApp(); }

    @Override
    public NavigationViewPainter getNavigationViewPainter() {
        return null;
    }

    @Override
    public HeaderViewPainter getHeaderViewPainter() {
        return new FanWalletHeaderPainter(getContext(),getFullyLoadedSession());
    }

    @Override
    public FooterViewPainter getFooterViewPainter() {
        return null;
    }

}
