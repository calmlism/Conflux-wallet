
package pro.conflux.wallet.base;

public interface BaseContract {

    interface BasePresenter {

//        void attachView(T view);
//
//        void detachView();
    }

    interface BaseView {

        void showError(String errorInfo);

        void complete();

    }
}
