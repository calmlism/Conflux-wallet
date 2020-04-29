package pro.conflux.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class TokenId implements Parcelable {
    public final TokenInfo tokenInfo;
    public String tokenId;
    public int index;

    public TokenId(TokenInfo tokenInfo, String tokenId ,int index) {
        this.tokenInfo = tokenInfo;
        this.tokenId = tokenId;
        this.index = index;
    }

    private TokenId(Parcel in) {
        tokenInfo = in.readParcelable(TokenInfo.class.getClassLoader());
        tokenId = in.readString();
        index = in.readInt();
    }

    public static final Creator<TokenId> CREATOR = new Creator<TokenId>() {
        @Override
        public TokenId createFromParcel(Parcel in) {
            return new TokenId(in);
        }

        @Override
        public TokenId[] newArray(int size) {
            return new TokenId[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tokenInfo, flags);
        dest.writeString(tokenId.toString());
        dest.writeInt(index);
    }
}
