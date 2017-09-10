package org.hitogo.view;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.TextView;

import org.hitogo.core.HitogoAnimation;
import org.hitogo.core.HitogoController;
import org.hitogo.core.HitogoObject;
import org.hitogo.core.HitogoUtils;
import org.hitogo.button.HitogoButton;

import java.security.InvalidParameterException;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HitogoView extends HitogoObject<HitogoViewParams> {

    private ViewGroup viewGroup;
    private HitogoAnimation animation;
    private HitogoViewParams params;

    @Override
    protected void onCheckStart(Activity activity, @NonNull HitogoViewParams params) {
        if (params.getText() == null) {
            throw new InvalidParameterException("Text parameter cannot be null.");
        }

        if (params.getState() == null) {
            throw new InvalidParameterException("To display non-dialog hitogos you need to define " +
                    "a state which will use a specific layout.");
        }

        if (!params.isDismissible() && params.getCallToActionButtons().isEmpty()) {
            Log.e(HitogoViewBuilder.class.getName(), "Are you sure that this hitogo should have no " +
                    "interaction points? If yes, make sure to close this one if it's not " +
                    "needed anymore!");
        }
    }

    @Override
    protected void onCreate(@NonNull HitogoViewParams params, @NonNull HitogoController controller) {
        this.params = params;
        this.animation = params.getHitogoAnimation();
        if (animation == null) {
            this.animation = controller.getDefaultAnimation();
        }

        if (params.getContainerId() != null && getRootView() != null) {
            View containerView = getRootView().findViewById(params.getContainerId());
            if (containerView instanceof ViewGroup) {
                viewGroup = (ViewGroup) containerView;
            } else {
                viewGroup = null;
            }
        }
    }

    @Override
    protected View onCreateView(@NonNull Activity activity, @NonNull LayoutInflater inflater,
                                @NonNull HitogoViewParams params) {
        View view = inflater.inflate(getController().getLayout(params.getState()), null);

        if (view != null) {
            view = buildLayoutContent(view);
            view = buildCallToActionButtons(view);
            view = buildCloseButtons(view);
            return view;
        } else {
            throw new InvalidParameterException("Hitogo view is null. Is the layout existing for " +
                    "the state: '" + params.getState() + "'?");
        }
    }

    @NonNull
    private View buildLayoutContent(@NonNull View containerView) {
        View view;

        if(params.getTitle() != null) {
            view = setViewString(containerView, params.getTitleViewId(), params.getTitle());
            view = setViewString(view, params.getTextViewId(), params.getText());
        } else {
            view = setViewString(containerView, params.getTextViewId(), params.getText());
        }

        return view;
    }

    @NonNull
    @SuppressWarnings("deprecation")
    private View setViewString(@NonNull View containerView, @Nullable Integer viewId,
                               @Nullable String chars) {
        if (viewId != null) {
            TextView textView = containerView.findViewById(viewId);
            if (textView != null) {
                if (chars != null && HitogoUtils.isNotEmpty(chars)) {
                    textView.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        textView.setText(Html.fromHtml(chars, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        textView.setText(Html.fromHtml(chars));
                    }
                } else {
                    textView.setVisibility(View.GONE);
                }
            } else {
                throw new InvalidParameterException("Did you forget to add the " +
                        "title/text view to your layout?");
            }
        } else {
            throw new InvalidParameterException("Title or text view id is null.");
        }
        return containerView;
    }

    @NonNull
    private View buildCallToActionButtons(@NonNull View containerView) {
        for (final HitogoButton callToActionButton : params.getCallToActionButtons()) {
            View button = containerView.findViewById(callToActionButton.getViewIds()[0]);

            if (button != null) {
                if (button instanceof TextView) {
                    ((TextView) button).setText(callToActionButton.getText() != null ?
                            callToActionButton.getText() : "");
                }

                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callToActionButton.getListener().onClick();
                        getController().closeHitogo();
                    }
                });
            } else {
                throw new InvalidParameterException("Did you forget to add the " +
                        "call-to-action button to your layout?");
            }
        }

        return containerView;
    }

    @NonNull
    private View buildCloseButtons(@NonNull View containerView) {
        final HitogoButton closeButton = params.getCloseButton();
        boolean isDismissible = params.isDismissible();

        if(closeButton != null) {
            final View removeIcon = containerView.findViewById(closeButton.getViewIds()[0]);
            final View removeClick = containerView.findViewById(closeButton.getViewIds()[1]);

            if (removeIcon != null && removeClick != null) {
                removeIcon.setVisibility(isDismissible ? View.VISIBLE : View.GONE);
                removeClick.setVisibility(isDismissible ? View.VISIBLE : View.GONE);
                removeClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButton.getListener().onClick();
                        getController().closeHitogo();
                    }
                });
            } else {
                throw new InvalidParameterException("Did you forget to add the close button to " +
                        "your layout?");
            }
        }
        return containerView;
    }

    @Override
    protected void onAttach(@NonNull Activity activity) {
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            viewGroup.addView(getView());
        } else {
            ViewGroup.LayoutParams layoutParams = getView().getLayoutParams();
            if (null == params) {
                layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            if (activity.isFinishing()) {
                return;
            }
            activity.addContentView(getView(), layoutParams);
        }
    }

    @Override
    protected void onShowAnimation(@NonNull Activity activity) {
        HitogoUtils.measureView(activity, getView(), viewGroup);
        animation.showAnimation(params, getView(), this);
    }

    @Override
    protected void onDetachDefault(@NonNull Activity activity) {
        final ViewManager manager = (ViewManager) getView().getParent();
        manager.removeView(getView());
    }

    @Override
    protected void onDetachAnimation(@NonNull Activity activity) {
        animation.hideAnimation(params, getView(), this);
    }

    @Override
    public long getAnimationDuration() {
        return animation.getAnimationDuration();
    }
}
