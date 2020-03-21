package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Transaction implements Parcelable {

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


    public final String blockHash;
    public final int transactionIndex;
    public final int nonce;
    public final String hash;
    public final String from;
    public final String to;
    public final String value;
    public final String data;
    public final String gasPrice;
    public final String gas;
    public final String contractCreated;
    public final int status ;
    public final String r;
    public final String s;
    public final int v;
    public final long timeStamp;
    public final String[] blocks;
    public Logs[] logs;


    public Transaction(
            String blockHash,
            int transactionIndex,
            int nonce,
            String hash,
            String from,
            String to,
            String value,
            String data,
            String gasPrice,
            String gas,
            String contractCreated,
            int status,
            String r,
            String s,
            int v,
            long timeStamp,
            String[] blocks,
            Logs[] logs) {
        this.blockHash = blockHash;
        this.transactionIndex = transactionIndex;
        this.nonce = nonce;
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.value = value;
        this.data = data;
        this.gasPrice = gasPrice;
        this.gas = gas;
        this.contractCreated = contractCreated;
        this.status = status;
        this.r = r;
        this.s = s;
        this.v = v;
        this.timeStamp = timeStamp;
        this.blocks = blocks;
        this.logs = logs;

    }

    protected Transaction(Parcel in) {
        this.blockHash = in.readString();
        this.transactionIndex = in.readInt();
        this.nonce = in.readInt();
        this.hash = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.value = in.readString();
        this.data = in.readString();
        this.gasPrice = in.readString();
        this.gas = in.readString();
        this.contractCreated = in.readString();
        this.status = in.readInt();
        this.r = in.readString();
        this.s = in.readString();
        this.v = in.readInt();
        this.timeStamp = in.readLong();
        this.blocks = (String[])in.readArray(String.class.getClassLoader());

        Parcelable[] parcelableArray = in.readParcelableArray(Logs.class.getClassLoader());
        Logs[] logsArray = null;
        if(parcelableArray !=null){
            this.logs = Arrays.copyOf(parcelableArray, parcelableArray.length, Logs[].class);
        }
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

        dest.writeString(blockHash);
        dest.writeInt(transactionIndex);
        dest.writeInt(nonce);
        dest.writeString(hash);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(value);
        dest.writeString(data);
        dest.writeString(gasPrice);
        dest.writeString(gas);
        dest.writeString(contractCreated);
        dest.writeInt(status);
        dest.writeString(r);
        dest.writeString(s);
        dest.writeInt(v);
        dest.writeLong(timeStamp);
        dest.writeArray(blocks);
        dest.writeParcelableArray(logs, flags);

    }



}
