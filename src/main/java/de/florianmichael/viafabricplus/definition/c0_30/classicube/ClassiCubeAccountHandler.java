package de.florianmichael.viafabricplus.definition.c0_30.classicube;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process.ILoginProcessHandler;
import de.florianmichael.viafabricplus.util.FileSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Map;

public class ClassiCubeAccountHandler extends FileSaver implements ILoginProcessHandler {
    public final static Logger LOGGER = LoggerFactory.getLogger("ViaFabricPlus/ClassiCube Account Handler");
    public static ClassiCubeAccountHandler INSTANCE;
    private ClassiCubeAccount account;

    public ClassiCubeAccountHandler() {
        super("classicube.account");
    }

    public static void create() {
        ClassiCubeAccountHandler.INSTANCE = new ClassiCubeAccountHandler();
        ClassiCubeAccountHandler.INSTANCE.init();
    }

    @Override
    public void write(JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : account.toJson().entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void read(JsonObject object) {
        try {
            account = ClassiCubeAccount.fromJson(object);
            account.login(this);
        } catch (LoginException e) {
            LOGGER.error("Failed to log into ClassiCube account!", e);
        }
    }

    @Override
    public void handleMfa(ClassiCubeAccount account) {
        LOGGER.error("Failed to log into ClassiCube account due to MFA request.");
    }

    @Override
    public void handleSuccessfulLogin(ClassiCubeAccount account) {
        LOGGER.info("Successfully logged into ClassiCube!");
    }

    public ClassiCubeAccount getAccountClone() {
        return new ClassiCubeAccount(account.token, account.username, account.password);
    }
}
