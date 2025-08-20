import com.fxcodeo.bitcoin.Wallet;
import com.fxcodeo.utils.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class Test {
    static {
        // 确保只注册一次
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static void main(String[] args) {
        Wallet wallet1 = new Wallet();
        System.out.println(wallet1.getBitcoinAddress());
        System.out.println(StringUtil.getStringFromKey(wallet1.getPublicKey()));
        System.out.println(StringUtil.getStringFromKey(wallet1.getPrivateKey()));
        System.out.println(wallet1.getBalance());
        System.out.println(wallet1.getUTXOs());
    }
}
