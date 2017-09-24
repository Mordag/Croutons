package org.hitogo.button.action;

import android.support.annotation.NonNull;
import android.util.Log;

import org.hitogo.core.HitogoUtils;
import org.hitogo.button.core.HitogoButton;

import java.security.InvalidParameterException;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HitogoAction extends HitogoButton<HitogoActionParams> {

    @Override
    protected void onCheck(@NonNull HitogoActionParams params) {
        if (params.getText() == null || HitogoUtils.isEmpty(params.getText())) {
            Log.w(HitogoActionBuilder.class.getName(), "Button has no text. If you want to " +
                    "display a button with only one icon, you can ignore this warning.");
        }

        if (params.hasActionView() && (params.getViewIds() == null || params.getViewIds().length == 0)) {
            throw new InvalidParameterException("Have you forgot to add at least one view id for " +
                    "this button?");
        }

        if (params.hasActionView()) {
            for (int id : params.getViewIds()) {
                if (id == -1) {
                    throw new InvalidParameterException("Button view id cannot be -1.");
                }
            }
        }
    }
}