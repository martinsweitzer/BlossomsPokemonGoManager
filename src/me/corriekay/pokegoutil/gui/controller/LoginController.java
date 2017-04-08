package me.corriekay.pokegoutil.gui.controller;

import javax.swing.SwingUtilities;

import com.pokegoapi.auth.GoogleUserCredentialProvider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import me.corriekay.pokegoutil.BlossomsPoGoManager;
import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.models.BpmResult;
import me.corriekay.pokegoutil.data.models.LoginData;
import me.corriekay.pokegoutil.utils.helpers.Browser;
import me.corriekay.pokegoutil.utils.helpers.UIHelper;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;

/**
 * The LoginController is use to handle all login related actions.
 */
public class LoginController extends BaseController<StackPane> {

    private static final String BOSSLAND_POKEHASH_SITE = "https://talk.pogodev.org/d/51-api-hashing-service-by-pokefarmer";
    private final AccountManager accountManager = AccountManager.getInstance();
    private LoginData configLoginData = new LoginData();

    private ToggleGroup radioGroup = new ToggleGroup();
    // UI elements
    @FXML
    private RadioButton ptcRadio;
    @FXML
    private RadioButton googleRadio;
    @FXML
    private Pane ptcPane;
    @FXML
    private Pane googlePane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField tokenField;
    @FXML
    private Button getTokenBtn;
    @FXML
    private TextField hashKeyField;
    @FXML
    private Button loginBtn;
    @FXML
    private CheckBox saveAuthChkbx;

    /**
     * Default constructor to initialize the component.
     */
    public LoginController() {
        super();
        initializeController();
    }

    /**
     * Displays an error dialog with a message.
     *
     * @param message the error message
     */
    private void alertFailedLogin(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Login");
        alert.setHeaderText("Unfortunately, your login has failed");
        alert.setContentText(message != null ? message : "" + "\nPress OK to go back and try again later.");
        alert.showAndWait();
    }

    @Override
    public String getFxmlLayout() {
        return "layout/Login.fxml";
    }

    @Override
    public void setGuiControllerSettings() {
        guiControllerSettings.setTitle("Login");
        guiControllerSettings.setResizeable(false);
    }

    /**
     * Initialize method that is called right after the login panel is created.
     */
    @FXML
    private void initialize() {
        configLoginData = accountManager.getLoginData();

        ptcRadio.setToggleGroup(radioGroup);
        googleRadio.setToggleGroup(radioGroup);
        
        ptcPane.disableProperty().bind(ptcRadio.selectedProperty().not());
        googlePane.disableProperty().bind(googleRadio.selectedProperty().not());
        
        saveAuthChkbx.setOnAction(this::onSaveAuthChkbxChanged);
        getTokenBtn.setOnAction(this::ongetTokenBtnClicked);
        loginBtn.setOnAction(this::onloginBtnClicked);

        final boolean hasSavedCredentials = configLoginData.hasSavedCredentials();
        saveAuthChkbx.setSelected(hasSavedCredentials);

        if (hasSavedCredentials) {
            if (configLoginData.hasUsername()) {
                usernameField.setText(configLoginData.getUsername());
                usernameField.setDisable(true);
            }

            if (configLoginData.hasPassword()) {
                passwordField.setText(configLoginData.getPassword());
                passwordField.setDisable(true);
            }
            
            if (configLoginData.hasHashKey()) {
                hashKeyField.setText(configLoginData.getHashKey());
                hashKeyField.setDisable(true);
            }

            if (configLoginData.hasToken()) {
                tokenField.setText("Using Previous Token");
                tokenField.setDisable(true);
                getTokenBtn.setDisable(true);
            }
        }

        final LoginType lastLoginType = configLoginData.getLoginType();
        if (lastLoginType != null) { 
            if (LoginType.isGoogle(lastLoginType)) {
                radioGroup.selectToggle(googleRadio);
            } else {
                radioGroup.selectToggle(ptcRadio);
            }
        }
        
        radioGroup.selectedToggleProperty().addListener(listener -> {
            if (saveAuthChkbx.isSelected()) {
                saveAuthChkbx.fire();
            }
        }); 
    }

    /**
     * Event handler for saveAuthChkbx.
     *
     * @param actionEvent event
     */
    private void onSaveAuthChkbxChanged(final ActionEvent actionEvent) {
        final boolean saveCredentials = ((CheckBox) actionEvent.getSource()).isSelected();
        accountManager.setSaveLogin(saveCredentials);
        toggleFields(saveCredentials);
    }

