package pro.conflux.wallet.repository;


import pro.conflux.wallet.entity.NetworkInfo;

public interface OnNetworkChangeListener {
	void onNetworkChanged(NetworkInfo networkInfo);
}
