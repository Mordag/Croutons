package org.hitogo.button.action;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hitogo.button.core.HitogoButtonBuilder;
import org.hitogo.button.core.HitogoButtonListener;
import org.hitogo.button.core.HitogoButton;
import org.hitogo.button.core.HitogoButtonParams;
import org.hitogo.button.core.HitogoButtonParamsHolder;
import org.hitogo.button.core.HitogoButtonType;
import org.hitogo.core.HitogoContainer;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HitogoActionBuilder extends HitogoButtonBuilder {

    private static final HitogoButtonType type = HitogoButtonType.ACTION;

    private String text;
    private int[] viewIds;
    private boolean hasActionView;
    private boolean closeAfterExecute;

    private HitogoButtonListener listener;

    public HitogoActionBuilder(@NonNull Class<? extends HitogoButton> targetClass,
                               @NonNull Class<? extends HitogoButtonParams> paramClass,
                               @NonNull HitogoContainer container) {
        super(targetClass, paramClass, container, type);
    }

    @NonNull
    public HitogoActionBuilder setText(String text) {
        this.text = text;
        return this;
    }

    @NonNull
    public HitogoActionBuilder forViewAction() {
        return forViewAction(getController().provideDefaultCloseIconId(),
                getController().provideDefaultCloseClickId());
    }

    @NonNull
    public HitogoActionBuilder forViewAction(Integer closeIconId) {
        return forViewAction(closeIconId, getController().provideDefaultCloseClickId());
    }

    @NonNull
    public HitogoActionBuilder forViewAction(Integer closeIconId, @Nullable Integer optionalCloseViewId) {
        hasActionView = true;
        viewIds = new int[2];
        viewIds[0] = closeIconId;
        viewIds[1] = optionalCloseViewId != null ? optionalCloseViewId : closeIconId;
        return this;
    }

    @NonNull
    public HitogoActionBuilder forClickOnlyAction() {
        hasActionView = false;
        viewIds = new int[0];
        return this;
    }

    @NonNull
    public HitogoActionBuilder listenWith(@Nullable HitogoButtonListener listener) {
        this.listener = listener;
        this.closeAfterExecute = true;
        return this;
    }

    @NonNull
    public HitogoActionBuilder listenWith(@Nullable HitogoButtonListener listener, boolean closingAfterExecute) {
        this.listener = listener;
        this.closeAfterExecute = closingAfterExecute;
        return this;
    }

    @Override
    protected void onProvideData(HitogoButtonParamsHolder holder) {
        holder.provideString("text", text);
        holder.provideIntArray("viewIds", viewIds);
        holder.provideBoolean("hasActionView", hasActionView);
        holder.provideBoolean("closeAfterExecute", closeAfterExecute);
        holder.provideButtonListener(listener);
    }
}