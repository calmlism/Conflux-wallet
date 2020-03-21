package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Logs implements Parcelable {
    /**
     * "logs": [{
     * 				"address": "0x61e0003084db11f56ac8970e5a753eeea15ec4d4",
     * 				"data": "0x00000000000000000000000000000000000000000000003635c9adc5dea00000",
     * 				"topics": ["0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", "0x0000000000000000000000007fc46011b87442d6a2f25e2e48101cdc9019839b", "0x00000000000000000000000072bd17186c8f5a89737218d733db8a76cca51c24"]
     *                        }],
     */
    public  final String address;
    public  final String data;
    public  final String[] topics;

    public Logs(String address,String data,String[] topicd){
        this.address = address;
        this.data = data;
        this.topics = topicd;
    }
    protected Logs(Parcel in){
        address = in.readString();
        data = in.readString();
        topics = (String[])in.readArray(String.class.getClassLoader());
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(data);
        parcel.writeArray(topics);
    }
}
