package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class CfxTransaction implements Parcelable {

    /*       Token交易
    *       "blockHash": "0x60e443ae91f1d94e2c9fa71b3774ab8dbe041c8fe897843cde770163497fa6a7",
			"transactionIndex": 0,
			"nonce": 79,
			"hash": "0xee97ce6a3bb5261b994efa056d2563ae7a7d7abf5a37a52974510694ead86d16",
			"from": "0x7fc46011b87442d6a2f25e2e48101cdc9019839b",
			"to": "0x61e0003084db11f56ac8970e5a753eeea15ec4d4",
			"value": "0",
			"data": "0xa9059cbb00000000000000000000000072bd17186c8f5a89737218d733db8a76cca51c2400000000000000000000000000000000000000000000003635c9adc5dea00000",
			"gasPrice": "100",
			"gas": "1000000",
			"contractCreated": null,
			"status": 0,
			"r": "0x03c4d612e0375cd2cf4aa5225d3dfc6363119705bab196568f7a3d8d91de92a9",
			"s": "0x78eca96b3f5e56fff946f77e2c4a14a8395690402de9172405f0651ff475c629",
			"v": 0,
			"timestamp": 1577816872,
			"logs": [{
				"address": "0x61e0003084db11f56ac8970e5a753eeea15ec4d4",
				"data": "0x00000000000000000000000000000000000000000000003635c9adc5dea00000",
				"topics": ["0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", "0x0000000000000000000000007fc46011b87442d6a2f25e2e48101cdc9019839b", "0x00000000000000000000000072bd17186c8f5a89737218d733db8a76cca51c24"]
			}],
			"blocks": ["0x60e443ae91f1d94e2c9fa71b3774ab8dbe041c8fe897843cde770163497fa6a7"]

        cfx交易
        * "blockHash": "0x298060d352adf31c036556c5da687cebe876234b71d2e60bf7eab81b95fb3363",
			"transactionIndex": 0,
			"nonce": 71,
			"hash": "0x05f20a59ccacc4e05c93b67522af13de84c5ddb2cd26a1021078e0154bd8078f",
			"from": "0x7fc46011b87442d6a2f25e2e48101cdc9019839b",
			"to": "0x921e78af6659782297c17acf9cf61dbcf3d4f924",
			"value": "10000000000000000000000",
			"data": "0x",
			"gasPrice": "100",
			"gas": "1000000",
			"contractCreated": null,
			"status": 0,
			"r": "0x7fe08d1ac5e512e3aaf9e762ceeaa2bc11fec169c48083cfd92f12abe47271bd",
			"s": "0x4cd58948081589743347eb1525ae1c21215c9437a8a1238443f39fc34a9d7dc2",
			"v": 0,
			"timestamp": 1577811738,
			"logs": [],
			"blocks": ["0x298060d352adf31c036556c5da687cebe876234b71d2e60bf7eab81b95fb3363"]
    * */
    public final String hash;
    public final String blockNumber;
    public final long timeStamp;
    public final int nonce;
    public final String from;
    public final String to;
    public final String value;
    public final String gas;
    public final String gasPrice;
    public final String gasUsed;
    public final String input;
    public final TransactionOperation[] operations;
    public final String contract;
    public final String error;

    public CfxTransaction(
            String hash,
            String error,
            String blockNumber,
            long timeStamp,
            int nonce,
            String from,
            String to,
            String value,
            String gas,
            String gasPrice,
            String input,
            String gasUsed,
            TransactionOperation[] operations,
            String contract) {
        this.hash = hash;
        this.error = error;
        this.blockNumber = blockNumber;
        this.timeStamp = timeStamp;
        this.nonce = nonce;
        this.from = from;
        this.to = to;
        this.value = value;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.input = input;
        this.gasUsed = gasUsed;
        this.operations = operations;
        this.contract = contract;
    }

    protected CfxTransaction(Parcel in) {
        hash = in.readString();
        error = in.readString();
        blockNumber = in.readString();
        timeStamp = in.readLong();
        nonce = in.readInt();
        from = in.readString();
        to = in.readString();
        value = in.readString();
        gas = in.readString();
        gasPrice = in.readString();
        input = in.readString();
        gasUsed = in.readString();
        Parcelable[] parcelableArray = in.readParcelableArray(TransactionOperation.class.getClassLoader());
        TransactionOperation[] operations = null;
        if (parcelableArray != null) {
            operations = Arrays.copyOf(parcelableArray, parcelableArray.length, TransactionOperation[].class);
        }
        this.operations = operations;
        this.contract = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(error);
        dest.writeString(blockNumber);
        dest.writeLong(timeStamp);
        dest.writeInt(nonce);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(value);
        dest.writeString(gas);
        dest.writeString(gasPrice);
        dest.writeString(input);
        dest.writeString(gasUsed);
        dest.writeParcelableArray(operations, flags);
        dest.writeString(contract);
    }
}
