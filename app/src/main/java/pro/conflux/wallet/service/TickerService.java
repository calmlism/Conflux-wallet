package pro.conflux.wallet.service;


import pro.conflux.wallet.entity.Ticker;

import io.reactivex.Observable;

public interface TickerService {

    Observable<Ticker> fetchTickerPrice(String symbols, String currency);
}