    /**
     * Event handler for loginBtn.
     *
     * @param ignored event
     */
    public void onloginBtnClicked(final ActionEvent ignored) {
        if (hashKeyField.textProperty().isNotEmpty().get()) {
            
            if (ptcRadio.isSelected()) {
                tryPtcLogin();
            } else {
                tryGoogleLogin();
            }
        } else {
            final StringBuilder msg = new StringBuilder();
            msg.append("Since forced update 0.57.4, you can't login using the free API anymore.");
            msg.append("\nIn order to keep using this tool you will need to fill a hash key to authenticate with Niantic servers.");
            msg.append("\nIf you don't have one, please go to Pogodev website to buy one by clicking on \"Get Hashkey\".");

            final Alert alert = new Alert(Alert.AlertType.WARNING, msg.toString(), new ButtonType("Get Hashkey"), ButtonType.OK);
            alert.setTitle("Hashkey Needed");
            alert.setHeaderText("Unfortunately, a hashkey is mandatory");
            alert.showAndWait().ifPresent(response -> {
                if (response.getButtonData().equals(ButtonData.OTHER)) {
                    Browser.openUrl(BOSSLAND_POKEHASH_SITE);
                }
            });
        }
    }
    
    /**
     * Event handler for getTokenBtn.
     *
     * @param ignored event
     */
    private void ongetTokenBtnClicked(final ActionEvent ignored) {
        tokenField.setDisable(false);
        Browser.openUrl(GoogleUserCredentialProvider.LOGIN_URL);
    }

    /**
     * Try to login with Google account.
     */
    private void tryGoogleLogin() {
        final LoginData loginData = new LoginData();

        if (configLoginData.hasToken()) {
            loginData.setToken(configLoginData.getToken());
            loginData.setSavedToken(true);
        } else {
            loginData.setToken(tokenField.getText());
        }

        loginData.setHashKey(hashKeyField.getText());
        loginData.setLoginType(LoginType.GOOGLE_AUTH);

        tryLogin(loginData);
    }

    /**
     * Try to login with PTC account.
     */
    private void tryPtcLogin() {
        final LoginData loginData = new LoginData();

        loginData.setHashKey(hashKeyField.getText());
        loginData.setUsername(usernameField.getText());
        loginData.setPassword(passwordField.getText());
        loginData.setLoginType(LoginType.PTC);

        tryLogin(loginData);
    }

    /**
     * After successful login, open the MainWindow.
     */
    private void openMainWindow() {
        new MainWindowController();
        BlossomsPoGoManager.getPrimaryStage().show();
    }
    
    /**
     * After successful login, open the OldMainWindow.
     */
    private void openOldWindow() {
        BlossomsPoGoManager.getPrimaryStage().hide();
        SwingUtilities.invokeLater(() -> {
            UIHelper.setNativeLookAndFeel();
            BlossomsPoGoManager.setNewMainWindow(new PokemonGoMainWindow(accountManager.getPokemonGo(), true));
            BlossomsPoGoManager.getMainWindow().start();
        });        
    }

    /**
     * Handle enabling and disabling of gui if credentials are saved.
     *
     * @param save save credentials
     */
    private void toggleFields(final boolean save) {
        if (usernameField.getText().isEmpty() || !save) {
            usernameField.setDisable(false);
        } else {
            usernameField.setDisable(true);
        }

        if (passwordField.getText().isEmpty() || !save) {
            passwordField.setDisable(false);
        } else {
            passwordField.setDisable(true);
        }

        if (tokenField.getText().isEmpty() || !save) {
            tokenField.setDisable(false);
        } else {
            tokenField.setDisable(true);
        }
        
        if (hashKeyField.getText().isEmpty() || !save) {
            hashKeyField.setDisable(false);
        } else {
            hashKeyField.setDisable(true);
        }

        getTokenBtn.setDisable(false);
    }

    /**
     * Try to login into Pokémon GO using the provided login credentials.
     *
     * @param loginData login credentials
     */
    private void tryLogin(final LoginData loginData) {
        final BpmResult loginResult = accountManager.login(loginData);

        if (loginResult.isSuccess()) {
            
            //             openMainWindow();
            openOldWindow();
        } else {
            alertFailedLogin(loginResult.getErrorMessage());
        }
    }

    /**
     * Logoff from main app.
     */
    public static void logOff() {

        BlossomsPoGoManager.getMainWindow().setVisible(false);
        BlossomsPoGoManager.getMainWindow().dispose();
        BlossomsPoGoManager.setNewMainWindow(null);
        BlossomsPoGoManager.getPrimaryStage().show();
    }
}
