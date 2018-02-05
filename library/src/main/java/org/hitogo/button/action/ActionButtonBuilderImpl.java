package org.hitogo.button.action;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hitogo.button.core.ButtonBuilderImpl;
import org.hitogo.button.core.ButtonImpl;
import org.hitogo.button.core.ButtonParams;
import org.hitogo.button.core.ButtonParamsHolder;
import org.hitogo.core.HitogoContainer;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ActionButtonBuilderImpl extends ButtonBuilderImpl<ActionButtonBuilder, ActionButton> implements ActionButtonBuilder {

    private int[] viewIds;
    private boolean hasButtonView;

    public ActionButtonBuilderImpl(@NonNull Class<? extends ButtonImpl> targetClass,
                                   @NonNull Class<? extends ButtonParams> paramClass,
                                   @NonNull ButtonParamsHolder holder,
                                   @NonNull HitogoContainer container) {
        super(targetClass, paramClass, holder, container);
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forCloseAction() {
        return forCloseAction(getController().provideDefaultCloseIconId(),
                getController().provideDefaultCloseClickId());
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forCloseAction(Integer closeIconId) {
        return forCloseAction(closeIconId, getController().provideDefaultCloseClickId());
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forCloseAction(Integer closeIconId, @Nullable Integer optionalCloseViewId) {
        return forViewAction(closeIconId, optionalCloseViewId);
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forViewAction(Integer closeIconId) {
        return forViewAction(closeIconId, null);
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forViewAction(Integer iconId, @Nullable Integer clickId) {
        this.hasButtonView = true;
        this.viewIds = new int[2];
        this.viewIds[0] = iconId;
        this.viewIds[1] = clickId != null ? clickId : iconId;
        return this;
    }

    @Override
    @NonNull
    public ActionButtonBuilderImpl forClickOnlyAction() {
        this.hasButtonView = false;
        this.viewIds = new int[0];
        return this;
    }

    @Override
    protected void onProvideData(ButtonParamsHolder holder) {
        super.onProvideData(holder);

        holder.provideIntArray(ActionButtonParamsKeys.VIEW_IDS_KEY, viewIds);
        holder.provideBoolean(ActionButtonParamsKeys.HAS_BUTTON_VIEW_KEY, hasButtonView);
    }
}