package ptml.releasing.app.views.dialogs;

import android.view.View;

/**
 * Created by yarolegovich on 2/2/18.
 */

public interface ViewConfigurator<T extends View> {
  void configureView(T v);
}
