package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Transaction implements Parcelable {

    /*
    {
	"code": 0,
	"message": "",
	"result": {
		"total": 1,
		"data": [{
			"blockHash": "0xa57f8d1ae8d4c9f1320de2216580f9a88b4cb1d7877cef00e837cd83d528f1e4",
			"chainId": "0x0",
			"contractCreated": null,
			"data": "0x",
			"epochHeight": "0x154c4",
			"from": "0x1516b5cfdca244f01788671c2a4c632221e9d9c7",
			"gas": "0x186a0",
			"gasPrice": "0x64",
			"hash": "0x7d642fb61a7f65f81197e44d010b9b311c4476652acac4a6d8e9b1b5655ba514",
			"nonce": "0x0",
			"r": "0x5591b28233e85f698b80e3b87294357de2bf12992362559b3687e89360fc281",
			"s": "0x77144a57031f2a997d914bc111f991237d2eabc484078c8d7497b495003175c9",
			"status": 0,
			"storageLimit": "0x186a0",
			"to": "0x1fc46011b87442d6a2f25e2e48101cdc9019839b",
			"transactionIndex": "0x2",
			"v": "0x1",
			"value": "0x1158e460913d00000",
			"timestamp": 1587965139,
			"index": 5468
		}]
	}
}
    * */

    public  String blockHash;
    public  String chainId;
    public  String contractCreated;
    public  String data;
    public  String epochHeight;
    public  String from;
    public  String gas;
    public  String gasPrice;
    public  String hash;
    public  String nonce;
    public  String r;
    public  String s;
    public  int status ;
    public  String storageLimit;
    public  String to;
    public  String transactionIndex;
    public  String v;
    public  String value;
    public  long timeStamp;
    public  int index;


    public Transaction(
            String blockHash,
            String chainId,
            String contractCreated,
            String data,
            String epochHeight,
            String from,
            String gas,
            String gasPrice,
            String hash,
            String nonce,
            String r,
            String s,
            int status,
            String storageLimit,
            String to,
            String transactionIndex,
            String v,
            String value,
            long timeStamp,
            int index
            ) {
        this.blockHash = blockHash;
        this.chainId = chainId;
        this.contractCreated = contractCreated;
        this.data = data;
        this.epochHeight = epochHeight;
        this.from = from;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.hash = hash;
        this.nonce = nonce;
        this.r = r;
        this.s = s;
        this.status = status;
        this.storageLimit = storageLimit;
        this.to = to;
        this.transactionIndex = transactionIndex;
        this.v = v;
        this.value = value;
        this.timeStamp = timeStamp;
        this.index = index;
    }

    protected Transaction(Parcel in) {
        this.blockHash = in.readString();
        this.chainId =  in.readString();
        this.contractCreated =  in.readString();
        this.data =  in.readString();
        this.epochHeight =  in.readString();
        this.from =  in.readString();
        this.gas =  in.readString();
        this.gasPrice =  in.readString();
        this.hash =  in.readString();
        this.nonce =  in.readString();
        this.r =  in.readString();
        this.s =  in.readString();
        this.status =  in.readInt();
        this.storageLimit =  in.readString();
        this.to =  in.readString();
        this.transactionIndex =  in.readString();
        this.v =  in.readString();
        this.value =  in.readString();
        this.timeStamp =  in.readLong();
        this.index =  in.readInt();

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
        dest.writeString(chainId);
        dest.writeString(contractCreated);
        dest.writeString(data);
        dest.writeString(epochHeight);
        dest.writeString(from);
        dest.writeString(gas);
        dest.writeString(gasPrice);
        dest.writeString(hash);
        dest.writeString(nonce);
        dest.writeString(r);
        dest.writeString(s);
        dest.writeInt(status);
        dest.writeString(storageLimit);
        dest.writeString(to);
        dest.writeString(transactionIndex);
        dest.writeString(v);
        dest.writeString(value);
        dest.writeLong(timeStamp);
        dest.writeInt(index);
    }

}
