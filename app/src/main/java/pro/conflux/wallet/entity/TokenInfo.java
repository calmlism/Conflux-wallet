package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenInfo implements Parcelable {
    public final String address;
    public final String name;
    public final String symbol;
    public final int decimals;
    public final String type;

    public TokenInfo(String address, String name, String symbol, int decimals,String type) {
        this.address = address;
        this.name = name;
        this.symbol = symbol;
        this.decimals = decimals;
        this.type = type;
    }

    private TokenInfo(Parcel in) {
        address = in.readString();
        name = in.readString();
        symbol = in.readString();
        decimals = in.readInt();
        type = in.readString();
    }

    public static final Creator<TokenInfo> CREATOR = new Creator<TokenInfo>() {
        @Override
        public TokenInfo createFromParcel(Parcel in) {
            return new TokenInfo(in);
        }

        @Override
        public TokenInfo[] newArray(int size) {
            return new TokenInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeInt(decimals);
        dest.writeString(type);
    }
}
